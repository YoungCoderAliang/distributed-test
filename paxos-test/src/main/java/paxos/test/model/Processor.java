package paxos.test.model;

import paxos.test.model.support.MsgPusher;

public interface Processor {
	void receiveMsg(PaxosMsg msg);
	
	String processorIp();
	
	void setMsgPusher(MsgPusher pusher);

	public MsgPusher getMsgPusher();
}
