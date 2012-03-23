package basis.brickness.testsupport.tests.foo.impl;

import static basis.environments.Environments.my;
import basis.brickness.testsupport.tests.bar.BarBrick;
import basis.brickness.testsupport.tests.foo.FooBrick;

class FooBrickImpl implements FooBrick {
	
	final BarBrick _bar = my(BarBrick.class);

	@Override
	public BarBrick bar() {
		return _bar;
	}

}
