import logging

"""message priorities from DEBUG to CRITICAL in increasing priority order
    by default, they are printed to stderr and DEBUG and INFO messages are supressed"""

#    C:\Users\NB-Levente\Desktop\python\tutorial>python logging_demo.py
#    WARNING:root:Warning: config file server.conf not found.
#    ERROR:root:Error in image processing
#    CRITICAL:root:Critical error -- Shutting down
#    What is root? the main module ?

FORMAT = "%(name)s %(asctime)s %(process)d %(thread)s %(pathname)s: %(message)s"
logging.basicConfig(format=FORMAT)

logging.debug('Debug message')
logging.info('Informational message')
logging.warning('Warning: config file %s not found.', 'server.conf')
logging.error('Error in image processing')
logging.critical('Critical error -- Shutting down')

