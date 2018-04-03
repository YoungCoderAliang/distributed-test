package distributed.test.zookeeper.java;

public class NodeList {
	private Node head;
	private Node tail;

	public void addNode(Node node) {
		if (node.next != null) {
			throw new RuntimeException();
		}
		if (head == null) {
			this.head = this.tail = node;
		} else {
			for (Node tmp = head; tmp != null; tmp = tmp.next) {
				if (node == tmp) {
					throw new RuntimeException();
				}
			}
			this.tail.next = node;
			this.tail = node;
		}
	}

	public synchronized void reverse() {
		if (head == null || head.next == null) {
			return;
		}
		Node last1 = head;
		Node nextSep = head.next.next;

		head = last1.next;
		Node lastSep = null;
		while (true) {
			// 将第一个的指针，记录为nx
			Node last2 = last1.next;
			if (lastSep != null) {
				// 将交换后，本节的第一个，派到上一节的最后一个
				lastSep.next = last2;
			}
			// 将本节第一个的指针，指向下一节第一个
			last1.next = nextSep;
			// 将本节第二个的指针，指向第一个，本节最后一个，指向原本第一个
			lastSep = last2.next = last1;
			
			
			if (nextSep == null) {
				tail = last1;
				break;
			}
			last1 = nextSep;
			if (last1.next == null) {
				tail = last1;
				break;
			}
			nextSep = nextSep.next.next;
		}
	}

	@Deprecated
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Node tmp = head; tmp != null; tmp = tmp.next) {
			sb.append(tmp.value).append(" ");
		}
		return sb.toString();
	}

	public static class Node {
		private Node next;
		private int value;

		public Node(int v) {
			this.value = v;
		}

		public String toString() {
			return String.valueOf(value);
		}
	}

	public static void main(String[] args) {
		NodeList nl = new NodeList();
		for (int i = 0; i < 10; i++) {
			nl.addNode(new Node(i));
		}
		System.out.println(nl.toString());
		nl.reverse();
		System.out.println(nl.toString());
		nl.addNode(new Node(10));
		nl.addNode(new Node(11));
		System.out.println(nl.toString());
		nl.reverse();
		System.out.println(nl.toString());
	}
}