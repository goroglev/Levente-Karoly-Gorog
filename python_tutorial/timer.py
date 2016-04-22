from timeit import Timer

duration = Timer('t = a; a = b; b = t', 'a = 1; b = 2').timeit()
print('Traditional arg swapping: a == {0:d}, b == {1:d}: {2}'.format(1, 2, duration))

duration = Timer('a, b = b, a', 'a = 1; b = 2').timeit()
print('Tuple packing and unpacking: a == {0:d}, b == {1:d}: {2}'.format(1, 2, duration))


