from collections import OrderedDict, namedtuple
from decimal import Decimal

from structt import Struct

class memoize:
    """A decorator class for providing a cache for the function call it is decorating."""

    def __init__(self, f, maxsize=None):
        self.reset_cache(maxsize)
        self.f = f

    def __call__(self, *args):
        if not args in self.cache.data:
            rect = self.f(*args)
            if hasattr(self.cache, 'maxsize') and self.cache.maxsize == len(self.cache.data):
                self.cache.data.popitem(last=False)
            self.cache.data[args] = rect
            self.cache.misses += 1
            return rect
        self.cache.hits += 1
        return self.cache.data[args]

    def reset_cache(self, maxsize=None):
        data=OrderedDict()
        self.cache = Struct(data=data, maxsize=maxsize, hits=0, misses=0)

@memoize
def grid(rect, index, endindex):

    if index == endindex:
        return rect

    if rect == None:
        raise IndexError('rectangle index {0:d} out of range (0..{1:d})'.format(index, endindex))
        
    if rect.width > rect.height:
        rect = Rectangle(rect.width - rect.height, rect.height)
    elif rect.width == rect.height:
        rect = None
    else:
        rect = Rectangle(rect.width, rect.height - rect.width)
    return grid(rect, index + 1, endindex)

class Rectangle:
    """Rectangle class defining `width` and `height` data members, the string representation of an instance\
        and other customized operations."""

    def __init__(self, width, height):
        self.width = Decimal(str(width))
        self.height = Decimal(str(height))

    def _format(self):
        return 'width: {0:4f}, height: {1:4f}'.format(self.width, self.height)

    def __add__(self, other):
        if self.width == other.width:
            return (rectangle(self.width, self.height + other.height), None)
        if self.height == other.height:
            return (rectangle(self.width + other.width, self.height), None)
        if self.width < other.width:
            return (rectangle(self.width, self.height + other.height), rectangle(other.width - self.width, other.height))
        return (rectangle(self.width - other.width, self.height), rectangle(other.width, self.height + other.height))
        
    def __getitem__(self, key):
        """<rectangle inst>[i] returns the i'th remaining rectangle after the original rectangle's surface\
        has been already covered by i square areas. 
        
        In each step, the square with the greatest possible area is chosen to cover the rest of the surface"""

        if not isinstance(key, int):
            raise TypeError("rectangle indices must be int's, not {0}".format(type(key)))
        
        item = grid(self, 0, key)
        c = grid.cache
        return item

Rectangle.__repr__ = Rectangle._format
