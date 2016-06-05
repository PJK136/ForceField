/**
 * Classe représentant les coordonnées cartésiennes d'un point ou d'un vecteur de dimension 2.
 * @author Paul Du
 */

public class Vector2D {
	/**
	 * Composante x
	 */
	private double x;
	
	/**
	 * Composante y
	 */
	private double y;
	
	/**
	 * Construit un vecteur de coordonnées (x,y).
	 * @param x Composante x
	 * @param y Composante y
	 */
	public Vector2D(double x, double y) {
		set(x, y);
	}
	
	/**
	 * Construit un vecteur de coordonnées (0,0).
	 */
	public Vector2D() {
		set(0,0);
	}
	
	/**
	 * Copie le vecteur passé en paramètre.
	 * @param v Vecteur à copier
	 */
	public Vector2D(Vector2D v) {
		set(v.x, v.y);
	}

	/**
	 * Retourne la composante x.
	 * @return Composante x
	 */
	public double x() {
		return x;
	}
	
	/**
	 * Retourne la composante y.
	 * @return Composante y
	 */
	public double y() {
		return y;
	}
	
	/**
	 * Modifie les coordonnées.
	 * @param x Composante x
	 * @param y Composante y
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Modifie la composante x.
	 * @param x Composante x
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Modifie la composante y.
	 * @param y Composante y.
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Ajoute les coordonnées (x,y) au vecteur.
	 * @param x Composante x à ajouter
	 * @param y Composante y à ajouter
	 */
	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Ajoute le vecteur passé en paramètre à ce vecteur.
	 * @param v Vecteur à ajouter
	 */
	public void add(Vector2D v) {
		add(v.x, v.y);
	}
	
	/**
	 * Retourne la somme de deux vecteurs.
	 * @param v1 Premier vecteur
	 * @param v2 Second vecteur
	 * @return La somme de v1 et v2
	 */
	public static Vector2D add(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x + v2.x, v1.y + v2.y);
	}
	
	/**
	 * Soustrait les coordonnées (x,y) à ce vecteur.
	 * @param x Composante x à soustraire
	 * @param y Composante y à soustraire
	 */
	public void sub(double x, double y) {
		add(-x, -y);
	}
	
	/**
	 * Soustrait le vecteur passé en paramètre à ce vecteur.
	 * @param v Vecteur à soustraire
	 */
	public void sub(Vector2D v) {
		add(-v.x, -v.y);
	}
	
	/**
	 * Retourne la différence de deux vecteurs.
	 * @param v1 Premier vecteur
	 * @param v2 Second vecteur
	 * @return La différence entre v1 et v2
	 */
	public static Vector2D sub(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x - v2.x, v1.y - v2.y);
	}
	
	/**
	 * Retourne le produit scalaire entre ce vecteur et celui passé en paramètre.
	 * @param v Vecteur pour le produit scalaire
	 * @return Produit scalaire avec v
	 */
	public double dotProduct(Vector2D v) {
		return x*v.x + y*v.y;
	}
	
	/**
	 * Retourne le produit scalaire entre deux vecteurs.
	 * @param v1 Premier vecteur
	 * @param v2 Second vecteur
	 * @return Produit scalaire entre v1 et v2
	 */
	public static double dotProduct(Vector2D v1, Vector2D v2) {
		return v1.x*v2.x + v1.y*v2.y;
	}
	
	/**
	 * Multiplie ce vecteur par un nombre.
	 * @param d Nombre réel
	 */
	public void multiply(double d) {
		set(x*d, y*d);
	}
	
	/**
	 * Retourne le produit entre un vecteur et un nombre
	 * @param v Vecteur 2D
	 * @param d Nombre réel
	 * @return Produit entre v et d
	 */
	public static Vector2D multiply(Vector2D v, double d) {
		return new Vector2D(v.x*d, v.y*d);
	}
	
	
	/**
	 * Retourne la longueur du vecteur.
	 * @return Longueur du vecteur
	 */
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Normalise ce vecteur.
	 */
	public void normalize() {
		double length = length();
		x /= length;
		y /= length;
	}
	
	/**
	 * Modifie la longueur de ce vecteur.
	 * @param length Longueur
	 */
	public void setLength(double length) {
		normalize();
		multiply(length);
	}
	
	/**
	 * Retourne le vecteur normalisé du vecteur passé en paramètre
	 * @param v Vecteur à normaliser
	 * @return Vecteur normalisé
	 */
	public static Vector2D normalize(Vector2D v) {
		Vector2D r = new Vector2D(v);
		r.normalize();
		return r;
	}
	
	/**
	 * Retourne le vecteur passé en paramètre à la longueur spécifiée.
	 * @param v Vecteur à modifier
	 * @param length Longueur
	 * @return Vecteur modifié
	 */
	public static Vector2D setLength(Vector2D v, double length) {
		return Vector2D.multiply(Vector2D.normalize(v), length);
	}
	
	/**
	 * Retourne une représentation décimale du vecteur sous forme "(x, y)".
	 */
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * Retourne une représentation décimale du vecteur sous forme "(x, y)" avec un nombre de décimales spécifié.
	 * @param N Nombre de décimales
	 * @return String de la forme "(x, y)" avec N décimales 
	 */
	public String toStringNdecimals(int N) {
		if (N < 0)
			return "Error decimals";
		
		return String.format("(%."+N+"f, %."+N+"f)", x, y);
	}
	
	/**
	 * Retourne vrai si une des composantes n'est pas un nombre.
	 * @return Vrai si une des composantes est NaN
	 */
	public boolean isNaN() {
		return Double.isNaN(x) || Double.isNaN(y);
	}
}
