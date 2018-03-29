package distributed.test.zookeeper;

public class ReflectChild extends ReflectParent {
	public int getNum() {
		try {
			java.lang.reflect.Field f = ReflectParent.class.getDeclaredField("num");
			f.setAccessible(true);
			Integer num = (Integer) f.get(this);
			return num;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
