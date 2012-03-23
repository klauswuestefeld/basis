package basis.brickness.impl.tests.fixtures.b.impl;

import basis.brickness.impl.tests.fixtures.b.BrickB;

public class BrickBImpl implements BrickB {
	
	@Override
	public ClassLoader classLoader() {
		return getClass().getClassLoader();
	}


	@Override
	public ClassLoader libsClassLoader() {
		try {
			Class<?> libClass = classLoader().loadClass("foo.ClassInLib");
			return libClass.getClassLoader();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
