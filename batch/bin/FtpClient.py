from ftplib import FTP
import re
import os
import time
import datetime
import sys
import json

PROJ_HOME = ''
DATADIR = 'data/'
STATDIR = 'status/'
TARGET_FILES = ['POHEAD', 'POITEM', 'RECEIVEHEAD', 'RECEIVEITEM', 'COSTCONFIRMATION', 'ZCMRJDS', 'FPMX', 'FKMX']

def getLatestVer():
    VER = STATDIR + 'latest.ver'
    last = 0
    if os.path.exists(VER):
       last = int(open(VER).read())
    else:
        for f in os.listdir(DATADIR):
            fullFileName = DATADIR + f
            ctime = int(os.path.getctime(fullFileName))
            if last < ctime:
                last = ctime
    return last
        
def saveLatestVer():
    open(STATDIR + 'latest.ver','w').write('{0}'.format(int(time.time())))

def getFiles(ftp):
    latestVer = getLatestVer()
    fileList = []
    for f in ftp.nlst():
        m = re.match(r'(.*?)_(\d+)\.', f)
        if m is None or m.group(1) not in TARGET_FILES:
            continue
        t = time.mktime(datetime.datetime.strptime(m.group(2), "%Y%m%d%H%M%S").timetuple())
        if t < latestVer:
            continue
        fileList.append(f)
    for fname in fileList:
        path = DATADIR + fname              
        f = open(path,'wb')                
        filename = 'RETR ' + fname        
        ftp.retrbinary(filename,f.write) 
    saveLatestVer()
if __name__ == "__main__":
    try:
        cfg = json.loads(open("conf/batch.conf").read())
        gConfig=cfg[cfg['env']]
        PROJ_HOME = gConfig['PROJ_HOME']
        DATADIR = PROJ_HOME+DATADIR
        STATDIR = PROJ_HOME+STATDIR
        ftp = FTP()
        ftp.connect("192.0.6.243") 
        ftp.login("lianshang","lian_shang")
        ftp.cwd("LSH")
        getFiles(ftp)
    except Exception as e:
        print str(e)


