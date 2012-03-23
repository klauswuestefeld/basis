package basis.lang;

import basis.lang.exceptions.Refusal;


public interface PickyConsumer<T> extends ConsumerX<T, Refusal> {
	
	@Override
	void consume(T value) throws Refusal; 

}
