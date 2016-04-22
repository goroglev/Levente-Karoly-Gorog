import re

header = 'First\tLast\t\tPhone no.\tAddress'
print(header, '\n', '-' * (len(header) + 18))
with open('phonebook.raw') as pbf:
    for entry in pbf:
        firstname, lastname, phone_no, house_no, street = re.split(":? ", entry.strip(), maxsplit=4)
        print('\t'.join([firstname, lastname + ' ' * (15 - len(lastname)), phone_no, house_no, street]))

