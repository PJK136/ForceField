/**
 * @author Paul Du
 *
 */

public abstract class ForceField extends Field {
	public ForceField(double step) {
		super(step);
	}
	
	public ForceField(double step, double constantStart, double constantEnd, double constantStep) {
		super(step, constantStart, constantEnd, constantStep);
	}

	public abstract Vector2D nextPoint(Vector2D point, double constant);
}
