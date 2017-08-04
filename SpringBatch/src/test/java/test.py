import os
import time, threading
import sched
import sys
import logging
reload(sys)
sys.setdefaultencoding('utf-8')
logging.basicConfig(level=logging.INFO,
                format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                datefmt='%a, %d %b %Y %H:%M:%S',
                filename='logs/base_data-%s.log'%time.strftime("%Y-%m-%d"),
                filemode='w')
schedule = sched.scheduler(time.time, time.sleep)
def runJob(jobName):
	cmd="java Test"
	logging.info("run job: %s,cmd=%s",jobName,cmd)
	tmp=os.system(cmd)
	logging.info("tmp: %s",tmp)
def runArticle():
	while (True):
		jobName='updateSkuJob'
		runJob(jobName)
if __name__ == '__main__':
	try:
		articleThread= threading.Thread(target=runArticle, name='ArticleThread')
		articleThread.start()
	except Exception, e:
		logging.error(e)
		
