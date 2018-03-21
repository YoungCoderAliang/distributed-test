package paxos.test.model.support;

import paxos.test.model.Acceptor;
import paxos.test.model.Proposer;

public class ResourceUtil {
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
    		LogUtil.log("end");
		}
	}
}
