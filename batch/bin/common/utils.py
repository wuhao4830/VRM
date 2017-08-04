import os

def MySystem(cmd):
    osret = os.system(cmd)
    osret = (osret >> 8)
    if osret != 0:
        print 'cmd ret %d' % osret
        raise Exception('run cmd %s fail ret %d' % (cmd, osret))

