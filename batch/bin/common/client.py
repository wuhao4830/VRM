import os,sys
from nshead import *
import traceback
import time
from log import *
import json
import random
import socket
import msgpack


class RpcClient:
    def __init__(self,servername,servers,con_time_out=100,write_time_out=100,read_time_out=1000,retry_times=3):
        self.servername = servername
        self.servers = servers.split(",")
        self.con_time_out = con_time_out
        self.write_time_out = write_time_out
        self.read_time_out = read_time_out
        self.retry_times = retry_times

    def RpcJsonCall(self, req, decode=True):
        req_s = ''
        try:
            req_body = json.dumps(req,ensure_ascii=False).encode('utf-8')
            head = NsHead()
            head.body_len = len(req_body)
            req_s = head.pack() + req_body
        except:
            traceback.print_exc(file=sys.stderr)
            sys.stderr.flush()
            sys.stderr.write('CLIENT ERR : json dump req fail')
            return None
        for try_times in range(0,self.retry_times):
            if len(self.servers) == 0:
                return None
            sock = None
            rand = 0
            while len(self.servers)>0:
                rand = random.randint(0,len(self.servers)-1)
                try:
                    sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM) 
                    sock.settimeout(self.con_time_out/1000.0)
                    (svr,port) = self.servers[rand].split(":")
                    sock.connect((svr, int(port)))
                    break;
                except:
                    WARNING('connect to %s fail'%self.servers[rand])
                    del(self.servers[rand])
                    sock = None
            if sock is None:
                FATAL('talk serve is all error')
                return None
            try:
                sock.settimeout(self.write_time_out/1000.0)
                #sock.settimeout(None)
                sock.sendall(req_s)
                sock.settimeout(self.read_time_out/1000.0)
                time_b = time.time()
                head = NsHead.from_str(sock.recv(36))
                if (not head) or head.magic_num != 0xfb709394:
                    WARNING('nshead read error')
                    return None
                recved = 0
                body = ''
                while recved < head.body_len:
                    time_e = time.time()
                    if (time_e-time_b)*1000 > self.read_time_out:
                        WARNING('Serious Err : socke read time over limit %dms'%self.read_time_out)
                        return None
                    temp = sock.recv(head.body_len-recved)
                    sys.stdout.flush()
                    recved += len(temp)
                    body = body + temp
                if decode:
                    return json.loads(body, encoding='utf-8')
                    #return msgpack.loads(body)
                else:
                    return body
            except:
                FATAL('Serious Err : talk meet error try time=%d'%try_times)
                traceback.print_exc(file=sys.stderr)
                del(self.servers[rand])
        return None
