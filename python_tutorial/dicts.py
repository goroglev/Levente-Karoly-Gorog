prices = {
'ACME': 45.23,
'AAPL': 612.78,
'IBM': 205.55,
'HPQ': 37.20,
'FB': 10.75
}

#!!! `zip(seq1, seq2)` creates an iterator which can be consumed only once.

print('Min:', min(zip(prices.values(), prices.keys())))
print('Max:', max(zip(prices.values(), prices.keys())))


