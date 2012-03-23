package basis.util.concurrent;

import java.util.concurrent.CountDownLatch;


/** Same as a java.util.concurrent.CountDownLatch.class of 1 and which does not throw InterruptedException. Throws IllegalState instead.
 * @see java.util.concurrent.CountDownLatch.class */
public class Latch implements Runnable {

	private final CountDownLatch _delegate = new CountDownLatch(1);

	
	/** Waits for some other thread to open() this latch. If this latch has already been opened, returns immediately. */
	public void waitTillOpen() {
		try {
			_delegate.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	
	/** Opens this latch. */
	@Override
	public void run() {
		open();
	}

	
	/** See waitTillOpen() */
	public void open() {
		_delegate.countDown();
	}

	
	public boolean isOpen() {
		return _delegate.getCount() == 0;
	}

}
