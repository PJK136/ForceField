/**
 * @author Paul Du
 *
 */

public abstract class YFuncField extends Field {
	public YFuncField(double step) {
		super(step);
	}
	
	public YFuncField(double step, double constantStart, double constantEnd, double constantStep) {
		super(step, constantStart, constantEnd, constantStep);
	}
	
	public abstract double compute(double y, double constant);
}
