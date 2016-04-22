import csv
import sys
from datetime import datetime
import re

class custom_excel(csv.excel):
    """Custom excel class defining field delimiter as ';' instead of ',',
    and newline as '\n' instead of '\r\n'.
    """

    delimiter = ';'
    lineterminator = '\n'

def process(row):
    """Transforms data in a `row`.

    Attributes:
        `row` - a row from a csv file as a dict
    """
    # can't delete namedtuple attributes (immutable data struct),
    # that's why a `dict` is preferred to store a row.
    # Plus namedtuple attributes should be valid python id's and nothing enforces this on csv header names.
   
    # what is the datetime string for timezone offset??? '+1:00', '+1' are both NOT...
    timestamp = datetime.strptime(''.join([row['Date'], ' ', row['Time'].upper()]), '%m/%d/%Y %I:%M%p')
    del row['Date'], row['Time'] # remove fields which are not going to be used
    row['Timestamp'] = timestamp.strftime('%m/%d/%Y %I:%M%p') # add new field

# assert len(sys.argv) == 2 # <.csv file>
# with open(sys.argv[1]) as f:

headers = []
with open('stocks.csv') as f:
    f_csv = csv.reader(f)
    headers_ = next(f_csv)
    # headers_ = [re.sub('[^A-Za-z_]', '_', h) for h in next(f_csv)] # sub non-valid chars in py id's with '_'
    # Row = namedtuple('Row', headers)
    
    # Instead of `namedtuple`, better use `dict`. 
    # More concise and perhaps better for data transformation.
    
    # Will the cursor in the file obj preserve its position after the prev. read operation,
    # or will it be moved to beginning of the file?
    f.seek(0) # It will preserve its position so I have to set it to beginning of file.
    f_csv = csv.DictReader(f)

    data = []
    for row in f_csv:
        process(row) # transforms data in `dict`
        data.append(row)
    
# We have a dataset in `data`.
# Let's define the output headers (conform the fields - old and new - after processing the input data).
headers = []
for header in headers_:
    if header == 'Date':
        headers.append('Timestamp')
    elif header != 'Time':
        headers.append(header)
with open('stockks.csv', 'w') as f:
    f_csv = csv.DictWriter(f, headers, dialect=custom_excel)
    f_csv.writeheader()
    f_csv.writerows(data)

