package basis.brickness.testsupport.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import basis.brickness.testsupport.Bind;
import basis.brickness.testsupport.BrickTestWithMocks;
import basis.brickness.testsupport.tests.bar.BarBrick;
import basis.brickness.testsupport.tests.foo.FooBrick;


public class BrickTestTest extends BrickTestWithMocks {
	
	@Bind final BarBrick _bar = new BarBrick() {};
	
	final FooBrick _foo = my(FooBrick.class);
	
	@Test
	public void test() {
		BarBrick other = _foo.bar();
		assertSame(_bar, other);
	}

}
