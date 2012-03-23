package basis.testsupport;

import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.internal.ExpectationBuilder;


public abstract class TestWithMocks extends CleanTestBase {

	private final Mockery _mockery = new JUnit4Mockery();

	protected TestWithMocks() {
		super();
	}

	protected Sequence newSequence(String name) {
		return _mockery.sequence(name);
	}

	protected <T> T mock(Class<T> type) {
		return _mockery.mock(type);
	}

	protected <T> T mock(String name, Class<T> type) {
		return _mockery.mock(type, name);
	}

	protected void checking(ExpectationBuilder expectations) {
		_mockery.checking(expectations);
	}

}