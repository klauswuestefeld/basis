package basis.brickness.impl.tests.fixtures.b;

import basis.brickness.Brick;

@Brick
public interface BrickB {

	ClassLoader classLoader();
	ClassLoader libsClassLoader();

}
