import threading, zipfile, sys, time

class AsyncZip(threading.Thread):
    """A threading.Thread - derived class for zipping a file asynchronously

    infile - file to be zipped
    outfile - name of the zipfile
    """
    def __init__(self, infile, outfile):
        threading.Thread.__init__(self)
        self.infile = infile
        self.outfile = outfile

    def run(self):
        print('Start zipping {0}...'.format(self.infile))
        zip_file = zipfile.ZipFile(self.outfile, 'w', zipfile.ZIP_DEFLATED)
        zip_file.write(self.infile)
        # time.sleep(5) # sleep 5 secs so it doesn't finish earlier than control is given back to main thread.
        print('Zipping {0} completed.'.format(self.infile))

asynczip = AsyncZip(*(sys.argv[1:]))
# asynczip.run() it's not run but start(!)
asynczip.start()
print('Main program continues to run, waiting for the zipping thread to complete')
asynczip.join()
