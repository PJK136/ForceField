/**
 * Classe abstraite définissant les caractéristiques communes aux lignes de champ analytiques (paramétrées et se différenciant par une constante).
 * @author Paul Du
 */
public abstract class FuncField extends Field {
	/**
	 * Début de l'intervalle de la constante
	 */
	protected double constantStart;
	/**
	 * Fin de l'intervalle de la constante
	 */
	protected double constantEnd;
	/**
	 * Pas de la constante
	 */
	protected double constantStep;
	
	{
		constantStart = 0.;
		constantEnd = 0.;
		constantStep = 1.;
	}
	
	public FuncField() {
		super();
	}
	
	public FuncField(double step) {
		super(step);
	}
	
	public FuncField(double step, double parameter) {
		super(step, parameter);
	}
	
	public FuncField(double step, double parameter, double constantStart, double constantEnd, double constantStep) {
		super(step, parameter);
		this.constantStart = constantStart;
		this.constantEnd = constantEnd;
		this.constantStep = constantStep;
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
	
	/**
	 * Calcule analytiquement la circulation du champ partant d'un point et s'arrêtant à une distance spécifiée dans la direction de PI/4, suivant un chemin spécifié.
	 * @param start Point de départ
	 * @param radius Distance
	 * @param path Chemin
	 * @return Circulation du champ
	 */
	public abstract double computeLineIntegral(Vector2D start, double radius, int path);
}
