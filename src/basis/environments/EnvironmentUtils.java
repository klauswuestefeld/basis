package basis.environments;

import static basis.environments.Environments.my;
import basis.lang.ByRef;
import basis.lang.ClosureX;
import basis.lang.Producer;
import basis.lang.ProducerX;

public class EnvironmentUtils {

	public static Environment compose(final Environment... environments) {
		return new Environment() { @Override public <T> T provide(Class<T> intrface) {
			for (Environment e : environments) {
				final T result = e.provide(intrface);
				if (null != result)
					return result;
			}
			return null;
		}};
	}

	public static <T> T retrieveFrom(Environment environment, final Class<T> need) {
		return produceIn(environment, new Producer<T>() { @Override public T produce() {
			return my(need);
		}});
	}

	public static <T, X extends Throwable> T produceIn(Environment environment, final ProducerX<T, X> producer) throws X {
		final ByRef<T> result = ByRef.newInstance();
		Environments.runWith(environment, new ClosureX<X>() { @Override public void run() throws X {
			result.value = producer.produce();
		}});
		return result.value;
	}
	
}
