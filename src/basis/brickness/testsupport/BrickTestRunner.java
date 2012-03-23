package basis.brickness.testsupport;

import static basis.environments.Environments.my;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.notification.RunNotifier;

import basis.brickness.Brickness;
import basis.brickness.impl.EagerClassLoader;
import basis.environments.Bindings;
import basis.environments.CachingEnvironment;
import basis.environments.Environment;
import basis.environments.EnvironmentUtils;
import basis.environments.Environments;
import basis.lang.Closure;
import basis.lang.types.Classes;
import basis.languagesupport.JarFinder;
import basis.testsupport.CleanTestRunner;


public class BrickTestRunner extends CleanTestRunner {

	@Override
	protected void invokeTestMethod(final Method method, final RunNotifier notifier) {
		Environments.runWith(createTestEnvironment(), new Closure() { @Override public void run() {
			BrickTestRunner.super.invokeTestMethod(method, notifier);
		}});
	}

	
	
	@Override
	protected TestMethod wrapMethod(Method method) {
		return new TestMethodWithEnvironment(method, getTestClass());
	}


	private class TestInstanceEnvironment implements Environment {

		private Object _testInstance;

		@Override
		public <T> T provide(Class<T> intrface) {
			if (intrface.isAssignableFrom(BrickTestRunner.class))
				return (T)BrickTestRunner.this;
			if (intrface.isAssignableFrom(TestInstanceEnvironment.class))
				return (T)this;
			return provideContribution(intrface);
		}

		private <T> T provideContribution(Class<T> intrface) {
			if (_testInstance == null) throw new IllegalStateException();
				
			for (Field field : _contributedFields) {
				final Object value = fieldValueFor(field, _testInstance);
				if (null == value) {
					assertFieldCantProvide(field, intrface);
					continue;
				}
				if (intrface.isInstance(value))
					return (T)value;
			}
			return null;
		}

		private <T> void assertFieldCantProvide(Field field, Class<T> intrface) {
			if (intrface.isAssignableFrom(field.getType())) {
				throw new IllegalStateException(field + " has not been initialized. You might have to move its declaration up, before it is used indirectly by other declarations.");
			}
		}
		
		private void instanceBeingInitialized(Object testInstance) {
			if (_testInstance != null) throw new IllegalStateException();
			_testInstance = testInstance;
		}
	}

	
	private final Field[] _contributedFields;
	
	
	public BrickTestRunner(Class<?> testClass) throws InitializationError {
		super(independentClassLoader(testClass));
		_contributedFields = contributedFields(getTestClass().getJavaClass());
	}
	
	
	private static Class<?> independentClassLoader(Class<?> testClass) {
		ClassLoader classLoader = new EagerClassLoader(classpath(), testClass.getClassLoader()) {

			@Override
			protected synchronized Class<?> doLoadClass(String name) throws ClassNotFoundException {
//				if (name.indexOf(".impl.") != -1) throw new IllegalStateException("Brick impl loaded in test classloader instead of in its own classloader: " + name);
				return super.doLoadClass(name);
			}

			@Override
			protected boolean isEagerToLoad(String className) {
				if (className.startsWith("sneer.bricks")) return true;
				if (className.startsWith("dfcsantos")) return true;
				return false;
			}
			
		};
			
		try {
			return classLoader.loadClass(testClass.getName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	

	static private URL[] classpath() {
		List<URL> result = new ArrayList<URL>();
		File bin = Classes.classpathRootFor(BrickTestRunner.class);
		result.add(toURL(bin));
		for (URL jar : JarFinder.languageSupportJars(bin))
			result.add(jar);
		for (URL jar : JarFinder.testSupportJars(bin))
			result.add(jar);
		return result.toArray(new URL[0]);
	}

	
	static private URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	
	private static Field[] contributedFields(Class<? extends Object> klass) {
		final ArrayList<Field> result = new ArrayList<Field>();
		while (klass != Object.class) {
			collectContributedFields(result, klass);
			klass = klass.getSuperclass();
		}
		return result.toArray(new Field[result.size()]);
	}

	private static void collectContributedFields(
			final ArrayList<Field> collector,
			final Class<? extends Object> klass) {
		
		for (Field field : klass.getDeclaredFields()) {
			if (field.getAnnotation(Bind.class) == null)
				continue;
			collector.add(field);
		}
	}

	protected static Object fieldValueFor(Field field, Object instance) {
		try {
			field.setAccessible(true);
			return field.get(instance);
		} catch (Exception e) {
			throw new IllegalStateException(e); 
		}
	}

	public void instanceBeingInitialized(Object testInstance) {
		my(TestInstanceEnvironment.class).instanceBeingInitialized(testInstance);
	}

	Environment cloneTestEnvironment(Object... bindings) {
		return createEnvironment(my(TestInstanceEnvironment.class), bindings);
	}

	private Environment createTestEnvironment() {
		return createEnvironment(new TestInstanceEnvironment());
	}
	
	private Environment createEnvironment(TestInstanceEnvironment testEnvironment, Object... bindings) {
		return new CachingEnvironment(
				EnvironmentUtils.compose(
					new Bindings(bindings).environment(),
					testEnvironment,
					Brickness.newBrickContainer()));
	}

	void dispose() {
		((CachingEnvironment)my(Environment.class)).clear();
	}
}
