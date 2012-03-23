package basis.testsupport.tests;

import org.junit.Test;

import basis.lang.Functor;
import basis.testsupport.AssertUtils;
import basis.testsupport.PrettyPrinter;


public class PrettyPrinterTest extends AssertUtils {

	@Test
	public void callToStringWithoutRegisteringPrettyPrinters() {
		assertEquals("null", PrettyPrinter.toString(null));
		assertEquals("A", PrettyPrinter.toString('A'));
		assertEquals("0", PrettyPrinter.toString(0));
		assertEquals("a string", PrettyPrinter.toString("a string"));
	}

	@Test
	public void usePrettyPrinterToCapitalizeStrings() {
		Functor<String, String> capitalizer = new Functor<String, String>() { @Override public String evaluate(String string) {
			return string.toUpperCase();
		}};
		PrettyPrinter.registerFor(String.class, capitalizer);
		assertEquals("SOME STRING", PrettyPrinter.toString("some string"));

		Functor<String, String> originalToString = new Functor<String, String>() { @Override public String evaluate(String string) {
			return string;
		}};
		PrettyPrinter.registerFor(String.class, originalToString);
		assertEquals("some string", PrettyPrinter.toString("some string"));
	}

}
