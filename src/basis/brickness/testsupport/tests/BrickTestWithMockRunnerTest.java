package basis.brickness.testsupport.tests;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Test;

import basis.brickness.testsupport.BrickTestWithMocks;


public class BrickTestWithMockRunnerTest extends BrickTestWithMocks {

	protected boolean _assertIsSatisfiedCalled;
	
	@SuppressWarnings("unused")
	private final Mockery _mockery = new Mockery() {
		@Override
		public void assertIsSatisfied() {
			_assertIsSatisfiedCalled = true;
		}
	};
	
	@Test public void test() {
		assertFalse(_assertIsSatisfiedCalled);
	}
	
	@After public void check() {
		assertTrue(_assertIsSatisfiedCalled);
	}
}
