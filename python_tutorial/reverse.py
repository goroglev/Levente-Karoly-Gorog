class Reverse:
    """Iterator class for reversing a sequence"""

    def __init__(self, data):
        self.data = data
        self.index = len(data)

    def __iter__(self):
        return self

    def __next__(self):
        """Returns the previous obj. in the sequence, starting from the last"""

        if self.index == 0:
            raise StopIteration
        self.index = self.index - 1
        return self.data[self.index]

def reverse(data):
    """Generator for yielding a seq in reversed order"""

    for i in range(len(data) - 1, -1, -1):
        yield data[i]
