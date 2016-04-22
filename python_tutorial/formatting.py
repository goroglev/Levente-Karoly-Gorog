yummie = 'This {food} is {adjective}!'

from math import pi

db = {'Levi': 190, 'Anna': 35, 'Steve': 6}
guys = 'Levi: {Levi:d}; Anna: {Anna:d}; Steve: {Steve:d}'

print(yummie.format(food='broccoli soup', adjective='yummie'))
print('The value of pi is {!r}.'.format(pi))
print('The value of pi is {:.3f}'.format(pi))
print(guys.format(**db))

