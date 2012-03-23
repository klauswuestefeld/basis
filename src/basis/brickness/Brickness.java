package basis.brickness;

import basis.brickness.impl.BricknessImpl;
import basis.environments.Environment;

public class Brickness {

	public static Environment newBrickContainer(Object... bindings) {
		return new BricknessImpl(bindings);
	}
	
}
