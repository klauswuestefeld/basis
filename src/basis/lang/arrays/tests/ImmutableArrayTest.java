package basis.lang.arrays.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import basis.lang.arrays.ImmutableArray;
import basis.testsupport.AssertUtils;
import basis.testsupport.CleanTestBase;

public class ImmutableArrayTest extends CleanTestBase {

	@Test
	public void iterable() {
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(Arrays.asList(1, 2, 3));
		AssertUtils.assertContents(array, 1, 2, 3);
	}

	@Test
	public void immutable() {		
		final List<Integer> original = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);

		original.add(4);
		AssertUtils.assertContents(array, 1, 2, 3);
	}

	@Test
	public void immutableUsingAlternateConstructor() {		
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);

		original[2] = 4;
		AssertUtils.assertContents(array, 1, 2, 3);
	}

	@Test
	public void toArray() {
		Integer[] original = new Integer[] {1,2,3};
		final ImmutableArray<Integer> array = new ImmutableArray<Integer>(original);
		Assert.assertArrayEquals(original, array.toArray());

		array.toArray()[2] = 4;
		Assert.assertArrayEquals(original, array.toArray());
	}

}
