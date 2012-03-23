package basis.lang;


public interface ProducerX<T, X extends Throwable> {

	T produce() throws X;
	
}
