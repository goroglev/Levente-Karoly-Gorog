def average(numbers):
    """Computes the arithmetic average of a list of numbers.
   
    >>> print(average([10, 20, 30])
    21.0
    """
    return sum(numbers) / len(numbers)

import doctest
doctest.testmod()
