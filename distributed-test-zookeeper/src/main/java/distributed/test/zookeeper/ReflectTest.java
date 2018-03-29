package distributed.test.zookeeper;

public class ReflectTest {
	public static void main(String[] args) {
	    ReflectChild c = new ReflectChild();
	    c.setNum(2);
	    System.out.println(c.getNum());
    }
}
