def scope_test():
    def local_spam():
        spam = 'local spam'

    def nonlocal_spam():
        nonlocal spam
        spam = 'nonlocal spam'

    def global_spam():
        global spam
        spam = 'global spam'

    spam = 'test spam'
    local_spam()
    print('After local_spam(), spam=`{spam}`'.format(spam=spam))
    nonlocal_spam()
    print('After nonlocal_spam(), spam=`{spam}`'.format(spam=spam))
    global_spam()
    print('After global_spam(), spam=`{spam}`'.format(spam=spam))

scope_test()
print('spam=`{spam}`'.format(spam=spam))
