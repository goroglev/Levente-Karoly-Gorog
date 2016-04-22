import itertools

def split_iter2tuples(iterable, splitlength):
    """Splits iterables to a list of tuples, each (except maybe last) of `splitlength` length."""

    r = list(itertools.zip_longest(*[iter(iterable)] * splitlength))
    if len(r):
        r[-1] = list(filter(lambda x: x is not None, r[-1]))
    return r
