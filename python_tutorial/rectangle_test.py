import unittest
import pickle

from rectangle import Rectangle, grid

class RectangleTest(unittest.TestCase):

    def test_rectangle_getitem(self):
        """Construct a rectangle and apply square gridding to obtain the remaining rectangle 
        after two-element grid"""

        r = Rectangle(width=10, height=3)
        self.assertTrue(4.0 == r[2].width and 3.0 == r[2].height)
    
    def test_rectangle_cache_full(self):
        """Construct a rectangle and apply a square gridding which has more iterations than 
        grid func's cache size. Check if cache is full at the end of the square gridding process."""

        grid.reset_cache(maxsize=8)
        r = Rectangle(width=10, height=3.92)
        c = grid.cache
        self.assertTrue(r[11] and len(c.data) == 8 and (c.hits + c.misses) > 8)

    def test_rectangle_cache_serialization(self):
        """Call `test_rectangle_cache_full()` and serialize `grid.cache`. After that, reset cache, 
        then load it from file (the serialized inst in prev step). Apply same assert on cache as in
        `test_rectangle_cache_full()`."""

        self.test_rectangle_cache_full()
        with open('grid.cache', 'wb') as f:
            pickle.dump(grid.cache, f)
        grid.reset_cache()
        with open('grid.cache', 'rb') as f:
            grid.cache = pickle.load(f)
        print(grid.cache)
        c = grid.cache
        self.assertTrue(len(c.data) == 8 and (c.hits + c.misses) > 8)


# unittest.main()


#
#from collections import deque               # to append fast to the left of a(n ordered) collection
#                                            # to be used in `__call__` method
#
#
#cache = True
#
#
#        if args[0] not in self.dict:        # args[0] is the input rect
#            self.dict[args[0]] = []         # this is associated w/ a deque containing the child rectangles
#        generations = self.dict[args[0]]    # the father of a father of a ... rectangle
#        
#        depth = args[2] - args[1]           # the depth (# iterations) of the child rectangle computation
#        if len(generations) > depth:
#            return generations[depth]   # we have the needed rectangle already computed
#
#        # take the last generation and derive new generations from it
#        last_gen = args[0]
#        if len(generations) > 0:
#            last_gen = generations[len(generations) - 1]
#        new_gen = self.f(last_gen, 0, depth - len(generations)) # this call is e.g. the `__rectangle__` method
#         # this will reverse the order of the new generations of rectangles (needed because of the recursive calls)
#        generations.append(new_gen)
#        print(self.dict)
#
#        return new_gen
#
#
