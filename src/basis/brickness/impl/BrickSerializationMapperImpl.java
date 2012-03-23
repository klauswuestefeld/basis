package basis.brickness.impl;

import basis.brickness.BrickSerializationMapper;
import basis.brickness.impl.BrickClassLoader.Kind;
import basis.lang.ByRef;

class BrickSerializationMapperImpl implements BrickSerializationMapper {
	
	private final ByRef<ClassLoader> _apiClassLoader;
	private final BrickImplLoader _brickImplLoader;

	BrickSerializationMapperImpl(ByRef<ClassLoader> apiClassLoader, BrickImplLoader brickImplLoader) {
		_apiClassLoader = apiClassLoader;
		_brickImplLoader = brickImplLoader;
	}

	@Override
	public Class<?> classGiven(String serializationHandle) throws ClassNotFoundException {
		String[] parts = serializationHandle.split(":");
		switch (parts.length) {
		case 2: // api:class
			return apiClassLoader().loadClass(parts[1]);
		case 3: // brick:kind:class
			String brick = parts[0];
			Kind kind = BrickClassLoader.Kind.valueOf(parts[1]);
			String klass = parts[2];
			return loadBrickClass(brick, kind, klass);
		default:
			throw new IllegalStateException();
		}
	}

	private Class<?> loadBrickClass(String brick, Kind kind, String klass) throws ClassNotFoundException {
		switch (kind) {
		case IMPL:
			return _brickImplLoader.loadImplClassFor(brick, klass);
		case LIBS:
			return _brickImplLoader.loadLibClassFor(brick, klass);
		}
		throw new IllegalStateException();
	}

	private ClassLoader apiClassLoader() {
		return _apiClassLoader.value;
	}

	@Override
	public String serializationHandleFor(Class<?> klass) {
		if (klass.isArray() || klass.isPrimitive())
			return klass.getName();
		return classLoaderPrefixFor(klass) + ":" + klass.getName();
	}

	private String classLoaderPrefixFor(Class<?> klass) {
		if (!(klass.getClassLoader() instanceof ClassLoaderForBricks))
			return "api";
		
		ClassLoaderForBricks classLoader = (ClassLoaderForBricks) klass.getClassLoader();
		String brick = classLoader.brickName();
		String kind = classLoader.kind().toString();
		return brick + ":" + kind;
	}

}
