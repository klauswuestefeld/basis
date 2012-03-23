package basis.lang;

/** A Runnable that can be used polimorphically in places that use a ClosureX.*/
public interface Closure extends ClosureX<RuntimeException>, Runnable {
	
	@Override
	void run();

}
