from pprint import pprint
from collections import Counter
from itertools import groupby
from datetime import datetime 

def dedup(items, key=None):
    """Removes dups from a seq while maintaining order by creating a generator object.
    
    This approach only works if elements of `items` are hashable.
    Attributes:
        `items` - a seq of items
    """

    seen = set() # elements already seen
    for elem in items:
        val = elem if key is None else key(elem)
        if val not in seen:
            seen.add(val)
            yield elem

l = [1, 2, 3, 1, 2, 34]
print('Original list: ', l, sep='\n')
dedupped_l = list(dedup(l))
print('After deduplication:', dedupped_l, sep='\n')

a = [ {'x':1, 'y':2}, {'x':1, 'y':3}, {'x':1, 'y':2}, {'x':2, 'y':4}]
print('Original data:')
pprint(a)
print('After dedup:')
dedupped_a = list(dedup(a, key=lambda d: (d['x'], d['y'])))
pprint(dedupped_a)

# b) determine the most frequently occurring items in a seq
words = [
'look', 'into', 'my', 'eyes', 'look', 'into', 'my', 'eyes',
'the', 'eyes', 'the', 'eyes', 'the', 'eyes', 'not', 'around', 'the',
'eyes', "don't", 'look', 'around', 'the', 'eyes', 'look', 'into',
'my', 'eyes', "you're", 'under'
]

word_counts = Counter(words)
mostcommon3 = word_counts.most_common(3)
print(type(mostcommon3))
print(mostcommon3)

rows = [
{'address': '5412 N CLARK', 'date': '07/01/2012'},
{'address': '5148 N CLARK', 'date': '07/04/2012'},
{'address': '5800 E 58TH', 'date': '07/02/2012'},
{'address': '2122 N CLARK', 'date': '07/03/2012'},
{'address': '5645 N RAVENSWOOD', 'date': '07/02/2012'},
{'address': '1060 W ADDISON', 'date': '07/02/2012'},
{'address': '4801 N BROADWAY', 'date': '07/01/2012'},
{'address': '1039 W GRANVILLE', 'date': '07/04/2012'},
]

# c) given a seq of data (dicts or instances), iterate over that seq 
# in groups, by one of the data fields.

sortedrows = sorted(rows, key=lambda row: datetime.strptime(row['date'], '%m/%d/%Y'))
for date, items in groupby(sortedrows, key=lambda row: row['date']):
    print(date)
    for item in items:
        print(item)


