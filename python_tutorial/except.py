# a)
# File <stdin>, line 1, in ?
# while True print('Hello world')
#               ^ (arrow pointing at the first token after the error)
# SyntaxError: invalid syntax

# b)
# Traceback: last (most recent) call
# File <stdin>, line 1, in ?
# TypeError: unsupported operand type(s) for: 'int' and 'str'


print('c)')
try:
    f = open('10_rules_good_studying.txt')
    s = f.readline()
    i = int(s.strip())
except:
    import sys
    import reprlib
    print(reprlib.repr(sys.exc_info())); print(sys.exc_info()[0].__name__);
    print("unknown error: {0}".format(sys.exc_info()[0])) # sys.exc_info() is a tuple of Exceptions 
#   raise

print('d)') 
# d) default formatting action for {} is str() NOT repr()
try:
    raise Exception('spam', 'spawn')
except Exception as inst:
    print('Exception type: {typ!s}\nInstance args: {args!s}\nstr(instance): {_str}'.format(typ=type(inst), args=inst.args, _str=str(inst)))

print('e)')
class MyException(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)
try:    
    raise MyException(2*2)
except MyException as inst:
    print('An exception {0!r} has occured of value {0!s}'.format(inst))

print('f)')
try:
    raise KeyboardInterrupt()
finally:
    print('Hello world')
