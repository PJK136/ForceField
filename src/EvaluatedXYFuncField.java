import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EvaluatedXYFuncField extends XYFuncField {

	protected String stringExpression;
	protected Expression expression;
	
	private double lastConstant = 0.;
	
	public EvaluatedXYFuncField(Type type, String expression) throws ParseException {
		super();
		setExpression(type, expression);
	}

	public EvaluatedXYFuncField(Type type, String expression, double step) throws ParseException {
		super(step);
		setExpression(type, expression);
	}

	public EvaluatedXYFuncField(Type type, String expression, double step, double parameter) throws ParseException {
		super(step, parameter);
		setExpression(type, expression);
	}

	public EvaluatedXYFuncField(Type type, String expression, double step, double parameter, double constantStart, double constantEnd,
			double constantStep) throws ParseException {
		super(step, parameter, constantStart, constantEnd, constantStep);
		setExpression(type, expression);
	}
	
	@Override
	public void setParameter(double parameter) {
		super.setParameter(parameter);
		expression.setVariable("c", parameter);
	}
	
	@Override
	public double compute(double xy, double constant) {
		if (lastConstant != constant) {
			expression.setVariable("k", constant);
			lastConstant = constant;
		}
			
		if (type == Type.X)
			return expression.setVariable("x", xy).evaluate();
		else
			return expression.setVariable("y", xy).evaluate();
	}

	@Override
	public double computeLineIntegral(Vector2D start, double radius, int path) {
		if (type == Type.Y && stringExpression.equals("exp(y)/(((y+c)^c)*k)")) {
			if (path == 0)
				return Math.pow(radius,3)*(Math.PI/4. - 1./3.) +
						   Math.pow(radius, 2)*(start.x()*Math.PI/4 + start.y()/2. + 1./2.) +
						   radius*(start.x()*start.y() + start.y() + parameter);
			else
				return Math.pow(radius, 3)/3. +
					   Math.pow(radius, 2)*(start.x() + start.y() + 1)/2. +
					   radius*(start.x()*start.y() + start.y() + parameter);
		} else
			return Double.NaN;
	}
	
	private Expression stringToExpression(Type type, String expr) throws ParseException {
		try {
			if (type == Type.X)
				return new ExpressionBuilder(expr).variables("x", "c", "k").build()
												  .setVariable("c", parameter)
												  .setVariable("k", lastConstant);
			else
				return new ExpressionBuilder(expr).variables("y", "c", "k").build()
												  .setVariable("c", parameter)
												  .setVariable("k", lastConstant);
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
	}

	public String expression() {
		return stringExpression;
	}
	
	public void setExpression(Type type, String s) throws ParseException {
		Expression expression = stringToExpression(type, s);
		if (!expression.validate(false).isValid())
			throw new ParseException("Expression non valide");
		setType(type);
		this.stringExpression = s;
		this.expression = expression;
	}
}
