class Calculator:
    """"Simple calculator with basic operations"""

    def add(self, x, y):
        """returns the sum of x, y
        
        raises ValueError if x or y is not a number
        """

        number_types = (int, float, complex)
        if isinstance(x, number_types) and isinstance(y, number_types):
            import pdb; pdb.set_trace()
            res = x + y
            # debug by print sttmnts
#            print('Value of x: {}'.format(x)); 
#            print('Value of y: {}'.format(y)); 
#            print('Value of result: {}'.format(res))
            return res
        raise ValueError("Expected args are numbers and received ", x, y)
