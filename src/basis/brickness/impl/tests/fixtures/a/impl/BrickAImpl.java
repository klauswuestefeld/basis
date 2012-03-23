package basis.brickness.impl.tests.fixtures.a.impl;

import static basis.environments.Environments.my;
import basis.brickness.impl.tests.fixtures.a.BrickA;
import basis.environments.Environment;

class BrickAImpl implements BrickA {
	
	private final Environment _instantiationEnvironment = my(Environment.class);
	
	
	@Override
	public Environment instantiationEnvironment() {
		return _instantiationEnvironment;
	}


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
