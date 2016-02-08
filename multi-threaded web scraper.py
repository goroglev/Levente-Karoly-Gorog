# Often the most complex part of writing multi-threaded apps is sync of shared resources. 
# The `threading` module offers sync primitives, such as: semaphores, sync. blocks, events, locks, 
# but it is relatively easy to make a mistake at design time using these primitives. 
# The `queue` module offers a more reliable, more readable way to design multi-threaded apps: 
# shared resources are all managed in a single thread and 
# the `queue` module gives sync access to threads requesting those resources. 

import sys
import pycurl
from io import BytesIO
import re
from queue import Queue, Empty
import threading
import time

# regex for extracting meta keywords from the header of an html document
META_KEYWORDS_REGEX = re.compile('<meta .*?name="keywords" .*?content="(.*?)"', re.IGNORECASE)

# GLOBALS
# -------
# count of the # web pages already processed 
count = 0
# queue with multi-threading support
workqueue = Queue()
# signaling that the queue is empty (reliable)
exitflag = 0

class ScraperThread (threading.Thread):
    """A scraper thread class which is designed to be used for scraping web pages.

    Attributes:
        `queue` - stores the web pages to be scraped as (id, url) tuples with multi-threading support
    """

    def __init__(self, queue):
        threading.Thread.__init__(self)
        self.queue = queue

    def run(self):
        process(self.name)

def process(threadname):
    """A generic process function TB executed by a worker thread
    which accesses shared resources within a work queue.
    Attributes:
        `threadname` - the name of the thread
    """

    # the global `exitflag` signals end of processing when all items from the `workqueue` have been consumed.
    while not exitflag:
        try:
            # get an item from the queue immediately if available, else raise the `Empty` exception 
            (url, id_) = workqueue.get_nowait()
            # scrape and extract relevant info from web page
            extract(url, id_, threadname)
            workqueue.task_done() # signals that a formerly enqueued task has been completed.
                              # `queue.join()` will block until for each item put into the queue
                              # the `queue.task_done()` will be called.
        except Empty as e:
            time.sleep(1)

def extract(url, id_, threadname):
    """Extract info from web page and write it to file.

    Attributes:
        `url` - the url of the web page
        `id_` - the id associated with the url
        `threadname` - the name of the current worker thread
    """
    # global var for counting the # web pages already processed
    global count

    # threading lock
    threadinglock = threading.Lock()
    try:
        html = scrape(url);
        # incr. `count` and print info, synchronized operation
        threadinglock.acquire()
        count += 1
        print(count, url, threadname) # perhaps replace this w/ logging
        threadinglock.release()

        match = META_KEYWORDS_REGEX.search(html)
        if (match):
            # unique lowercase keywords
            keywordstring = match.group(1)
            keywords = set(keyword.lower() for keyword in keywordstring.split(','))
            if len(keywords) > 0:
                threadinglock.acquire()			
                outputfile.write(id_ + ' ' + ' '.join(keywords) + '\n')
                outputfile.flush()
                threadinglock.release()
    except Exception as e: # what can be `pycurl.error`?
        threadinglock.acquire()	
        count += 1;
        # what are `e[0]` and `e[1]`?
        print(count, url, threadname, e) # perhaps replace this w/ logging
        threadinglock.release()

def scrape(url):
    """Scrapes a web page and returns its html code as string.
    
    Attributes:
        `url` - the url of the web page
    """
    # TODO: dig deeper into `pycurl`
#    import pdb; pdb.set_trace()
    buf = BytesIO() # in py 2.x, `StringIO` worked fine. From py 3 PycURL invokes the write callback w/ `byte` arg.
    c = pycurl.Curl()
    c.setopt(pycurl.NOSIGNAL, 1)
    c.setopt(pycurl.CONNECTTIMEOUT, 5)
    c.setopt(c.URL, url)
    c.setopt(c.WRITEDATA, buf)
    c.setopt(c.HTTPHEADER, ['Accept: text/html', 'Accept-Charset: UTF-8'])
    c.perform()
    c.close()
    html = buf.getvalue().decode('utf-8', errors='replace') # `buf.getvalue()` returns a `byte` object which
                                                           # needs to be decoded to string using 'utf-8'
    buf.close()		
    return html

assert len(sys.argv) == 4 and sys.argv[3].isdecimal() # <input file> <output file> <number of threads>

with open(sys.argv[2], mode='a', encoding='utf-8') as outputfile:
    for j in range(int(sys.argv[3])):
        thread = ScraperThread(workqueue)
        thread.start()

    with open(sys.argv[1], mode='r') as inputfile:
        for line in inputfile:
            workqueue.put_nowait(line.strip().split()) # line pattern is '<url>\s<id>\n'

    while not workqueue.empty():
        pass

    exitflag = 1;
    workqueue.join() # will block until for each item `queue.put_nowait()` into the queue
                     # the `queue.task_done()` has been called.
