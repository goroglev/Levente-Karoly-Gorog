from decimal import *

dec_res = round(Decimal('0.70') * Decimal('1.05'), 2)
float_res = round(.70 * 1.05, 2)
print("Decimal calc: {0}, floating point calc: {1}".format(dec_res, float_res))

print("Decimal('1.0') % Decimal('0.1') = {0}".format(Decimal('1.0') % Decimal('0.1')))
print("1.0 % 0.1 = {0}".format(1.0 % 0.1))

print("sum([Decimal('0.1')] * 10) =", sum([Decimal('0.1')] * 10))
print("sum([0.1] * 10) = ", sum([0.1] * 10))

getcontext().prec = 36
print("Decimal('1') / Decimal('7') =", Decimal('1') / Decimal('7'))
