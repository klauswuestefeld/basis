package basis.brickness;

import java.util.List;

import basis.lang.Producer;


public interface Nature {

	List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef);
	
	<T> T instantiate(Class<T> brick, Class<T> implClass, Producer<T> defaultInstantiator);

}
