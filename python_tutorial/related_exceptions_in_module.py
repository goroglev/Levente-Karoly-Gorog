class Error(Exception):
    """Base class for exceptions in this module"""
    pass

class InputError(Error):
    """Exception class for tracking errors in input

    Attributes:
        expression - the expression where the error occurred
        message - the message explaining the error
    """

    def __init__(self, expression, message):
        self.expression = expression
        self.message = message

class TransitionError(Error):
    """Exception class for tracking transition attempts which are not allowed.

    Attributes:
        previous -- previous state
        next -- attempted state
        message -- message explaining why the attempted transition is not allowed
    """

    def __init__(self, prev, _next, message):
        self.prev = prev
        self._next = _next
        self.message = message
