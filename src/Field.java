
public abstract class Field {
	protected double step;
	private double constantStart;
	private double constantEnd;
	private double constantStep;
	
	public Field(double step) {
		this(step, 0., 0., 1.);
	}
	
	public Field(double step, double constantStart, double constantEnd, double constantStep) {
		this.step = step;
		this.constantStart = constantStart;
		this.constantEnd = constantEnd;
		this.constantStep = constantStep;
	}
	
	public double step() {
		return step;
	}
	
	public void setStep(double step) {
		this.step = step;
	}
	
	public double constantStart() {
		return constantStart;
	}
	
	public double constantEnd() {
		return constantEnd;
	}
	
	public double constantStep() {
		return constantStep;
	}
	
	public void setConstantStart(double start) {
		constantStart = start;
	}
	
	public void setConstantEnd(double end) {
		constantEnd = end;
	}
	
	public void setConstantStep(double step) {
		constantStep = step;
	}
}
