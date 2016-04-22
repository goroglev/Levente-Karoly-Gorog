from threading import Thread
import sys
import time
import zipfile

class BackgroundProcess(Thread):

    def __init__(self, inputfilename, zipfilename):
        Thread.__init__(self)
        self.inputfilename = inputfilename
        self.zipfilename = zipfilename

    def run(self):
        zipfile_ = zipfile.ZipFile(self.zipfilename, mode='a', compression=zipfile.ZIP_DEFLATED) # compression 'zipfile.DEFLATED' requires zlib
        zipfile_.write(self.inputfilename)
        time.sleep(5) # sleep 5 secs

assert len(sys.argv) == 3 # input and zip file names are expected
bp = BackgroundProcess(*sys.argv[1:])
bp.start()
print('Waiting for background process to complete...')
bp.join()
print('Background process completed.')
