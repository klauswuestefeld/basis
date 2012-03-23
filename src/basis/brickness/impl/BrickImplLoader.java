package basis.brickness.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import basis.brickness.Brick;
import basis.brickness.BrickConventions;
import basis.brickness.Nature;
import basis.lang.ByRef;
import basis.lang.CacheMap;
import basis.lang.ProducerX;
import basis.lang.types.Classes;


class BrickImplLoader {
	
	private final CacheMap<String, ClassLoaderForPackage> _implClassLoaders = CacheMap.newInstance();
	private final ByRef<ClassLoader> _apiClassLoader;
	
	public BrickImplLoader(ByRef<ClassLoader> apiClassLoader) {
		_apiClassLoader = apiClassLoader;
	}

	<T> Class<T> loadImplClassFor(final Class<T> brick) throws ClassNotFoundException {
		final String brickName = brick.getName();
		return (Class<T>)implClassLoaderFor(brickName).loadClass(implNameFor(brickName));
	}


	protected ClassLoader apiClassLoader() {
		return _apiClassLoader.value;
	}

	private <T> ClassLoaderForPackage newImplClassLoaderFor(Class<T> brick) {
		File path = Classes.classpathRootFor(brick);
		String brickName = brick.getName();
		String implPackage = BrickConventions.implPackageFor(brickName);
		ClassLoader apiClassLoader = brick.getClassLoader();
		ClassLoader libsClassLoader = ClassLoaderForBrickLibs.newInstanceIfNecessary(brick, path, implPackage, naturesFor(brick), apiClassLoader);
		ClassLoader nextClassLoader = libsClassLoader == null ? apiClassLoader : libsClassLoader;
		ClassLoaderForPackage implClassLoader = new ClassLoaderForPackage(brick, path, implPackage, naturesFor(brick), nextClassLoader);
		return implClassLoader;
	}

	List<Nature> naturesFor(Class<?> brick) {
		final Brick annotation = brick.getAnnotation(Brick.class);
		if (annotation == null) throw new BrickLoadingException("Brick '" + brick.getName() + "' is not annotated as such!");

		return naturesImplsFor(annotation.value());
	}
	
	Class<?> loadImplClassFor(String brick, String klass) throws ClassNotFoundException {
		return implClassLoaderFor(brick).loadClass(klass);
	}

	private ClassLoaderForPackage implClassLoaderFor(final String brickName) throws ClassNotFoundException {
		return _implClassLoaders.get(brickName, new ProducerX<ClassLoaderForPackage, ClassNotFoundException>() {  @Override public ClassLoaderForPackage produce() throws ClassNotFoundException {
			return newImplClassLoaderFor(apiClassLoader().loadClass(brickName));
		}});
	}
	
	Class<?> loadLibClassFor(String brick, String klass) throws ClassNotFoundException {
		return libClassLoaderFor(brick).loadClass(klass);
	}
	
	private ClassLoader libClassLoaderFor(String brick) throws ClassNotFoundException {
		return implClassLoaderFor(brick)._next;
	}

	private static List<Nature> naturesImplsFor(final Class<? extends Nature>[] natureClasses) {
		final ArrayList<Nature> result = new ArrayList<Nature>(natureClasses.length);
		for (Class<? extends Nature> natureClass : natureClasses)
			result.add(my(natureClass));
		
		return result;
	}
	
	private static String implNameFor(final String brickInterfaceName) {
		return BrickConventions.implClassNameFor(brickInterfaceName);
	}

}
