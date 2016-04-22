class Error(Exception):
    """Base error class"""

    def __str__(self):
        return 'Exception details: {members}'.format(members=self.args)

class InputError(Error):
    """Exception class tracking errors in input.
    
    Attributes:
        `expr` - expression in which the input error occurred
        `msg`  - message explaining the error
    """

    def __init__(self, expr, msg):
        Error.__init__(self, expr, msg)
        self.msg = msg
        self.expr = expr

class TransitionError(Error):
    """Raised when an operation attempts a state transition which is
    not allowed.

    Attributes:
        `prev` - state at the beginning of the transition
        `_next` - attempted next state
        `msg`  - why the state transition is not allowed
    """

    def __init__(self, prev, _next, msg):
        Error.__init__(self, prev, _next, msg)
        self.prev = prev; self._next = _next; self.msg = msg
