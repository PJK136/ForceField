/**
 * @author Paul Du
 *
 */

public abstract class YFuncField extends Field {
	public YFuncField(double step) {
		super(step);
	}
	
	public YFuncField(double step, double constantStart, double constantEnd, double constantStep, double parameter) {
		super(step, constantStart, constantEnd, constantStep, parameter);
	}
	
	public abstract double compute(double y, double constant);
	
	public abstract double computeLineIntegral(Vector2D start, double radius, boolean circle);
}
