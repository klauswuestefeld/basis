package basis.environments.tests;

import static basis.environments.Environments.my;

import org.junit.Assert;
import org.junit.Test;

import basis.environments.Environment;
import basis.environments.EnvironmentUtils;
import basis.environments.Environments;
import basis.lang.Closure;


public class EnvironmentsTest extends Assert {
	
	final Object _binding = new Object();
	boolean _ran = false;

	@Test
	public void testMyEnvironment() {
		final Environment environment = environment();
		Environments.runWith(environment, new Closure() { @Override public void run() {
			assertSame(environment, my(Environment.class));
		}});
	}
	
	@Test(expected=IllegalStateException.class)
	public void testMyInUnsuitableEnvironment() {
		EnvironmentUtils.retrieveFrom(
			new Environment() { @Override public <T> T provide(Class<T> intrface) {
				return null;
			}}, Runnable.class);
	}
	
	@Test
	public void testRunWith() {
		Environments.runWith(environment(), new Closure() { @Override public void run() {
			assertEquals(_binding, Environments.my(Object.class));
			_ran = true;
		}});
		assertTrue(_ran);
	}
	
	@Test
	public void testNakedMy() {
		try {
			Environments.my(Object.class);
			fail("No exception happened?!");
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().endsWith(
					"is not running in an environment. Try inside: Environments.runWith"));
		}
	}
	
	private Environment environment(final Object... bindings) {
		return new Environment() { @Override public <T> T provide(Class<T> intrface) {
			if (Object.class == intrface)
//				return intrface.cast(_binding);
				return (T)_binding;
			
			for (Object binding : bindings)
				if (intrface.isInstance(binding))
//					return intrface.cast(binding);
					return (T)binding;
			
			throw new IllegalArgumentException();
		}};
	}

}
