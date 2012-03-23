package basis.brickness.impl;

import java.lang.reflect.Constructor;
import java.util.List;

import basis.brickness.Nature;
import basis.environments.Bindings;
import basis.environments.CachingEnvironment;
import basis.environments.Environment;
import basis.environments.EnvironmentUtils;
import basis.environments.NonBlockingEnvironment;
import basis.lang.ByRef;
import basis.lang.Producer;


public class BricknessImpl implements NonBlockingEnvironment {
	
	public BricknessImpl(Object... bindings) {
		_brickImplLoader = new BrickImplLoader(_apiClassLoader);
		
		Bindings bindingsEnvironment = new Bindings();
		bindingsEnvironment.bind(this);
		bindingsEnvironment.bind(new BrickSerializationMapperImpl(_apiClassLoader, _brickImplLoader));
		bindingsEnvironment.bind(bindings);
	
		_cache = createCachingEnvironment(bindingsEnvironment);
	}
	
	
	private CachingEnvironment _cache;
	private final BrickImplLoader _brickImplLoader;
	private final ByRef<ClassLoader> _apiClassLoader = ByRef.newInstance();
	
	
	@Override
	public <T> T provide(Class<T> intrface) {
		return _cache.provide(intrface);
	}

	
	/** Returns null instead of blocking if another thread is loading this brick. */
	@Override
	public <T> T provideWithoutBlocking(Class<T> intrface) {
		return _cache.provideWithoutBlocking(intrface);
	}

	
	private CachingEnvironment createCachingEnvironment(Bindings bindings) {
		return new CachingEnvironment(EnvironmentUtils.compose(bindings.environment(), new Environment(){ @Override public <T> T provide(Class<T> brick) {
			return loadBrick(brick);
		}}));
	}
	
	
	private <T> T loadBrick(Class<T> brick) {
		try {
			return tryToLoadBrick(brick);
		} catch (ClassNotFoundException e) {
			throw new BrickLoadingException("Exception loading brick: " + brick + ": " + e.getMessage(), e);
		}
	}

	
	private <T> T tryToLoadBrick(Class<T> brick) throws ClassNotFoundException {
		checkClassLoader(brick);
		
		Class<T> brickImpl = _brickImplLoader.loadImplClassFor(brick);
		return instantiate(brick, brickImpl);
	}
	
	
	private <T> T instantiate(Class<T> brick, final Class<T> brickImpl) {
		List<Nature> natures = _brickImplLoader.naturesFor(brick);
		if (natures.isEmpty())
			return (T)newInstance(brickImpl);
		
		Nature nature = natures.get(0);
		return nature.instantiate(brick, brickImpl,	new Producer<T>() {	@Override public T produce() {
			return (T)newInstance(brickImpl);
		}});
	}

	
	private static <T> T newInstance(Class<?> brickImpl) {
		try {
			Constructor<?> constructor = brickImpl.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (T)constructor.newInstance();
		} catch (Exception e) {
			throw new BrickLoadingException(e.getClass().getSimpleName() + " thrown while trying to instantiate " + brickImpl, e);
		}
	}

	
	private void checkClassLoader(Class<?> brick) {
		if (_apiClassLoader.value == null)
			_apiClassLoader.value = brick.getClassLoader();
		
		if (brick.getClassLoader() != _apiClassLoader.value)
			throw new IllegalStateException("" + brick + " was loaded with " + brick.getClassLoader() + " instead of " + _apiClassLoader.value + " like previous bricks.");
	}

}
