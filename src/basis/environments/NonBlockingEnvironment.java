package basis.environments;

public interface NonBlockingEnvironment extends Environment {
	/** Returns null instead of blocking if another thread is resolving this need. */
	<T> T provideWithoutBlocking(Class<T> need);
}