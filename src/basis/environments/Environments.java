package basis.environments;

import basis.lang.ClosureX;

public class Environments {

	private final static ThreadLocal<Environment> _environment = new ThreadLocal<Environment>() { @Override	protected Environment initialValue() { return null;};};

	public static <X extends Throwable> void runWith(Environment environment, ClosureX<X> closure) throws X {
		final Environment previous = current();
		_environment.set(environment);
		try {
			closure.run();
		} finally {
			_environment.set(previous);
		}
	}

	public static <T> T my(Class<T> need) {
		if (need == null) throw new IllegalArgumentException("'need' can't be null.");
		final Environment environment = current();
		if (need == Environment.class) return (T) environment;
		if (environment == null) throw new IllegalStateException("Thread " + Thread.currentThread() + " is not running in an environment. Try inside: Environments.runWith");
		
		final T implementation = environment.provide(need);
		if (null == implementation)	throw new IllegalStateException("Environment failed to provide thread " + Thread.currentThread() + " with implementation for " + need);
		return implementation;
	}

	private static Environment current() {
		return _environment.get();
	}

}