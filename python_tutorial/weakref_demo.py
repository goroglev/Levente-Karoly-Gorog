import weakref, gc

"""demo a weakref use case"""

class A:
    """A wrapper around an arbitrary value
    """
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return str(self.value)

a = A(9.81)
wrt = weakref.WeakValueDictionary() # create / retrieve weakref dict
wrt['primary'] = a # add new item to dict

print(wrt['primary']) # `a` still exists at this point
del a
gc.collect()

print(wrt['primary']) # `a` shouldn't exist at this point
