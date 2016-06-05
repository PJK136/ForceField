/**
 * Classe abstraite représentant les lignes de champ analytiques de type (x,y) = f(t).
 * <br>Il ne reste qu'à la dériver pour implémenter l'équation des lignes de champ.
 * @author Paul Du
 *
 */
public abstract class TFuncField extends FuncField {
	/**
	 * Début de l'intervalle du paramètre t
	 */
	private double startT;
	/**
	 * Fin de l'intervalle du paramètre t
	 */
	private double endT;
	
	{
		setStartT(0.0);
		setEndT(0.0);
	}
	
	public TFuncField() {
		super();
	}

	public TFuncField(double step, double parameter) {
		super(step, parameter);
	}

	public TFuncField(double step, double parameter, double constantStart, double constantEnd, double constantStep) {
		super(step, parameter, constantStart, constantEnd, constantStep);
	}
	
	public TFuncField(double step, double parameter, double constantStart, double constantEnd, double constantStep, double startT, double endT) {
		super(step, parameter, constantStart, constantEnd, constantStep);
		this.setStartT(startT);
		this.setEndT(endT);
	}


	public double startT() {
		return startT;
	}

	public void setStartT(double startT) {
		this.startT = startT;
	}

	public double endT() {
		return endT;
	}

	public void setEndT(double endT) {
		this.endT = endT;
	}

	/**
	 * Calcule les coordonnées d'un point d'une ligne de champ en fonction de t et de la constante.
	 * @param t Paramètre t
	 * @param constant Constante différenciant chaque ligne de champ
	 * @return Point appartenant à une ligne de champ
	 */
	public abstract Vector2D compute(double t, double constant);
}
