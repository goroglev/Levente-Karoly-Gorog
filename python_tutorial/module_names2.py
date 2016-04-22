import sys

class A:
    """Check module names programatically in base and inherited class"""

    def __init__(self):
        print(locals()); print(globals()); print(vars())
        self.module = sys.modules[A.__module__]
