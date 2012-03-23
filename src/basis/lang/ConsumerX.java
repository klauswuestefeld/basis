package basis.lang;


public interface ConsumerX<T, X extends Throwable> {

	void consume(T value) throws X; 

}
