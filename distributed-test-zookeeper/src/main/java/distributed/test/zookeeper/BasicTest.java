package distributed.test.zookeeper;

import javax.swing.event.HyperlinkEvent.EventType;

import org.apache.zookeeper.Watcher;

public class BasicTest {
	public static void main(String[] args) {
		Watcher w = e -> {
			if (e.getType().equals(EventType.ACTIVATED)) {
				
			}
		};
	}
}
