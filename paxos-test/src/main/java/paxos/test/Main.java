package paxos.test;

import paxos.test.model.Acceptor;
import paxos.test.model.Proposer;
import paxos.test.model.support.LogUtil;
import paxos.test.model.support.MsgPusher;

public class Main {
	// 设定的acceptor的数量
	public static int acceptorAmount = 15;
	// 设定的proposer的数量
	public static int proposerNumber = 15;
	// 用于驱动各个processor的线程数
	public static int pushThreads = acceptorAmount * proposerNumber;
	// 网络基础延迟 
	public static int msgDelayFixed = 100;
	// 网络随机延迟
	public static int msgDelayRandom = 200;
	// proposer 重启round周期
	public static int monitorPeriod = (msgDelayFixed + msgDelayRandom) * 2 * 2 + 1000;
	// 丢包率 1/losePer
	public static int losePer = 7;
	
	public static void main(String[] args) {
		LogUtil.log("start");
	    Acceptor.config = new Acceptor[acceptorAmount] ;
	    for (int i = 0; i < acceptorAmount; i++) {
	    	Acceptor.config[i] = new Acceptor(i);
	    	new MsgPusher(Acceptor.config[i]).start();
	    }
	    Proposer.config = new Proposer[proposerNumber] ;
	    for (int i = 0; i < proposerNumber; i++) {
	    	Proposer.config[i] = new Proposer(i);
	    	new MsgPusher(Proposer.config[i]).start();
	    	Proposer.config[i].startRound();
	    }
    }
}
