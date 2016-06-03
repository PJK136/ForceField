/**
 * @author Paul Du
 *
 */

public abstract class ForceField extends Field {
	public ForceField(double step) {
		super(step);
	}
	
	public ForceField(double step, double constantStart, double constantEnd, double constantStep, double parameter) {
		super(step, constantStart, constantEnd, constantStep, parameter);
	}

	public abstract Vector2D compute(Vector2D point, double constant);
	
	public Vector2D nextPoint(Vector2D point, double constant) {
		return Vector2D.add(point, Vector2D.setLength(compute(point, constant), step));
	}
	
	public double computeLineIntegral(Vector2D start, double R, double step, boolean circle) {
		double lineIntegral = 0.;

		for (double constant = constantStart; constant <= constantEnd; constant += constantStep) {
			if (circle) {
				final double da = -step; //Circulation de pi -> pi/2 donc da < 0
				for (double a = Math.PI; a >= Math.PI/2; a += da) {
					//OM = (R*cos(a) + R + x1, R*sin(a) + y1)
					Vector2D point = new Vector2D(R*Math.cos(a) + R + start.x(), R*Math.sin(a) + start.y());
					Vector2D force = compute(point, constant);
					//dOM = (-R*sin(a)da, R*cos(a)da)
					Vector2D dOM = new Vector2D(-R*Math.sin(a)*da, R*Math.cos(a)*da);
					if (!Double.isNaN(force.x()) && !Double.isNaN(force.y()))
							lineIntegral += Vector2D.dotProduct(force, dOM);
				}
			} else { //line
				final double dt = step;
				for (double t = 0; t <= R; t += dt) {
					//OM = (x1 + t, y1 + t)
					Vector2D point = Vector2D.add(start, new Vector2D(t, t));
					Vector2D force = compute(point, constant);
					//dOM = (dt, dt)
					Vector2D dOM = new Vector2D(dt, dt);
					if (!Double.isNaN(force.x()) && !Double.isNaN(force.y()))
							lineIntegral += Vector2D.dotProduct(force, dOM);
				}
			}
		}
		
		return lineIntegral;
	}
}
