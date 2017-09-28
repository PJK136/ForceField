import java.awt.Color;

/**
 * Classe abstraite définissant les caractéristiques communes au calcul et à la représentation de tous les champs.
 * @author Paul Du
 */
public abstract class Field {
	/**
	 * Pas de la représentation
	 */
	protected double step;
	
	/**
	 * Paramètre constant du champ
	 */
	protected double parameter;
	
	/**
	 * Couleur de la représentation du champ
	 */
	protected Color color;
	
	//Définit les valeurs par défaut (le bloc d'initialisation s'exécute avant le constructeur)
	{
		step = 1.;
		parameter = 1.;
		color = Color.black;
	}
	
	/**
	 * Construit un champ avec les valeurs par défaut (pas = 1, paramètre = 1, couleur = noire). 
	 */
	public Field() {
	}
	
	/**
	 * Construit un champ avec le pas spécifié.
	 * @param step Pas
	 */
	public Field(double step) {
		this.step = step;
	}
	
	/**
	 * Construit un champ avec le pas et le paramètre spécifiés.
	 * @param step Pas
	 * @param parameter Paramètre
	 */
	public Field(double step, double parameter) {
		this.step = step;
		this.parameter = parameter;
	}
	
	/**
	 * Construit un champ avec le pas, le paramètre et la couleur spécifiés.
	 * @param step Pas
	 * @param parameter Paramètre
	 * @param color Couleur
	 */
	public Field(double step, double parameter, Color color) {
		this.step = step;
		this.parameter = parameter;
		this.color = color;
	}
	
	/**
	 * Retourne le pas de la représentation.
	 * @return Pas
	 */
	public double step() {
		return step;
	}
	
	/**
	 * Modifie le pas de la représentation.
	 * @param step Pas
	 */
	public void setStep(double step) {
		this.step = step;
	}
	
	/**
	 * Retourne le paramètre du champ.
	 * @return Paramètre
	 */
	public double parameter() {
		return parameter;
	}
	
	/**
	 * Modifie le paramètre du champ.
	 * @param parameter Paramètre
	 */
	public void setParameter(double parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * Retourne la couleur de la représentation du champ.
	 * @return Couleur
	 */
	public Color color() {
		return color;
	}
	
	/**
	 * Modifie la couleur de la représentation du champ.
	 * @param color Couleur
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}
