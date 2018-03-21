package paxos.test.model.support;

public class ProposerNumberGenerator {
	private static int number = 0;
	public static int newNumber() {
		return number++;
	}
}
