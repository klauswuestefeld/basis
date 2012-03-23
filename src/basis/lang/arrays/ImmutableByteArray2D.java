package basis.lang.arrays;

import java.util.Arrays;

public class ImmutableByteArray2D {

	private final byte[][] _payload;

	public ImmutableByteArray2D(byte[][] bufferToCopy) {
		_payload = copy(bufferToCopy);
	}

	public byte[][] copy() {
		return copy(_payload);
	}

	public byte[] get(@SuppressWarnings("unused") int index) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	private static byte[][] copy(byte[][] original) {
		byte[][] result = new byte[original.length][0];
		
		for (int i = 0; i < original.length; i++)
			result[i] = Arrays.copyOf(original[i], original[i].length);
		
		return result;
	}

}
