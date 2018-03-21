package paxos.test.model;

public class PaxosNV {
	private Integer number;
	private String value;

	public PaxosNV() {

	}

	public PaxosNV(Integer number, String value) {
		this.number = number;
		this.value = value;
	}

	public Integer getNumber() {
		return number;
	}

	public String getValue() {
		return value;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
