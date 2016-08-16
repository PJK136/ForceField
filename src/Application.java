/**
 * @author Paul Du
 *
 */

public class Application {

	public static void main(String[] args) {
		YFuncField yField = new YFuncField(1./200, -2, 2, 0.5) {

			@Override
			public double compute(double y, double constant) {
				final int c = 2;
				return Math.exp(y)/(Math.pow(y+c, c)*constant);
			}
		};
		
		ForceField forceField = new ForceField(1./100) {
			
			@Override
			public Vector2D nextPoint(Vector2D point, double constant) {
				final double c = 2;
				return Vector2D.add(point, Vector2D.setLength(new Vector2D(point.x()*point.y(), (point.y() + c)), step));
			}
		};
		
		MainWindow window = new MainWindow(yField, forceField);
		
		/*for (double x = -100; x < 100; x += 0.01)
			window.fieldViewPanel().addPoint(new Vector(x, Math.sin(x)));*/
		
		/*window.fieldViewPanel().addField(new FieldYFunc() {
			
			@Override
			public double compute(double y, double constant) {
				return Math.sin(y);
			}
		});*/
	}

}
