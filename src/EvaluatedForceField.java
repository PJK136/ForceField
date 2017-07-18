import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EvaluatedForceField extends ForceField {

	protected String xStringExpression;
	protected String yStringExpression;
	
	protected Expression xExpression;
	protected Expression yExpression;
	
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
	public Vector2D compute(Vector2D point) {
		xExpression.setVariable("x", point.x());
		xExpression.setVariable("y", point.y());
		xExpression.setVariable("c", parameter);
		yExpression.setVariable("x", point.x());
		yExpression.setVariable("y", point.y());
		yExpression.setVariable("c", parameter);
		return new Vector2D(xExpression.evaluate(), yExpression.evaluate());
	}
	
	private static Expression stringToExpression(String expr) throws ParseException {
		try {
			return new ExpressionBuilder(expr).variables("x", "y", "c").build();
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
