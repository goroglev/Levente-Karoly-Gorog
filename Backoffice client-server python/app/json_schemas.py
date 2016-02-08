from jsonschema import validate, ValidationError
from datetime import datetime

def str2date(date_string):
    # e.g. date_string -- 2014-10-28T00:00:00Z
    return datetime.strptime(date_string, '%Y-%m-%dT%H:%M:%SZ')

scpp_schema = {
            "type" : "object",
            "properties" : {
                "customerId" : {"type" :"string"},
                "startTime" : {"type" : "string"},
                "endTime" : {"type":"string"},
                "volume" : {"type":"number"}
                }
            }

def validate_scpp_msg(scpp_msg):
    """Validate an scpp msg according to the json schema and common sense (start date < end date)"""

    validate(scpp_msg, scpp_schema)
    startTime = str2date(scpp_msg["startTime"])
    endTime = str2date(scpp_msg["endTime"])
    if startTime >= endTime:
        raise ValidationError("Start time {0} should be before end time {1}".format(scpp_msg["startTime"], scpp_msg["endTime"]))

tariff_schema = {
        "type" : "object",
        "properties" : {
            "startFee": {"type" : "number"},
            "hourlyFee": {"type" : "number"},
            "feePerKWh": {"type" : "number"},
            "activeStarting": {"type" : "string"}
            }
        }

def validate_tariff_msg(tariff_msg, last_active_starting):
    """Validate a tariff msg according to 1) the json schema 
    2) tariff's active starting ts > last active starting ts
    3) tariff's active starting ts must be in the future
    """

    validate(tariff_msg, tariff_schema)
    if last_active_starting is not None and last_active_starting >= tariff_msg['activeStarting']:
        raise ValidationError("activeStarting {0} is before last tariff's active starting date {1}!"
                .format(tariff_msg['activeStarting'], last_active_starting))
    if str2date(tariff_msg['activeStarting']) < datetime.now():
        raise ValidationError("activeStarting {0} cannot be before current time!"
                .format(tariff_msg['activeStarting'], last_active_starting))

