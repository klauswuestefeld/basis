package basis.brickness.impl.tests;

import static basis.environments.Environments.my;

import org.junit.Assert;
import org.junit.Test;

import basis.brickness.Brickness;
import basis.brickness.impl.tests.fixtures.a.BrickA;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;


public class BricknessEnvironmentTest extends Assert {
	
	Environment _subject = Brickness.newBrickContainer();


	@Test (timeout = 2000)
	public void brickInstantiationPreservesEnvironment() throws Exception {
		Environments.runWith(_subject, new Closure() { @Override public void run() {
			assertSame(_subject, my(BrickA.class).instantiationEnvironment());
		}});
	}

}
