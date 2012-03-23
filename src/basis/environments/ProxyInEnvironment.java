package basis.environments;

import static basis.environments.Environments.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import basis.lang.ByRef;
import basis.lang.Closure;
import basis.lang.types.Classes;



public class ProxyInEnvironment implements InvocationHandler {

	
	public static <T> T newInstance(T component) {
		return newInstance(my(Environment.class), component);
	}

	
	public static <T> T newInstance(Environment environment, final T component) {
		final Class<? extends Object> componentClass = component.getClass();
		final ProxyInEnvironment invocationHandler = new ProxyInEnvironment(environment, component);
		Class<?>[] interfaces = Classes.allInterfacesOf(componentClass);
		return (T)Proxy.newProxyInstance(componentClass.getClassLoader(), interfaces, invocationHandler);
	}
	
	
	private final Environment _environment;
	private final Object _delegate;

	
	private ProxyInEnvironment(Environment environment, Object component) {
		_environment = environment;
		_delegate = component;
	}

	
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final ByRef<Object> result = ByRef.newInstance();

		Environments.runWith(_environment, new Closure() { @Override public void run() {
			try {
				result.value = method.invoke(_delegate, args);
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException();
			} catch (IllegalAccessException e) {
				throw new IllegalStateException();
			} catch (InvocationTargetException e) {
				result.value = e.getCause();
			}
		}});
		
		if (result.value == null)
			return null;
		
		if (result.value instanceof Throwable)
			throw (Throwable)result.value;
		
		return result.value;
	}

}