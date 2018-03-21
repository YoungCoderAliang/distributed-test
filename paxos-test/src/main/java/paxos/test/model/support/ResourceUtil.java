package paxos.test.model.support;

import java.util.Date;

import paxos.test.model.Acceptor;
import paxos.test.model.Proposer;

public class ResourceUtil {
	private static Date start;
	public static void start() {
		start = new Date();
	}
	
	public static void release() {
		boolean allStop = true;
		for (Proposer p : Proposer.config) {
			allStop = allStop & p.monitorStopFlag;
		}
		if (allStop) {
			for (Acceptor acc : Acceptor.config) {
				acc.getMsgPusher().stop();
			}
			NetworkMock.es.shutdown();
			long milli = new Date().getTime() - start.getTime();
			long second = milli / 1000;
			LogUtil.log("end. take seconds: " + second);
		}
	}
}
