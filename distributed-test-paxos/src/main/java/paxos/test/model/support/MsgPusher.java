package paxos.test.model.support;

import paxos.test.model.PaxosMsg;
import paxos.test.model.Processor;

public class MsgPusher implements Runnable {
	private Processor processor;
	private Thread t;
	
	private boolean stopFlag = false;

	public MsgPusher(Processor processor) {
		this.processor = processor;
		this.processor.setMsgPusher(this);
	}

	public synchronized void start() {
		if (t == null) {
			t = new Thread(this);
			t.setName(processor.processorIp());
			t.start();
		}
	}

	public void run() {
		while (!stopFlag) {
			PaxosMsg msg = NetworkMock.getMsg(processor.processorIp(), 100);
			if (msg != null) {
				processor.receiveMsg(msg);
			}
		}
	}

	public void stop() {
		stopFlag = true;
    }
}
