import re
import random

def munge(match):
    """returns a word consisting of the same chars as in match.group(1) but in random order"""
    text = match.group(1); length = len(text)
    return ''.join(text[i] for i in random.sample(range(length), length))

sentence = "Professor Abdolmalek, please report your absences promptly."

print(re.sub(r'\B(\w{2,})\B', munge, sentence))
    
