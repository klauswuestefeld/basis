package basis.lang;

public class Objects {

	public static boolean areEqual(Object x, Object y) {
		if (x == null)
			return y == null;
		return x.equals(y);
	}
}
