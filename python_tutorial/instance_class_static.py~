import re

class Date:
    """A class for storing `year`, `month`, `day` fields.
    
    Attributes:
        `year`
        `month`
        `day`
    """
    
    DATE_PATTERN = re.compile(r'(?:0?[1-9]|[1-2]\d|3[01])-(?:0?[1-9]|1[0-2])-(?:[12]\d{3})')

    def __init__(self, year, month, day):
        self.year = year; self.month = month; self.day = day

    def __repr__(self):
        return '-'.join(map(str, [self.day, self.month, self.year]))

    @classmethod
    def parse_string(cls, date_string):
        """Parse `date_string` which is assumed to have the format of
        'dd-mm-yyyy'"
        """
        if not Date.is_valid(date_string):
            raise TypeError("{date_string} is not a valid 'dd-mm-yyyy' date format!".format(date_string=date_string))
        date = Date(*map(int, reversed(date_string.split('-'))))
        return date

    @staticmethod
    def is_valid(date_string):
        """Check if `date_string` is of 'dd-mm-yyyy' form."""
        return re.fullmatch(Date.DATE_PATTERN, date_string) is not None

class SmartDate(Date):
    """Check if `is_valid()` static method is inherited."""

    pass




