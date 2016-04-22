# Create a sequence containing the numbers from 0 to 100. Using the `filter`
# and `map` methods, compute the sum of two times those numbers which are
# divisible by 5.

divisibleby5 = sum(a for a in range(0, 101, 5))
print(divisibleby5)

zero2hundred = [a for a in range(0, 101)]
divisibleby10 = map(lambda num: 2 * num, filter(lambda num: num % 5 == 0, zero2hundred))
print(sum(divisibleby10))



