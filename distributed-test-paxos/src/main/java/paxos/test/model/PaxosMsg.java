package paxos.test.model;

public class PaxosMsg {
	private String from;
	private String to;
	private PaxosMsgType type;
	private PaxosNV nv;
	private Integer requestNumber;
	
	public PaxosMsg copy() {
		PaxosMsg copy = new PaxosMsg();
		copy.from = from;
		copy.to = to;
		copy.type = type;
		copy.nv = nv;
		copy.requestNumber = requestNumber;
		return copy;
	}
	public String getFrom() {
		return from;
	}
	public String getTo() {
		return to;
	}
	public PaxosMsgType getType() {
		return type;
	}
	public PaxosNV getNv() {
		return nv;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public void setType(PaxosMsgType type) {
		this.type = type;
	}
	public void setNv(PaxosNV nv) {
		this.nv = nv;
	}
	/**
	 * @return the requestNumber
	 */
    public Integer getRequestNumber() {
	    return requestNumber;
    }
	/**
	 * @param requestNumber the requestNumber to set
	 */
    public void setRequestNumber(Integer requestNumber) {
	    this.requestNumber = requestNumber;
    }
}
