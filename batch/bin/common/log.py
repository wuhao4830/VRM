import logging
from logging import handlers

__gLoggerND = logging.getLogger('nd')
__gLoggerWF = logging.getLogger('wf')

def INIT_LOG(name, bOpenDebug=False):
    if bOpenDebug:
        __gLoggerND.setLevel(logging.DEBUG)
    else:
        __gLoggerND.setLevel(logging.INFO)
    hl_nd = handlers.WatchedFileHandler('log/%s.log'%name) 
    hl_nd.setFormatter(logging.Formatter(fmt='%(asctime)s %(levelname)s: %(message)s', datefmt='%Y-%m-%d %H:%M:%S'))
    __gLoggerND.addHandler(hl_nd)
    __gLoggerWF.setLevel(logging.WARNING)
    hl_wf = handlers.WatchedFileHandler('log/%s.log.wf'%name) 
    hl_wf.setFormatter(logging.Formatter(fmt='%(asctime)s %(levelname)s: %(message)s', datefmt='%Y-%m-%d %H:%M:%S'))
    __gLoggerWF.addHandler(hl_wf)
    return
    
def DEBUG(logstr):
    __gLoggerND.debug(logstr)
def NOTICE(logstr):
    __gLoggerND.info(logstr)
def ERROR(logstr):
    __gLoggerND.warning(logstr)
def WARNING(logstr):
    __gLoggerWF.error(logstr)
def FATAL(logstr):
    __gLoggerWF.error(logstr)

if __name__ == '__main__':
    INIT_LOG('test', True)
    DEBUG('debug DEBUG')
    NOTICE('debug NOTICE')
    WARNING('debug WARNING')
    FATAL('debug FATAL')
