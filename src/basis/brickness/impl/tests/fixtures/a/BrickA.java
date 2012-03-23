package basis.brickness.impl.tests.fixtures.a;

import basis.brickness.Brick;
import basis.environments.Environment;

@Brick
public interface BrickA {

	Environment instantiationEnvironment();

	ClassLoader classLoader();
	ClassLoader libsClassLoader();
	
}
