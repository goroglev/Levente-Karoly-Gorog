import struct

data = 0
with open('test.zip', 'rb') as zipfile:
    data = zipfile.read()

start = 0
for i in range(3): # the first 3 headers
    start = start + 14
    
    fields = data[start:start+16]
    (crc32, comp_size, uncomp_size, filename_size, extra_size) = struct.unpack('<IIIHH', fields) # I - 4 byte unsigned number; H - 2 byte unsigned number; < - standard size (?), little endian byte order (?).
    start = start + 16
    
    filename = data[start:start+filename_size]
    print(filename, hex(crc32), comp_size, uncomp_size)

    start = start + filename_size + extra_size + comp_size # skip to next header

