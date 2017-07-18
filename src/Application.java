import java.awt.Color;

/**
 * Classe permettant de définir les équations des champs et de lancer la modélisation.
 * @author Paul Du
 */
public class Application {

	public static void main(String[] args) throws ParseException {	
		EvaluatedXYFuncField yField = new EvaluatedXYFuncField(XYFuncField.Type.Y, "exp(y)/(((y+c)^c)*k)", 1./200, 2, -2, 2, 0.5);
		yField.setColor(Color.green);
		
		EvaluatedForceField forceField = new EvaluatedForceField("xy", "y+c", 1./100., 2.);
		forceField.setColor(Color.red);
		
		MainWindow window = new MainWindow(yField, forceField);
	}

}
