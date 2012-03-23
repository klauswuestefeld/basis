package basis.environments;

import basis.lang.CacheMap;
import basis.lang.Functor;



public class CachingEnvironment implements NonBlockingEnvironment {

	public CachingEnvironment(Environment delegate) {
		_delegate = delegate;
	}

	
	private final CacheMap<Class<?>, Object> _cache = CacheMap.newInstance();
	
	private final Environment _delegate;

	private Functor<Class<?>, Object> _functor = new Functor<Class<?>, Object>(){ @Override public Object evaluate(Class<?> key) {
		return _delegate.provide(key);
	}};;

	
	@Override
	public <T> T provide(Class<T> need) {
		return (T)_cache.get(need, _functor);
	}

	
	/** Returns null instead of blocking if another thread is getting this need. */
	@Override
	public <T> T provideWithoutBlocking(Class<T> need) {
		return (T)_cache.getWithoutBlocking(need, _functor);
	}
	
	
	public void clear() {
		_cache.clear();
	}

}
