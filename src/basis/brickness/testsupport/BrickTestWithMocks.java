package basis.brickness.testsupport;

import static basis.environments.Environments.my;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.runner.RunWith;

import basis.environments.Environment;
import basis.testsupport.TestWithMocks;


@RunWith(BrickTestWithMockRunner.class)
public abstract class BrickTestWithMocks extends TestWithMocks {

	{
		my(BrickTestRunner.class).instanceBeingInitialized(this);
	}

	
	protected BrickTestWithMocks() {
		super();
	}

	
	protected Environment newTestEnvironment(Object... bindings) {
		return my(BrickTestRunner.class).cloneTestEnvironment(bindings);
	}

	
	@After
	public void afterBrickTest() {
		my(BrickTestRunner.class).dispose();
	}

	
	@Override
	protected void afterFailedtest(Method method, Throwable thrown) {}

}