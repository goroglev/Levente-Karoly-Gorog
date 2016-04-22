isnt = lambda x: 'is' + (not x) * "n't"

with open('file.txt', 'r') as f:
    print('File <file.txt> ' + isnt(f.closed) + ' closed.')    
    for line in f:
        print(line, end='')
print('File <file.txt> ' + isnt(f.closed) + ' closed.\n')

with open('file.txt', 'r+') as f:
    l = f.readlines()
    print(''.join(l) + '\n')
    f.write('Done ({no_lines:d})?'.format(no_lines=len(l)))

with open('file.txt', 'rb') as f:
    beginstr = f.read(34) # 8-bit string
    print('The first {0:d} characters decoded w/ "latin9": {1:s}'.format(f.tell(), beginstr.decode('latin9')))
    f.seek(10, 1) # jump 10 bytes ahead from curr. pos
    substr = f.read(20)
    curr_pos = f.tell()
    print('Characters ("latin9" decoded) between (includ.) pos. {0} and pos. {1}: {2:s}'.format(curr_pos - 20, curr_pos - 1, substr.decode('latin9')))

# d) Deserialize the pickle byte stream from 'http://www.pythonchallenge.com/pc/def/banner.p' and print the resulting obj on stdout. Jsonify the obj and save it to 'banner.json' then read it in again and display it on stdout.
from urllib.request import urlopen
url = 'http://www.pythonchallenge.com/pc/def/banner.p'
with urlopen(url) as response:
    import pickle
    data = pickle.load(response)

print("Unpickled data  (abbreviated display) from {0}".format(url))
# large, nested container so let's use reprlib
import reprlib
print(reprlib.repr(data))
ascii_art = '\n'.join([''.join([char * count for char, count in line]) for line in data])
print(ascii_art)

import json
with open('banner.json', 'w') as banner:
    json.dump(data, banner)

with open('banner.json', 'r') as banner:
    data = json.load(banner)
    print("Unjsonified data (abbreviated display) from {0}".format(banner.name))
    print(reprlib.repr(data))
ascii_art = '\n'.join([''.join([char*count for char, count in line]) for line in data]) # we can unpack elements of a list too, yay! 
print(ascii_art)
