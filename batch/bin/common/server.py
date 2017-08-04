#encoding=utf-8
'''
 * @file multiprocess.py
 * @author zengwenjun(com@baidu.com)
 * @date 2015/04/29 16:03:23
 * @brief 
 *  
''' 
import os,sys
import signal
import nshead
import struct
import socket
import threading
import time
import traceback
import json
import msgpack



def daemonize(rootdir, stdin='/dev/null',stdout= '/dev/null', stderr= 'dev/null'):
    #Perform first fork.
    try:
        pid = os.fork()
        if pid > 0:
            sys.exit(0)  #first parent out
    except OSError, e:
        sys.stderr.write("fork #1 failed: (%d) %s\n" %(e.errno, e.strerror))
        sys.exit(1)
    os.chdir(rootdir)
    os.umask(0)
    os.setsid()
    try:
        pid = os.fork()
        if pid > 0:
            sys.exit(0) #second parent out
    except OSError, e:
        sys.stderr.write("fork #2 failed: (%d) %s]\n" %(e.errno,e.strerror))
        sys.exit(1)
    for f in sys.stdout, sys.stderr: f.flush()
    si = file(stdin, 'r')
    so = file(stdout,'a+')
    se = file(stderr,'a+',0)
    os.dup2(si.fileno(), sys.stdin.fileno())
    os.dup2(so.fileno(), sys.stdout.fileno())
    os.dup2(se.fileno(), sys.stderr.fileno())

class MultiThreadReqHandler(threading.Thread):

    def __init__(self, threadname, socket, call_func, remhost, remport):
        threading.Thread.__init__(self, name=threadname)
        self.socket = socket
        self.call_func = call_func
        self.remhost = remhost
        self.remport = remport

    def run(self):
        while 1 :
            res_nshead = nshead.NsHead.from_str(self.socket.recv(36))
            if not res_nshead:
                break
            recved = 0
            body = ''
            while recved < res_nshead.body_len:
                temp = self.socket.recv(res_nshead.body_len-recved)
                recved += len(temp)
                body = body + temp
            try:
                mcbody = mcpack.loads(body)
                self.call_func(mcbody)
            except:
                break
        self.socket.close();

class MultiThreadServer(threading.Thread):

    def __init__(self, servername, port, backlog, call_func):
        threading.Thread.__init__(self, name=servername)
        self.port = port
        self.backlog = backlog
        self.call_func = call_func
        self.setDaemon(True)
        self.srvsock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        self.srvsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.srvsock.bind(('', self.port))
        self.srvsock.listen(self.backlog)

    def run(self):
        while 1 : 
             clisock,(remhost,remport) = self.srvsock.accept()
             reqHandler = MultiThreadReqHandler("reciever", clisock, self.call_func, remhost, remport)
             reqHandler.setDaemon(True)
             reqHandler.start()

class MultiThreadServer_ShortConnect:
    def __init__(self, port, backlog, process_num, process_func, child_init_func, process_timeout_s):
        self.port = port
        self.backlog = backlog
        self.child_init_func = child_init_func
        self.process_func = process_func
        self.process_num = process_num
        self.process_timeout_s = process_timeout_s
        self.srvsock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        self.srvsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.srvsock.bind(('', self.port))
        self.srvsock.listen(self.backlog)
        self.childpidlist = []

    def ForkChild(self):
        threading.Thread(target=self.ChildRun).start()

    def BeforeRun(self):
        for i in range(0,self.process_num):
            ret = self.ForkChild()

    def BeforeChildRun(self):
        if self.child_init_func:
            self.child_init_func()
    
    def Run(self):
        while True:
            time.sleep(1)

    def ChildRun(self):
        self.BeforeChildRun()
        while True:
            sys.stdout.flush()
            sys.stderr.flush()
            clisock,(remhost,remport) = self.srvsock.accept()
            print "recv", (remhost,remport)
            while 1:
                try:
                    req_nshead = nshead.NsHead.from_str(clisock.recv(36))
                    if (not req_nshead) or req_nshead.magic_num != 0xfb709394:
                        print 'nshead read error'
                        break
                    recved = 0
                    body = ''
                    while recved < req_nshead.body_len:
                        temp = clisock.recv(req_nshead.body_len-recved)
                        recved += len(temp)
                        body = body + temp
                    req = json.loads(body, encoding='utf-8')
                    resp = {}
                    ret = self.process_func(req, resp)
                    resp["ret"] = ret
                    #mcpack_body = json.dumps(resp, ensure_ascii=False).encode('utf-8')
                    if resp.get('dodump', True):
                        mcpack_body = json.dumps(resp, ensure_ascii=resp.get('ensure_ascii', False)).encode('utf-8')
                        #mcpack_body = msgpack.dumps(resp)
                    else:
                        mcpack_body = resp['dumpstr']
                    head = nshead.NsHead()
                    head.body_len = len(mcpack_body)
                    clisock.sendall(head.pack() + mcpack_body)
                    #print 'ok',mcpack_body
                except:
                    traceback.print_exc(file=sys.stderr)
                    break
                break
            clisock.close();

    def AfterRun(self):
        pass


class MultiProcessServer:
    def __init__(self, port, backlog, process_num, process_func, child_init_func, process_timeout_s):
        self.port = port
        self.backlog = backlog
        self.child_init_func = child_init_func
        self.process_func = process_func
        self.process_num = process_num
        self.process_timeout_s = process_timeout_s
        self.srvsock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        self.srvsock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.srvsock.bind(('', self.port))
        self.srvsock.listen(self.backlog)
        self.childpidlist = []

    def ForkChild(self):
        cid = os.fork()
        if cid == 0:
            #child
            self.BeforeChildRun()
            self.ChildRun()
            sys.exit(0)
        elif cid > 0:
            #father
            self.childpidlist.append(cid)
            print "FORK CHILD SUCC PID %d" % cid
        return cid

    def BeforeRun(self):
        for i in range(0,self.process_num):
            ret = self.ForkChild()
            if ret < 0:
                #error
                self.Exit()
                return -1

    def BeforeChildRun(self):
        self.child_init_func()
        pass
    
    def Run(self):
        while True:
            try:
                result = os.wait()
                pid = result[0]
                ret = result[1]
                if pid == 0:
                    break
                self.childpidlist.remove(pid)
                print "child exit pid %d code %d" % (pid, ret)
                ret = self.ForkChild()
                if ret < 0:
                    sys.stderr.write("Serious Err : FORK CHILD FAIL\n")
            except:
                break

    def ChildRun(self):
        while True:
            signal.signal(signal.SIGALRM, self.ChildTimeOut)
            clisock,(remhost,remport) = self.srvsock.accept()
            signal.alarm(self.process_timeout_s)
            print "recv", (remhost,remport)
            while 1:
                try:
                    res_nshead = nshead.NsHead.from_str(clisock.recv(36))
                    if not res_nshead:
                        break
                    recved = 0
                    body = ''
                    while recved < res_nshead.body_len:
                        temp = clisock.recv(res_nshead.body_len-recved)
                        recved += len(temp)
                        body = body + temp
                    req = mcpack.loads(body)
                    resp = {}
                    ret = self.process_func(req, resp)
                    resp["ret"] = ret
                    head = nshead.NsHead()
                    mcpack_body = mcpack.dumps(resp)
                    head.body_len = len(mcpack_body)
                    clisock.sendall(head.pack() + mcpack_body)
                except:
                    traceback.print_exc(file=sys.stderr)
                    break
                break
            clisock.close();
            signal.alarm(0)

    def ChildTimeOut(self, signum, frame):
        sys.stderr.write("Child TimeOut\n")
        sys.exit(0)

    def AfterRun(self):
        pass

    def Exit(self):
        for pid in self.childpidlist:
            os.kill(pid, 9)


def TEST_PROC(req, resp):
    print "get req ",req
    resp["test"] = "hahaha"
    return 0

if __name__ == "__main__":
    pass
    mp = MultiProcessServer(12567, 1000, 10, TEST_PROC, 10);
    mp.BeforeRun()
    mp.Run()
