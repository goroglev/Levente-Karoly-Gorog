# -*- coding: latin9 -*-

# a) Generator used in summing up cross products of two vectors of equal length.

x = [123, 32, 36]
y = [2, 3, 4]

sum_xy = sum(xi * yi for xi, yi in zip(x, y))

print('Sum of cross products x,y:', sum_xy)

# b) sin table from 0 to 90 degrees
from math import sin, pi
sin_table = {x: sin(x * pi / 180) for x in range(0, 91)}

print('Degree', '|', 'sin(degree)')
for x, sin_x in sin_table.items():
    print(x, '\t', sin_x)

# c) unique words from a page
page = """I do what I can. I make quick choices
and I don't blame myself (retroactively)
for not making the right choices. Moreover,
I forget the self."""

import re
from pprint import pprint

LINE_REGEX = re.compile(r'(.*?)$', flags=re.MULTILINE)
unique_words = set(word.group().lower() for line in LINE_REGEX.finditer(page) for word in re.finditer(r"['\w]+", line.group()))

pprint(unique_words, width=160) 

# d) 
from collections import namedtuple
Student = namedtuple('Student', ['gpa', 'name', 'age'])

students = [Student(gpa=120000, name='Laci', age=21),
            Student(gpa=150000, name='Levi', age=22),
            Student(gpa=194000, name='Lóri', age=20),]
richest = max((student.gpa, student.name) for student in students)
print(richest)
