import java.awt.Color;

/**
 * Classe permettant de définir les équations des champs et de lancer la modélisation.
 * @author Paul Du
 */
public class Application {

	public static void main(String[] args) {
		XYFuncField yField = new XYFuncField() {
			// Utiliser des blocs d'initialization est plus clair
			{
				step = 1./200.;
				parameter = 2;
				constantStart = -2.;
				constantEnd = 2;
				constantStep = 0.5;
				type = Type.Y;
				color = Color.green;
			}
			
			@Override
			public double compute(double y, double constant) {
				return Math.exp(y)/(Math.pow(y+parameter, parameter)*constant);
			}

			@Override
			public double computeLineIntegral(Vector2D start, double radius, int path) {
				if (path == 0)
					return Math.pow(radius,3)*(Math.PI/4. - 1./3.) +
							   Math.pow(radius, 2)*(start.x()*Math.PI/4 + start.y()/2. + 1./2.) +
							   radius*(start.x()*start.y() + start.y() + parameter);
				else
					return Math.pow(radius, 3)/3. +
						   Math.pow(radius, 2)*(start.x() + start.y() + 1)/2. +
						   radius*(start.x()*start.y() + start.y() + parameter);
			}
		};
		
		ForceField forceField = new ForceField() {
			{
				step = 1./100.;
				parameter = 2.;
				color = Color.red;
			}
			
			@Override
			public Vector2D compute(Vector2D point) {
				// F = (x*y, y+c)
				return new Vector2D(point.x()*point.y(), (point.y() + parameter));
			}
		};
		
		MainWindow window = new MainWindow(yField, forceField);
	}

}
