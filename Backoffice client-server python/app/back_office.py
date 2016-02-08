from http.server import HTTPServer, BaseHTTPRequestHandler
import threading
from socketserver import ThreadingMixIn
from urllib.request import unquote
import sys

import json
import re
import calendar
from datetime import datetime
from string import Template

from db import TVIDB # own db management class
import user_report_template as urt # user report template for story #3
import json_schemas

class BackOfficeReqHandler(BaseHTTPRequestHandler):
    """Own request handler. 

    Handles POST and GET requests separately and then forwards them to the `handle_request` func.
    `handle_request` delegates the request further to the appropriate end-handler function, 
    based on the request path."""

    def do_POST(self):
        # read post data and then forward the req to the req handler func.
        content_length = int(self.headers['Content-Length'])
        data = json.loads(self.rfile.read(content_length).decode('utf-8'))        
        self.handle_request(data)
    
    def do_GET(self):
        self.handle_request()

    def handle_request(self, data=None):
        """Handles both POST and GET reqs.
        
        In case of GET there's no data. 
        It also extracts data from the url path (regex groups) and passes it to
        the appropriate end-handler func. """
        
        thread_name = threading.current_thread().name
        print(thread_name, self.raw_requestline)
        
        # resolve request path to end-handler function
        # (url) unquote the request path so that eventual unicode codes (%<code>) are converted back to unicode chars
        delegations = [(re.fullmatch(url_pattern, unquote(self.path)), action) 
                for url_pattern, action in BackOfficeReqHandler.REQUEST_HANDLERS.items() 
                if re.fullmatch(url_pattern, unquote(self.path)) is not None]

        # for an existing request path there should be exactly one handler func.
        if len(delegations) == 1:
            delegate = delegations[0]
            args = self,
            if data is not None: # if there is POST data
                args = args + (data,)
            for group in delegate[0].groups(): # if there are more args to be extracted from the request url (e.g. user, month, year)
                args = args + (group,)
            try:
                return delegate[1](*args) # call the appropriate handler func
            finally:
                self.wfile.flush()
        else: # error: page doesn't exist
            self.send_response(404)
            self.end_headers()
            self.wfile.write(str.encode("The requested page {page} is not found!".format(page=self.path), 'utf-8'))
            self.wfile.flush()
            return

    def receive_scpp(self, scpp_msg):
        """Validate the received scpp message against the schema in the spec,
        process scpp POST requests, insert data into the db.        
        """
        try:
            print(scpp_msg)
            json_schemas.validate_scpp_msg(scpp_msg)
        except Exception as exc:
            self.send_response(400)
            self.send_header('Content-type', 'text/plain; charset=utf-8')
            self.end_headers()
            error_msg = "Invalid scpp json object:\n" + str(scpp_msg) + '\n' + str(exc)
            print(error_msg)
            self.wfile.write(str.encode(error_msg, 'utf-8'))
            return

        tvi_db.insert_scpp_msg(scpp_msg)
        self.send_response(200)
        self.end_headers()
        return
    
    def report_scpp(self, filename='scpp.csv', db_streaming_func=TVIDB.get_scpp_msgs):
        """Returns a csv document as a response with all scpp transactions
        
        db_streaming_func -- transactions is streamed one by one directly from the db.
        """
        self.send_response(200)
        self.send_header('Content-type', 'text/csv; charset=utf-8')
        self.send_header('Content-Disposition', 'attachment;filename=' + filename + ';') # download pop-up in the browser
        self.end_headers()
        for record in db_streaming_func(tvi_db): # streaming from the db and write the records one-by-one to the response stream
            self.wfile.write(str.encode(record + '\n', 'utf-8'))

        return

    def report_scpp_cost(self):
        """Similar to `report_scpp` func but with a diff. db_streaming func."""

        return self.report_scpp(filename='scpp_cost.csv', db_streaming_func=TVIDB.get_scpp_msgs_with_costs)

    def report_scpp_user(self, year, month, user):
        """Generate a transaction report for a given user, year, month.

        Constructs the report in 3 steps: beginning part, transactions, end.
        Uses string.Template and substitutes keywords in the templates"""
        
        month_name = calendar.month_name[int(month)]

        # if user doesn't exist or <year><month> is in the future, return 404
        error_msg = ''
        user_exists = tvi_db.user_exists(user)
        if not user_exists:
            error_msg = 'Inexistent user: {user:s}'.format(user=user)
        requested_report_year_month_valid = datetime.now() > datetime(int(year), int(month), 1)
        if not requested_report_year_month_valid:
            error_msg = 'Report date {month_name} {year} is in the future!'.format(month_name=month_name, year=year)
        if not (user_exists and requested_report_year_month_valid):
            self.send_response(404)
            self.send_header('Content-type', 'text/plain; charset=utf-8')
            self.end_headers()
            self.wfile.write(str.encode(error_msg, 'utf-8'))
            return
            
        self.send_response(200)
        self.send_header('Content-type', 'text/plain; charset=utf-8')
        self.end_headers()
        # beginning part of the report
        begin_report = urt.begin_report.substitute(customer=user, month_name=month_name, year=year)
        self.wfile.write(str.encode(begin_report + '\n', 'utf-8'))

        # transactions
        total_cost = 0
        for transact in tvi_db.get_scpp_msgs_per_user(year, month, user):
            transaction = urt.transaction.substitute(start_time=transact[1], end_time=transact[2], energy_volume=transact[3], 
                    transaction_fee=round(transact[4], 2) if transact[4] is not None else '')
            if transact[4] is not None: # total fee of the transaction
                total_cost = total_cost + transact[4]
            self.wfile.write(str.encode(transaction + '\n', 'utf-8'))

        # end report
        end_report = urt.end_report.substitute(total_cost=round(total_cost, 2))
        self.wfile.write(str.encode(end_report + '\n', 'utf-8'))

        return

    def receive_tariff(self, tariff_msg):
        """Validate and process tariff POST requests, insert data into the db."""
        try:        
            json_schemas.validate_tariff_msg(tariff_msg, tvi_db.lastActiveStarting)
        except Exception as exc:
            self.send_response(400) # bad request
            self.send_header('Content-type', 'text/plain; charset=utf-8')
            self.end_headers()
            error_msg = "Invalid tariff json object:\n" + str(tariff_msg) + '\n' + str(exc)
            print(error_msg)
            self.wfile.write(str.encode(error_msg, 'utf-8'))
            return

        tvi_db.insert_tariff_msg(tariff_msg)
        self.send_response(200)
        self.end_headers()
        return

    # A map associating compiled url patterns with request handler funcs
    REQUEST_HANDLERS = { re.compile(url_pattern, re.UNICODE): action for url_pattern, action in 
        {
            '/scpp'                                         :   receive_scpp,       # handling scpp POST
            '/scpp.csv'                                     :   report_scpp,        # scpp csv GET    
            '/scpp_cost.csv'                                :   report_scpp_cost,   # scpp csv with cost GET
            '/invoices/(20\d{2})/(\d{2})/([\w\\.]+)\\.txt'  :   report_scpp_user,   # scpp txt per user per month/year GET
            '/tariff'                                       :   receive_tariff      # tariffs POST
        }.items()
    }

class BackOfficeHTTPServer(ThreadingMixIn, HTTPServer): # `ThreadingMixIn` adds multi-threading to HTTP Server. 
    """Multi-threaded back-office http server."""
    pass

if __name__ == '__main__':
    host = 'localhost'; port = 8080; # for simplicity, define them as constant (makes testing easier)
    try:
        tvi_db = TVIDB('tvi.db') # sqlite db
        http_server = BackOfficeHTTPServer((host, port), BackOfficeReqHandler)
        print('Started backoffice http server on `http://{host}:{port:d}`.'.format(host=host, port=port))
        http_server.serve_forever()
    except KeyboardInterrupt:
        print('^C received, shutting down the backoffice http server')
        http_server.socket.close()
        # close db resources
        tvi_db.cursor.close(); tvi_db.conn.close()
