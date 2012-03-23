package basis.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

	static public <K, V> CacheMap<K, V> newInstance() {
		return new CacheMap<K, V>();
	}
	
	private CacheMap() {}
	
	Map<K, Thread> _resolversByKey = new HashMap<K, Thread>();
	
	
	public <X extends Throwable> V get(K key, final ProducerX<V, X> producerToUseIfAbsent) throws X {
		return get(key, new FunctorX<K, V, X>() { @Override public V evaluate(K ignored) throws X { //Optimize Use the same functor instead of creating a new one every time.
			return producerToUseIfAbsent.produce();
		}});
	}

	
	public <X extends Throwable> V get(K key, FunctorX<K, V, X> functorToUseIfAbsent) throws X {
		return get(key, functorToUseIfAbsent, true);
	}

	
	public V get(K key, final V valueToUseIfAbsent) {
		return get(key, new Functor<K, V>() { @Override public V evaluate(K ignored) { //Optimize Use the same functor instead of creating a new one every time.
			return valueToUseIfAbsent;
		}});
	}
	
	
	/** Returns null instead of blocking if another thread is running the functor to resolve this same key. */
	public <X extends Throwable> V getWithoutBlocking(K key, FunctorX<K, V, X> functorToUseIfAbsent) throws X {
		return get(key, functorToUseIfAbsent, false);
	}

	
	private <X extends Throwable> V get(K key, FunctorX<K, V, X> functorToUseIfAbsent, boolean blocking) throws X {
		boolean thisThreadMustResolve = false;
		synchronized (_resolversByKey) {
			V found = get(key);
			if (found != null) return found;
			
			thisThreadMustResolve = volunteerToResolve(key);
		}

		if (thisThreadMustResolve) {
			V resolved = functorToUseIfAbsent.evaluate(key); //Fix: throw the exception in the other threads waiting too.
			synchronized (_resolversByKey) {
				put(key, resolved);
				_resolversByKey.remove(key);
				_resolversByKey.notifyAll();
			};
			return resolved;
		}
		
		if (!blocking) return null;
		
		synchronized (_resolversByKey) {
			while (_resolversByKey.containsKey(key))
				waitWithoutInterruptions(key);
		}
		
		return get(key);
	}


	private boolean volunteerToResolve(K key) {
		Thread resolver = _resolversByKey.get(key);
		
		if (resolver == null) {
			_resolversByKey.put(key, Thread.currentThread());
			return true;
		}

		if (resolver == Thread.currentThread())
			throw new IllegalStateException("The resolution (loading) of " + key + " is being triggered recursively.");
		
		return false;
	}

	
	private void waitWithoutInterruptions(K key) {
		try {
			_resolversByKey.wait();
		} catch (InterruptedException e) {
			String stack = stackGiven(_resolversByKey.get(key));
			throw new IllegalStateException("This thread was interrupted while waiting for another thread to resolve: " + key + "\n>>>>>>Start of other thread's stack:\n" + stack + "\n<<<<<<End of other thread's stack");
		}
	}

	
	private String stackGiven(Thread thread) {
		String result = "";
		for (StackTraceElement element : thread.getStackTrace())
			result += "\n\tat " + element.getClassName() + "."
				+ element.getMethodName() + "(" + element.getFileName()
				+ ":" + element.getLineNumber() + ")";
		return result;
	}

}
