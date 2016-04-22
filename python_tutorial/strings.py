# -*- coding: utf-8 -*-

a = r'alma\na\nfa\nalatt'
print('Raw string:\n', a)

b = 'alma\na\nfa\nalatt'
print('Interpreted string:\n', b)

print(2 * 'ta' + 'rozott')

multi_lines = ('put several lines within parenthesis '
        'to join them together') 
print(multi_lines)

unicode2 = '\u00B2'
print('unicode2.isdigit()', unicode2.isdigit())
print('unicode2.isdecimal()', unicode2.isdecimal())
print('Python[-1]', 'Python'[-1])

def strip_html(html_str):
    tag = False
    quote = False
    out = ''
    for char in html_str:
        if char == '<' and not quote:
            tag = True
        elif (char == '"' or char == "'") and tag:
            quote = not quote
        elif char == '>' and not quote:
            tag = False
        elif not tag:
            out = out + char
    return out

from urllib.request import urlopen

with urlopen('http://www.tutorialspoint.com/python/string_isdecimal.htm') as res:
        print(strip_html(res.readall().decode('utf-8')))

import string
translation_table = str.maketrans(string.punctuation, ' ' * len(string.punctuation))

def convert_punctuation2space(str):
    return str.translate(translation_table)
