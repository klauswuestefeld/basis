package basis.lang;


public interface Producer<T> extends ProducerX<T, RuntimeException> {
	
	@Override
	T produce();

}
