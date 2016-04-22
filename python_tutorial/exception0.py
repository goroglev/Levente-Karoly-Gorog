import sys

for arg in sys.argv[1:]:
    try:
        f = open(arg, 'r')
    except IOError:
        print('Cannot open file {0} for reading!'.format(arg))
    else:
        print('File {0} has {1} lines.'.format(arg, len(f.readlines())))
        f.close()
