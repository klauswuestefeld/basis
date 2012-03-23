package basis.brickness.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import basis.brickness.ClassDefinition;
import basis.brickness.Nature;


abstract class ClassLoaderForBricks extends EagerClassLoader implements BrickClassLoader {
	
	ClassLoaderForBricks(Class<?> brick, URL[] urls, ClassLoader next, List<Nature> natures) {
		super(urls, next);
		_brick = brick;
		_natures = natures;
	}
	
	private final List<Nature> _natures;
	
	private final Class<?> _brick;
	
	@Override
	public String brickName() {
		return _brick.getName();
	}
	
	@Override
	protected Class<?> doLoadClass(String name) throws ClassNotFoundException {
		
		final URL classResource = findResource(name.replace('.', '/') + ".class");
		if (classResource == null) throw new ClassNotFoundException(name);
		
		ClassDefinition originalClassDef;
		try {
			originalClassDef = new ClassDefinition(name, toByteArray(classResource));
		} catch (IOException e) {
			throw new ClassNotFoundException("Invalid URL (" + classResource +")", e);
		}
		List<ClassDefinition> classDefs = realizeNatures(originalClassDef);
		return defineClassesAndReturn(classDefs, name);
	}

	private Class<?> defineClassesAndReturn(List<ClassDefinition> classDefs, String classNameToReturn) throws ClassFormatError {
		Class<?> mainClass = null;
		for (ClassDefinition classDef : classDefs) {
			Class<?> clazz = defineClass(classDef.name, classDef.bytes, 0, classDef.bytes.length);
			if (classDef.name.equals(classNameToReturn)) {
				if (mainClass != null) 
					throw new IllegalStateException();
				mainClass = clazz;
			}
		}
		if (mainClass == null) throw new IllegalStateException();
		return mainClass;
	}

	private List<ClassDefinition> realizeNatures(ClassDefinition originalClassDef) {
		if (!shouldRealizeNatures())
			return Arrays.asList(originalClassDef);
		
		List<ClassDefinition> classDefs = Collections.singletonList(originalClassDef);
		for (Nature nature : _natures)
			classDefs = realizeNature(classDefs, nature);
		return classDefs;
	}

	protected boolean shouldRealizeNatures() {
		return true;
	}

	private List<ClassDefinition> realizeNature(List<ClassDefinition> classDefs, Nature nature) {
		ArrayList<ClassDefinition> resultingDefs = new ArrayList<ClassDefinition>();
		for (ClassDefinition classDef : classDefs)
			resultingDefs.addAll(nature.realize(_brick, classDef));
		return resultingDefs;
		
	}

	public byte[] toByteArray(final URL classResource) throws IOException {
    	InputStream input = classResource.openStream();
    	try {
	        ByteArrayOutputStream output = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024*4];
			int n = 0;
			while (-1 != (n = input.read(buffer))) 
				output.write(buffer, 0, n);
	        return output.toByteArray();
    	}
    	finally {
    		input.close();
    	}
    }
}
