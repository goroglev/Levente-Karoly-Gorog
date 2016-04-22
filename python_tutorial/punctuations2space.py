import string

str_ = "Whatever pain I've got in my waist, I'll persevere until I have my well-deserved break."

transTable = str.maketrans(string.punctuation, ' ' * len(string.punctuation))
print('Text with punctuation\n', '-' * 17)
print(str_, '\n')
print('Punctuation replaced by space\n', '-' * 25)
print(str_.translate(transTable))
