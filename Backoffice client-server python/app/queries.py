QUERIES = {
        "CREATE_SCPP"       : """CREATE TABLE IF NOT EXISTS scpp (customerId text, startTime text, 
                                endTime text, volume real, totalFee real)""",
       "CREATE_TARIFFS"     : """CREATE TABLE IF NOT EXISTS tariffs (startFee real, hourlyFee real, 
                                feePerKwh real, activeStarting text)""",

"CREATE_ACTIVE_START_INDEX" : "CREATE INDEX IF NOT EXISTS activeStartingInd ON tariffs (activeStarting ASC)",
    "MAX_ACTIVE_START"      : "SELECT MAX(activeStarting) FROM tariffs",
        "SELECT_TARIFF"     : """SELECT MAX(activeStarting), startFee, hourlyFee, feePerKwh 
                                FROM tariffs WHERE activeStarting <= '{active_start}' 
                                GROUP BY activeStarting ORDER BY activeStarting DESC LIMIT 1""",
            "INSERT_SCPP"   : "INSERT INTO scpp VALUES (?, ?, ?, ?, ?)",
            "SELECT_SCPP"   : "SELECT customerId, startTime, endTime, printf('%.2f', volume) FROM scpp",
"SELECT_SCPP_WITH_TOTALFEE" : """SELECT customerId, startTime, endTime, printf("%.2f", volume), 
                                printf("%.2f", totalFee) FROM scpp""",
        "SELECT_SCPP_USER"  : "SELECT * FROM scpp WHERE customerId='{user}' LIMIT 1",
"SELECT_SCPP_USER_MONTH"    : """SELECT customerId, strftime("%Y-%m-%d %H:%M", startTime), 
                                strftime("%Y-%m-%d %H:%M", endTime), printf("%.2f", volume), totalFee FROM scpp
                                WHERE endTime LIKE '{year}-{month}%' AND customerId LIKE '{user}'
                                ORDER BY startTime""",
        "INSERT_TARIFF"     : "INSERT INTO tariffs VALUES (?, ?, ?, ?)"
}
