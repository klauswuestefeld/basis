package basis.brickness.testsupport.tests.foo;

import basis.brickness.Brick;
import basis.brickness.testsupport.tests.bar.BarBrick;

@Brick
public interface FooBrick {

	BarBrick bar();

}
