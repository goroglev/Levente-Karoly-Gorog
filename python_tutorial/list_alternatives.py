from array import array
from collections import deque
import bisect
from heapq import heapify, heappush, heappop, nsmallest, nlargest
from pprint import pprint

# a) create a py array of 5 unsigned integers, do a sum on it and print the sublist between (including) the 3rd and 5th elements
vec = array('H', [1, 2, 3, 9, 10])
print(sum(vec))
print(vec[2:5])

# b) create a queue of 4 strings, then pop and print the left one, and print the queue after the pop op.
d = deque(["test0", "test2", "test3", "test4"])
print("Retrieving the first element from the queue:", d.popleft())
print(d)

# c) insert a new element into a sorted list of 4 tuples of (<number>, <text>), using the `bisect` module
l = [(0, 'null'), (1, 'one'), (98, 'ninety-eight'), (13263426, "thirteen million two hundred sixty-three thousand four-hundred twenty-six")]
bisect.insort(l, (98.1, "ninety-eight point one"))
print(l)

# d) create a list of 4 elements, print it, then print the 3 smallest values.
# Create fron the list a heap, insert into it a new elem and pop+print the lowest 3 values.
l = [4, 5, 6, -1]; print(l)
print('Lowest 3 elements from orig. list:', nsmallest(3, l))
heapify(l)
heappush(l, 3)
print("popping lowest 3 elems from the heap: {0}, the resulting heap: {1}".format([heappop(l) for i in range(3)], l))

# e) Create a small portfolio dataset (given in the solution file as a list), elements are dicts, 
# e.g. {name:'IBM', 'shares': 100, price: '91.1'}. Pick the 3 cheapest and the 3 most expensive stocks.
portfolio = [
{'name': 'IBM', 'shares': 100, 'price': 91.1},
{'name': 'AAPL', 'shares': 50, 'price': 543.22},
{'name': 'FB', 'shares': 200, 'price': 21.09},
{'name': 'HPQ', 'shares': 35, 'price': 31.75},
{'name': 'YHOO', 'shares': 45, 'price': 16.35},
{'name': 'ACME', 'shares': 75, 'price': 115.65}
]
print('Cheap:')
pprint(nsmallest(3, portfolio, key=lambda stock: stock['price']))
print('Expensive:')
pprint(nlargest(3, portfolio, key=lambda stock: stock['price']))


