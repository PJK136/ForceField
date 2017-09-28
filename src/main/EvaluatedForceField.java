import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EvaluatedForceField extends ForceField {

	protected String xStringExpression;
	protected String yStringExpression;
	
	protected Expression xExpression;
	protected Expression yExpression;
	
	private double lastX = 0.;
	private double lastY = 0.;
	
	public EvaluatedForceField(String x, String y) throws ParseException {
		super();
		setXExpression(x);
		setYExpression(y);
	}

	public EvaluatedForceField(String x, String y, double step) throws ParseException {
		super(step);
		setXExpression(x);
		setYExpression(y);
	}
	
	public EvaluatedForceField(String x, String y, double step, double parameter) throws ParseException {
		super(step, parameter);
		setXExpression(x);
		setYExpression(y);
	}
	
	@Override
	public void setParameter(double parameter) {
		super.setParameter(parameter);
		xExpression.setVariable("c", parameter);
		yExpression.setVariable("c", parameter);
	}
	
	@Override
	public Vector2D compute(Vector2D point) {
		if (point.x() != lastX) {
			xExpression.setVariable("x", point.x());
			yExpression.setVariable("x", point.x());
			lastX = point.x();
		}
		
		if (point.y() != lastY) {
			xExpression.setVariable("y", point.y());
			yExpression.setVariable("y", point.y());
			lastY = point.y();
		}
		
		return new Vector2D(xExpression.evaluate(), yExpression.evaluate());
	}
	
	private Expression stringToExpression(String expr) throws ParseException {
		try {
			return new ExpressionBuilder(expr).variables("x", "y", "c").build()
											  .setVariable("x", lastX)
											  .setVariable("y", lastY)
											  .setVariable("c", parameter);
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
	}
	
	public String xExpression() {
		return xStringExpression;
	}
	
	public void setXExpression(String x) throws ParseException {
		Expression expression = stringToExpression(x);
		if (!expression.validate(false).isValid())
			throw new ParseException("Expression non valide");
		this.xStringExpression = x;
		this.xExpression = expression;
	}
	
	public String yExpression() {
		return yStringExpression;
	}
	
	public void setYExpression(String y) throws ParseException {
		Expression expression = stringToExpression(y);
		if (!expression.validate(false).isValid())
			throw new ParseException("Expression non valide");
		this.yStringExpression = y;
		this.yExpression = expression;
	}
}
