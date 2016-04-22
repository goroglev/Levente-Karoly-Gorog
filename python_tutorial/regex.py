import re

# a) split 'Words, words, words.' by non-word characters.
split = re.split('\W+', 'Words, words, words.')
print(split)

# b) include the part inbetween words in the resulting list.
split = re.split('(\W+)', 'Words, words, words.')
print(split)

# c) split '0a3B9' by non-digit
split = re.split('(?i)[a-f]+', '0a3B9')
print(split)

# d) split '...words, words...' so that at beginning and end an empty
# string will be included in the result list
split = re.split('(\W+)', '...words, words...')
print(split)

# e) replace every two dashes ('--') by one ('-') and every single dash (' ') w/
# a space (' ') in the following string: 'pro----gram-files', using `re.sub` and
# a function for `repl`
programfiles = 'pro----gram-files'
res = re.sub('-{1,2}', lambda match: '-' if match.group(0) == '--' else ' ', programfiles)
print(res)

# f) extract a list of words starting w/ 'f' from this sentence: 'which foot or hand fell fastest'
fwords = re.findall(r'\bf\w+', 'which foot or hand fell fastest')
print(fwords)

# g)  replace every repetition (the same word twice) w/ the word just one time: 'cat in the the drawers'
cats = 'cats in the the drawers'
neatcats = re.sub(r'\b(\w+) \1', r'\1', cats)
print(neatcats)

# h) match the first and last name of Malcolm Reynolds. Using string group id's
m = re.match('(?P<first_name>\w+) (?P<last_name>\w+)', 'Malcolm Reynald')
print(m.group('first_name', 'last_name'))
print(m.groupdict())

# i) match every two char of 'hogyha szel fujna hozzam' w/ the same group and retrieve the first group
m = re.match('(.{5})+', 'Hogyha szel fujna hozzam')
print(m.group(1))

# j) remove 'removethis' from e-mail addresses
m = re.search('(removethis)', 'tarajos.goetremovethishe@gmail.com')
print(m.string[:m.start(1)] + m.string[m.end(1):])
