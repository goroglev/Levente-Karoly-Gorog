from string import Template

t = Template("${village}folk send $$10 to ${cause}")
s = t.substitute(village='Nottingham', cause='flood')
print(s)

import time, os.path
photofiles = ['img_1074.jpg', 'img_1076.jpg', 'img_1077.jpg']

class BatchRename(Template):
    delimiter = '%'

fnpattern = input("Enter filename pattern (%d-date, %s-seqnum, %f-format): ") #  %d%s%f

br = BatchRename(fnpattern)

date = time.strftime('%d%b%y')
for i, filename in enumerate(photofiles):
    base, ext = os.path.splitext(filename)
    print(filename + " -> " + br.substitute(d=date, s='_'+str(i), f=ext))
