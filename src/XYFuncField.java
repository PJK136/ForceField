/**
 * Classe abstraite représentant les lignes de champ analytiques de type y = f(x) ou x = f(y).
 * <br>Il ne reste qu'à la dériver pour implémenter l'équation des lignes de champ.
 * @author Paul Du
 */
public abstract class XYFuncField extends FuncField {
	
	/**
	 * Énumération des types d'équation des lignes de champs.
	 */
	enum Type {
		/**
		 * Pour une équation de type y = f(x)
		 */
		X,
		/**
		 * Pour une équation de type x = f(y)
		 */
		Y
	}
	
	/**
	 * Type de l'équation des lignes de champ.
	 */
	protected Type type;
	
	{
		type = Type.X;
	}
	
	public XYFuncField() {
		super();
	}
	
	public XYFuncField(double step) {
		super(step);
	}

	public XYFuncField(double step, double parameter) {
		super(step, parameter);
	}
	
	public XYFuncField(double step, double parameter, double constantStart, double constantEnd, double constantStep) {
		super(step, parameter, constantStart, constantEnd, constantStep);
	}
	
	public XYFuncField(double step, double parameter, double constantStart, double constantEnd, double constantStep, Type type) {
		super(step, parameter, constantStart, constantEnd, constantStep);
		this.type = type;
	}

	public Type type() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * Calcule la composante x ou y d'un point d'une ligne de champ en fonction de l'autre composante et de la constante.
	 * @param xy Composante x ou y selon le type
	 * @param constant Constante différenciant chaque ligne de champ
	 * @return Composante y pour le type X ou composante x ou pour le type Y 
	 */
	public abstract double compute(double xy, double constant);
}
