import reprlib
superabraca = set('supercalifragilisticexpialidocious')
print(reprlib.repr(superabraca))

import pprint # pretty printer
nested_array =  [[[['cyan', 'magenta'], 'green', 'opaque'], ['orange', 'rose']], 'blue']
pprint.pprint(nested_array, width=60) # no built-in `print` method needed, pprint does itself the job
pprint.pprint(nested_array, width=40)

import textwrap
doc = """the wrap() method is like fill except that
it returns a list of strings instead of a large string including
newlines to separate the lines"""
print(textwrap.fill(doc, width=40))
print(textwrap.wrap(doc, width=40))
