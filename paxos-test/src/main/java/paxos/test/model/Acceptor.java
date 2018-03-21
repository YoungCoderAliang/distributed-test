package paxos.test.model;

import paxos.test.model.support.MsgPusher;
import paxos.test.model.support.NetworkMock;

public class Acceptor implements Processor {
	public static Acceptor[] config;
	private String ip;
	private int maxProposer = -1;
	private Integer acceptedProposal;
	private String acceptedValue;
	
	private MsgPusher pusher;

	public Acceptor(int index) {
		ip = "acceptor_" + index;
	}

	public void receiveMsg(PaxosMsg msg) {
		if (msg.getType().equals(PaxosMsgType.prepare)) {
			if (msg.getNv().getNumber() > maxProposer) {
				maxProposer = msg.getNv().getNumber();
				PaxosMsg ret = new PaxosMsg();
				ret.setFrom(ip);
				ret.setTo(msg.getFrom());
				ret.setType(PaxosMsgType.promise);
				ret.setNv(new PaxosNV(acceptedProposal, acceptedValue));
				ret.setRequestNumber(msg.getNv().getNumber());
				NetworkMock.sendMsg(ret);
			}
		} else if (msg.getType().equals(PaxosMsgType.acceptRequest)) {
			if (msg.getNv().getNumber() >= maxProposer) {
				acceptedProposal = maxProposer = msg.getNv().getNumber();
				acceptedValue = msg.getNv().getValue();
			}
			PaxosMsg ret = new PaxosMsg();
			ret.setFrom(ip);
			ret.setTo(msg.getFrom());
			ret.setType(PaxosMsgType.accept);
			ret.setRequestNumber(msg.getNv().getNumber());
			ret.setNv(new PaxosNV(maxProposer, null));
			NetworkMock.sendMsg(ret);
		} else {
		}
	}

	public String processorIp() {
		return ip;
	}

	public void setMsgPusher(MsgPusher pusher) {
	    this.pusher = pusher;
    }

	public MsgPusher getMsgPusher() {
	    return pusher;
    }
}
