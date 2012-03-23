package basis.testsupport;

import java.util.HashMap;
import java.util.Map;

import basis.lang.Functor;


public class PrettyPrinter {

	private static Map<Class<?>, Functor<?, String>> _printersByType = new HashMap<Class<?>, Functor<?,String>>();

	/** Calling PrettyPrinter.toString(object) without registering a pretty printer for the object's type is the same as invoking String.valueOf(object) */
	public static <T> String toString(T object) {
		String result = String.valueOf(object);
		if (object == null) return result;

		Functor<?, String> prettyPrinter = _printersByType.get(object.getClass());
		if (prettyPrinter != null)
			result = ((Functor<T, String>) prettyPrinter).evaluate(object);

		return result;
	}
	
	public static <T> void registerFor(Class<T> type, Functor<T, String> prettyPrinter) {
		_printersByType.put(type, prettyPrinter);
	};
	
}
