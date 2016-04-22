import sys

def strip_html(htmlFile):
    isHtmlTag = False
    isQuote = False
    htmlTag = ''
    c = htmlFile.read(1) # read a single char
    # empty string evaluates to False, denoting end of file
    while c: 
        if c == '<' and not isQuote:
            isHtmlTag = True
            htmlTag = '<'
        elif c == '>' and not isQuote:
            isHtmlTag = False
        elif c == '"':
            isQuote = not isQuote
        elif isHtmlTag:
            htmlTag = ''.join([htmlTag, c])
        elif not (isHtmlTag or htmlTag.startswith('<script')):        
            yield c
        c = htmlFile.read(1) 

if len(sys.argv) != 2:
    sys.exit('Usage: python strip_html.py <htmlFilename>')

with open(sys.argv[1]) as htmlFile:
    # strip_html(htmlFile) returns a generator object
    for c in strip_html(htmlFile):
        print(c, end='')
