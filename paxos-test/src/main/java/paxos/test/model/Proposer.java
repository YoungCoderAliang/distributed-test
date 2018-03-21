package paxos.test.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import paxos.test.Main;
import paxos.test.model.support.LogUtil;
import paxos.test.model.support.MsgPusher;
import paxos.test.model.support.NetworkMock;
import paxos.test.model.support.ProposerNumberGenerator;
import paxos.test.model.support.ResourceUtil;

public class Proposer implements Processor {
	public static Proposer[] config;
	private String ip;
	private Date lastStartRoundTime;
	private Integer lastSendProposalNumber;
	private String lastAcceptRequestValue;
	private PaxosStage stage;
	private Set<PaxosMsg> promiseMsg = new HashSet<PaxosMsg>();
	private Set<PaxosMsg> acceptMsg = new HashSet<PaxosMsg>();
	
	private Thread monitor;
	public boolean monitorStopFlag = false;
	private MsgPusher pusher;

	public Proposer(int index) {
		this.ip = "proposer_" + index;
	}

	public void receiveMsg(PaxosMsg msg) {
		if (msg.getRequestNumber() != lastSendProposalNumber || stage.equals(PaxosStage.chose)) {
			return;
		}
		if (msg.getType().equals(PaxosMsgType.promise)) {
			if (stage.equals(PaxosStage.accept)) {
				return;
			}
			promiseMsg.add(msg);
			// 收到的响应达到多数
			if (majority(promiseMsg.size())) {
				// 如果有 accept ，找到最大序号的
				PaxosNV max = null;
				for (PaxosMsg promise : promiseMsg) {
					if (promise.getNv().getNumber() != null) {
						if (max == null) {
							max = promise.getNv();
						} else {
							if (promise.getNv().getNumber() > max.getNumber()) {
								max = promise.getNv();
							}
						}
					}
				}
				lastAcceptRequestValue = null;
				if (max == null) {
					// 如果没有 accept ，使用自己作为值
					lastAcceptRequestValue = ip;
				} else {
					// 如果有 accept ，找到最大序号的对应的值
					lastAcceptRequestValue = max.getValue();
				}
				stage = PaxosStage.accept;
				PaxosMsg acceptRequest = new PaxosMsg();
				acceptRequest.setFrom(ip);
				acceptRequest.setType(PaxosMsgType.acceptRequest);
				acceptRequest.setNv(new PaxosNV(lastSendProposalNumber, lastAcceptRequestValue));
				for (Acceptor acc : Acceptor.config) {
					PaxosMsg toAcc = acceptRequest.copy();
					toAcc.setTo(acc.processorIp());
					NetworkMock.sendMsg(toAcc);
				}
			}
		} else if (msg.getType().equals(PaxosMsgType.accept)) {
			if (stage.equals(PaxosStage.prepare)) {
				return;
			}
			if (msg.getNv().getNumber() > lastSendProposalNumber) {
				startRound();
			} else {
				acceptMsg.add(msg);
				if (majority(acceptMsg.size())) {
					stage = PaxosStage.chose;
					chose();
				}
			}
		} else {
		}
	}

	private void chose() {
		LogUtil.log("find chose : " + lastAcceptRequestValue);
		pusher.stop();
		monitorStopFlag = true;
		ResourceUtil.release();
    }

	private boolean majority(int amount) {
		return new BigDecimal(amount).compareTo(new BigDecimal(Acceptor.config.length).divide(new BigDecimal(2))) > 0;
	}

	public void startRound() {
		lastStartRoundTime = new Date();
		stage = PaxosStage.prepare;
		promiseMsg.clear();
		acceptMsg.clear();
		lastSendProposalNumber = ProposerNumberGenerator.newNumber();
		PaxosMsg msg = new PaxosMsg();
		msg.setFrom(ip);
		msg.setType(PaxosMsgType.prepare);
		msg.setNv(new PaxosNV(lastSendProposalNumber, null));
		for (Acceptor acc : Acceptor.config) {
			PaxosMsg toAcc = msg.copy();
			toAcc.setTo(acc.processorIp());
			NetworkMock.sendMsg(toAcc);
		}
		
		if (monitor == null) {
			monitor = new Thread(new Runnable() {
				public void run() {
	                while (!monitorStopFlag) {
	                	Date now = new Date();
	                	if (now.getTime() - lastStartRoundTime.getTime() > Main.monitorPeriod) {
	                		// 30秒没有完成表决，重新进入round
	                		LogUtil.log("newround");
	                		startRound();
	                	}
	                	try {
	                        Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
	                }
                }
			});
			monitor.setName(ip + " monitor");
			monitor.start();
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
