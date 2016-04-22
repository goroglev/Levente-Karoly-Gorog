def test_spam():

    def local_spam():
        spam = 'local spam'

    def nonlocal_spam():
        nonlocal spam
        spam = 'nonlocal_spam'


    def global_spam():
        global spam
        spam = 'global spam'

    spam = 'spam'

    for spam_fn in [local_spam, nonlocal_spam, global_spam]:
        spam_fn()
        print(spam_fn.__name__, spam)

test_spam()
print("And finally, after all these func calls, the value of `spam` is '{spam}'".format(spam=spam))
