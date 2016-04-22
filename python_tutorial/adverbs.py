import re

text = "He was carefully disguised but captured quickly by police."
print(text, '\n\nAdverbs:\n----------')
for m in re.finditer(r'\w+ly', text): # every occurence (match, position) of '\w+ly' pattern in text
    print('{adverb} at position {pos}'.format(adverb=m.group(), pos=m.start()))
