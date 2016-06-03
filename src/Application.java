/**
 * @author Paul Du
 *
 */

public class Application {

	public static void main(String[] args) {
		YFuncField yField = new YFuncField(1./200, -2, 2, 0.5, 2) {
			
			@Override
			public double compute(double y, double constant) {
				return Math.exp(y)/(Math.pow(y+parameter, parameter)*constant);
			}

			@Override
			public double computeLineIntegral(Vector2D start, double radius, boolean circle) {
				if (circle)
					return Math.pow(radius,3)*(Math.PI/4. - 1./3.) +
						   Math.pow(radius, 2)*(start.x()*Math.PI/4 + start.y()/2. + 1./2.) +
						   radius*(start.x()*start.y() + start.y() + parameter);
				else
					return Math.pow(radius, 3)/3. +
						   Math.pow(radius, 2)*(start.x() + start.y() + 1)/2. +
						   radius*(start.x()*start.y() + start.y() + parameter);
			}
		};
		
		ForceField forceField = new ForceField(1./100) {

			@Override
			public Vector2D compute(Vector2D point, double constant) {
				parameter = 2;
				// (x*y, y+c)
				return new Vector2D(point.x()*point.y(), (point.y() + parameter));
			}
		};
		
		MainWindow window = new MainWindow(yField, forceField);
		
		//Ajouter des points
		/*for (double x = -100; x < 100; x += 0.01)
			window.fieldViewPanel().addPoint(new Vector(x, Math.sin(x)));*/
		
		//Ajouter un champp
		/*window.fieldViewPanel().addField(new FieldYFunc() {
			
			@Override
			public double compute(double y, double constant) {
				return Math.sin(y);
			}
		});*/
	}

}
