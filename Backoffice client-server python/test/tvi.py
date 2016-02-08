import unittest

from urllib.request import Request, quote, urlopen
from urllib.error import HTTPError

from datetime import datetime, timedelta
import json

from app.scpp import scppMsg, iso8601ZFormat

class Tvi(unittest.TestCase):

    def setUp(self):
        self.server_url = 'http://localhost:8080/'

    # USER INVOICE
    def test_user_invoice_with_unicode_user_name(self):
        """Request user invoice report where user names contain unicode chars in the url."""
        user_invoice_url_with_unicode_chars = self.server_url + 'invoices/2015/07/' + quote(u'cédric') + '.txt'
        response = urlopen(user_invoice_url_with_unicode_chars) # returns http.client.HTTPResponse
        self.assertEqual(200, response.getcode())

    def test_user_invoice_with_non_existing_user(self):
        """Request an invoice report for a non-existing user w/ unicode chars.
        
        Check if the server returns 404 and <error_msg>.
        """
        user = u'péter'
        non_user_invoice_url_w_unicode_chars = '{0}invoices/2015/07/{1}.txt'.format(self.server_url, quote(user))
        with self.assertRaises(HTTPError): # context object; expect that the code block within raises HTTPError
            try:
                 response = urlopen(non_user_invoice_url_w_unicode_chars)
            except HTTPError as he:
                first_line = he.file.readline().decode('utf-8'); 
                inexistent_user = 'Inexistent user: ' + user
                import re; 
                if re.search(inexistent_user, first_line) is not None: # if 1st line from server error msg
                    # contains 'Inexistent user: <user>', throw forward the caught exception
                    raise he
                else: # else raise an exception that expected error message is not what we got
                    raise Exception('Expected `{0}`,\ngot `{1}`'.format(inexistent_user, first_line))

    # SCPP MESSAGE
    def test_scpp_post_with_customerId_as_number(self):
        """Post an invalid json scpp msg to the server, where customerId is a number instead of a string"""
        
        end_time = datetime.now()
        start_time = end_time - timedelta(0, 1800) # end time - 1800 sec
        scpp_req = self.scpp_req(12355, start_time, end_time, 85)
        self.assertRaises(HTTPError, urlopen, scpp_req)
    
    def test_scpp_post_with_start_date_after_end_date(self):
        """Post an invalid json scpp msg to the server, where start date is after end date"""
        
        end_time = datetime.now()
        start_time = end_time - timedelta(0, 1800) # end time - 1800 sec
        scpp_req = self.scpp_req('pete', end_time, start_time, 85) # end_time as start time and vice versa ;-)
        self.assertRaises(HTTPError, urlopen, scpp_req)

    def test_valid_scpp_post(self):
        """Post a valid json scpp msg to the server"""
        
        end_time = datetime.now()
        start_time = end_time - timedelta(0, 1800) # end time - 1800 sec
        scpp_req = self.scpp_req('pete', start_time, end_time, 85)
        response = urlopen(scpp_req)
        self.assertEquals(200, response.getcode())

    # TARIFF MESSAGE
    def test_tariff_post_activeStarting_before_last_entry_activeStarting_in_db(self):
        """Post an invalid tariff msg to the server where activeStarting timestamp is
        before activeStarting of last entry in the `tariffs` table.
        """
        
        tariff_req = self.tariff_req(.2, 1, .25, "1984-06-11T00:00:00Z")
        self.assertRaises(HTTPError, urlopen, tariff_req)

    def test_tariff_post_activeStarting_after_last_activeStarting_before_current_date(self):
        """Post an invalid tariff msg to the server where activeStarting timestamp is after the
        greatest activeStarting in the `tariffs` table but before current date
        """
        
        tariff_req = self.tariff_req(.2, 1, .25, "2015-08-09T00:00:00Z")
        self.assertRaises(HTTPError, urlopen, tariff_req)

    def test_valid_tariff_post(self):
        """Post a valid tariff msg to the server"""
        
        tariff_req = self.tariff_req(.2, 1, .25, iso8601ZFormat(datetime.now() + timedelta(0, 20))) # activeStarting = current time + 20 sec
        response = urlopen(tariff_req)
        self.assertEqual(200, response.getcode())

    # CHARGE COST CALCULATION
    def test_charge_session_cost_calculation(self):
        """Test if charge session cost is calculated correctly.

        current (reference) time = 0 sec
        Post a tariff to the server with validity starting in +5 sec.
        Post a scpp message to the server for user `pete` with startTime=+10 sec.
        Check the charge cost for user 'pete' for session with startTime=+10 sec."""
        
        now = datetime.now() # reference time

        # tariff
        tariff_req = self.tariff_req(5.2, 1, .25, iso8601ZFormat(now + timedelta(0, 5))) # tariff activeStarting=ref. time + 2 sec
        response = urlopen(tariff_req)
        self.assertEqual(200, response.getcode())

        # scpp
        five_sec_later = now + timedelta(0, 5)
        half_hr_five_sec_later = now + timedelta(0, 1803)
        scpp_req = self.scpp_req('pete', five_sec_later, half_hr_five_sec_later, 100) 
        response = urlopen(scpp_req)
        self.assertEqual(200, response.getcode())

        # verify the charge cost for `pete` for the above session
        charge_session_info_pete = 'from {startTime} to {endTime}: 100.00 kWh @ 30.7\n'.format(startTime=five_sec_later.strftime('%Y-%m-%d %H:%M'), endTime=half_hr_five_sec_later.strftime('%Y-%m-%d %H:%M'))
        print(charge_session_info_pete)

        month = five_sec_later.month
        if month < 10:
            month = '0' + str(month)

        pete_invoice_url = self.server_url + 'invoices/{year}/{month}/pete.txt'.format(year=five_sec_later.year, month=month)
        with urlopen(pete_invoice_url) as response:
                while 1:
                    line = response.readline().decode('utf-8')
                    self.assertTrue(line != '') # EOF
                    if line == charge_session_info_pete:
                        return # success!i

    def scpp_req(self, user, startTime, endTime, volume):
        """Create a scpp POST request with scpp msg as POST data"""

        scpp_msg = scppMsg(user, startTime, endTime, volume)
        return Request(url=self.server_url + 'scpp', data=str.encode(scpp_msg, 'utf-8'), headers={'Content-Type': 'application/json'})

    def tariff_req(self, startFee, hourlyFee, feePerKwh, activeStarting):
        """Create a tariff POST request with tariff msg as POST data"""

        tariff_msg = json.dumps({
            "startFee": startFee,
            "hourlyFee": hourlyFee,
            "feePerKwh": feePerKwh,
            "activeStarting": activeStarting
            })
        return Request(url = self.server_url + 'tariff', data=str.encode(tariff_msg, 'utf-8'), headers={'Content-Type': 'application/json'})
