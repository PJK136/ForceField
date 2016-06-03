/**
 * @author Paul Du
 *
 */

public class Vector2D {
	private double x;
	private double y;
	
	public Vector2D(double x, double y) {
		set(x, y);
	}
	
	public Vector2D() {
		set(0,0);
	}
	
	public Vector2D(Vector2D v) {
		set(v.x, v.y);
	}

	public double x() {
		return x;
	}
	
	public double y() {
		return y;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public void add(Vector2D v) {
		add(v.x, v.y);
	}
	
	public static Vector2D add(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x + v2.x, v1.y + v2.y);
	}
	
	public void sub(double x, double y) {
		add(-x, -y);
	}
	
	public void sub(Vector2D v) {
		add(-v.x, -v.y);
	}
	
	public static Vector2D sub(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.x - v2.x, v1.y - v2.y);
	}
	
	public double dotProduct(Vector2D v) {
		return x*v.x + y*v.y;
	}
	
	public void multiply(double d) {
		set(x*d, y*d);
	}
	
	public static Vector2D multiply(Vector2D v, double d) {
		return new Vector2D(v.x*d, v.y*d);
	}
	
	public static double dotProduct(Vector2D v1, Vector2D v2) {
		return v1.x*v2.x + v1.y*v2.y;
	}
	
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	public void normalize() {
		double length = length();
		x /= length;
		y /= length;
	}
	
	public void setLength(double length) {
		normalize();
		multiply(length);
	}
	
	public static Vector2D normalize(Vector2D v) {
		Vector2D r = new Vector2D(v);
		r.normalize();
		return r;
	}
	
	public static Vector2D setLength(Vector2D v, double length) {
		return Vector2D.multiply(Vector2D.normalize(v), length);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public String toStringNdecimals(int decimals) {
		if (decimals < 0)
			return "Error decimals";
		
		return String.format("(%."+decimals+"f, %."+decimals+"f)", x, y);
	}
	
	public boolean isNaN() {
		return Double.isNaN(x) || Double.isNaN(y);
	}
}
