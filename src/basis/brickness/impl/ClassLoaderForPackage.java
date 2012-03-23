package basis.brickness.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import basis.brickness.Nature;


/** Loads only classes from a given package and its subpackages. All other loads are deferred to the next classLoader.*/
class ClassLoaderForPackage extends ClassLoaderForBricks {	

	public ClassLoaderForPackage(Class<?> brick, File classpath, String packageName, List<Nature> natures, ClassLoader next) {
		super(brick, toURLs(classpath), next, natures);
		_package = packageName;
	}
	
	private final String _package;
		
	@Override
	protected boolean isEagerToLoad(String className) {
		return className.startsWith(_package);
	}

	private static URL[] toURLs(File file) {
		try {
			return new URL[]{file.toURI().toURL()};
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return ClassLoaderForPackage.class.getSimpleName()+ ":" + _package + ":" + hashCode();
	}

	@Override
	public Kind kind() {
		return Kind.IMPL;
	}
	
}
