/**
 * Classe abstraite implémentant les calculs de modélisation numérique d'un champ de force.
 * <br>Il ne reste qu'à la dériver pour implémenter l'équation de la force en un point.
 * @author Paul Du
 */
public abstract class ForceField extends Field {
	public ForceField() {
		super();
	}
	
	public ForceField(double step) {
		super(step);
	}
	
	public ForceField(double step, double parameter) {
		super(step, parameter);
	}

	/**
	 * Calcule la force à un point donné.
	 * @param point Point d'application de la force
	 * @return Vecteur force
	 */
	public abstract Vector2D compute(Vector2D point);

	/**
	 * Calcule le point précédent suivant la force.
	 * @param point Point actuel
	 * @return Point précédent
	 */
	public Vector2D previousPoint(Vector2D point) {
		return Vector2D.sub(point, Vector2D.setLength(compute(point), step));
	}
	
	/**
	 * Calcule le prochain point suivant la force.
	 * @param point Point actuel
	 * @return Point suivant
	 */
	public Vector2D nextPoint(Vector2D point) {
		return Vector2D.add(point, Vector2D.setLength(compute(point), step));
	}
	
	/**
	 * Calcule numériquement la circulation du champ partant d'un point et s'arrêtant à une distance spécifiée dans la direction de PI/4, suivant un chemin spécifié avec un pas spécifié.
	 * <br>Cette implémentation propose comme chemins : 0 = Arc de cercle d'angle PI/4 sens horaire, 1 = Ligne droite
	 * @param start Point de départ
	 * @param R Distance
	 * @param path Chemin
	 * @param step Pas
	 * @return Circulation du champ
	 */
	public double computeLineIntegral(Vector2D start, double R, int path, double step) {
		double lineIntegral = 0.;
		
		if (path == 0) { //Arc de cercle
			final double da = -step; //Circulation de pi -> pi/2 donc da < 0
			for (double a = Math.PI; a >= Math.PI/2; a += da) {
				//OM = (R*cos(a) + R + x1, R*sin(a) + y1)
				Vector2D point = new Vector2D(R*Math.cos(a) + R + start.x(), R*Math.sin(a) + start.y());
				Vector2D force = compute(point);
				//dOM = (-R*sin(a)da, R*cos(a)da)
				Vector2D dOM = new Vector2D(-R*Math.sin(a)*da, R*Math.cos(a)*da);
				if (!Double.isNaN(force.x()) && !Double.isNaN(force.y()))
						lineIntegral += Vector2D.dotProduct(force, dOM);
			}
		} else { //Ligne
			final double dt = step;
			for (double t = 0; t <= R; t += dt) {
				//OM = (x1 + t, y1 + t)
				Vector2D point = Vector2D.add(start, new Vector2D(t, t));
				Vector2D force = compute(point);
				//dOM = (dt, dt)
				Vector2D dOM = new Vector2D(dt, dt);
				if (!Double.isNaN(force.x()) && !Double.isNaN(force.y()))
						lineIntegral += Vector2D.dotProduct(force, dOM);
			}
		}

		return lineIntegral;
	}
}
