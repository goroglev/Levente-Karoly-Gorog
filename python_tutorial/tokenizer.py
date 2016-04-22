import re
import collections

Token = collections.namedtuple('Token', ['typ', 'value', 'line', 'column'])

TOKENS = [
('NUMBER', r'\d+(?:\.\d*)?'),
('ASSIGN', r':='),
('END', r';'),
('ID', r'[a-zA-Z]+'),
('OP', r'[+\-*/]'),
('NEWLINE', r'\n'),
('SKIP', r'[ \t]+'),
('MISMATCH', r'.+')
]

KEYWORDS = {'IF', 'THEN', 'ENDIF', 'FOR', 'NEXT', 'GOSUB', 'RETURN'}

TOKEN_REGEX = re.compile('|'.join([r'(?P<%s>%s)' % pair for pair in TOKENS]), flags=re.MULTILINE)

def tokenize(code):
    """tokenize a code snippet given the above language elements"""
    line_nr = 1; nr_chars_before = 0
    for m in TOKEN_REGEX.finditer(code):
        kind = m.lastgroup
        value = m.group(kind)
        column = m.start(kind) - nr_chars_before
        if kind == 'NEWLINE':
            line_nr += 1
            nr_chars_before = m.end(kind)
            continue
        elif kind == 'ID' and value in KEYWORDS:
            kind = value
        elif kind == 'SKIP': continue
        elif kind == 'MISMATCH':
            raise RuntimeError('Unknown error on line %d, column %d' %(line_nr, m.start(kind) - nr_chars_before))
        yield Token(typ=kind, value=value, line=line_nr, column=column)

statements = '''
    IF quantity THEN
        total := total + price * quantity;
        tax := price * 0.05;
    ENDIF;'''

for token in tokenize(statements):
    print(token)
