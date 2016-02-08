import sqlite3
from datetime import datetime
import re

from queries import QUERIES # db queries w/ variables
from json_schemas import str2date

class TVIDB:
    
    def __init__(self, db_filename):
        '''init db connection & create tables, indices'''

        self.conn = sqlite3.connect(db_filename, check_same_thread=False)
        self.cursor = self.conn.cursor()
       
        # create tables if they don't exist yet
        for query_str in [QUERIES['CREATE_SCPP'], QUERIES['CREATE_TARIFFS'], 
                QUERIES['CREATE_ACTIVE_START_INDEX']]:
                self.cursor.execute(query_str)
        self.lastActiveStarting = self.cursor.execute(QUERIES['MAX_ACTIVE_START']).fetchone()[0]

    def insert_scpp_msg(self, msg):
        """Inserts an scpp message into the scpp table

        An scpp message consists of (customerId, startTime, endTime, volume) fields.
        msg -- a json object containing (customerId, startTime, endTime, volume) keys with corresponding values.
        """      
        
        # compute the total fee for the charging session
        total_tariff = 0
        tariff = self.cursor.execute(QUERIES['SELECT_TARIFF'].format(active_start=msg['startTime'])).fetchone()
        print('Applicable tariff: {tariff}'.format(tariff=tariff))
        if tariff is not None: # `tariffs` table empty or no applicable tariff
            charge_duration = str2date(msg['endTime']) - str2date(msg['startTime'])
            charge_duration_hr = charge_duration.total_seconds() / 3600.
            total_tariff = tariff[1] + tariff[2]*charge_duration_hr + tariff[3] * msg['volume']

        insert_scpp_query = QUERIES['INSERT_SCPP']
        self.cursor.execute(insert_scpp_query, 
                (msg['customerId'], msg['startTime'], msg['endTime'], msg['volume'], total_tariff))
        self.conn.commit()

    def get_scpp_msgs(self):
        """get *all* scpp transactions from the db including all fields except `totalFee`"""
        for row in self.cursor.execute(QUERIES['SELECT_SCPP']):
            yield ','.join(row)

    def get_scpp_msgs_with_costs(self):
        """get *all* scpp transactions from the db including all fields"""
        for row in self.cursor.execute(QUERIES['SELECT_SCPP_WITH_TOTALFEE']):
            yield ','.join(['' if field is None else str(field) for field in row])

    def user_exists(self, user):
        """verify if `user` has at least one transaction in the `scpp` table"""
        transaction = self.cursor.execute(QUERIES['SELECT_SCPP_USER'].format(user=user)).fetchone()
        return transaction is not None

    def get_scpp_msgs_per_user(self, year, month, user):
        """get scpp transactions from the db per user, year, month"""
        for row in self.cursor.execute(QUERIES['SELECT_SCPP_USER_MONTH'].format(year=year, month=month, user=user)):
            yield row

    def insert_tariff_msg(self, msg):
        """Inserts a tariff message into the tariffs table

        A tariff message -- msg -- consists of (startFee, hourlyFee, feePerKwh, activeStarting) fields.
        """
        self.cursor.execute(QUERIES['INSERT_TARIFF'], 
                (msg['startFee'], msg['hourlyFee'], msg['feePerKwh'], msg['activeStarting']))
        self.conn.commit()
        self.lastActiveStarting = msg['activeStarting']
