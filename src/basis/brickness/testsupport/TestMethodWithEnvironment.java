/**
 * 
 */
package basis.brickness.testsupport;

import static basis.environments.Environments.my;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;

import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.ClosureX;


class TestMethodWithEnvironment extends TestMethod {

	private final Environment _environment;

	public TestMethodWithEnvironment(Method method, TestClass testClass) {
		super(method, testClass);
		_environment = my(Environment.class);
	}

	@Override
	public void invoke(final Object test) throws InvocationTargetException {
		Environments.runWith(_environment, new ClosureX<InvocationTargetException>() { @Override public void run() throws InvocationTargetException {
			doInvoke(test);
		}});
	}

	private void doInvoke(Object test) throws InvocationTargetException {
		try {
			super.invoke(test);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}