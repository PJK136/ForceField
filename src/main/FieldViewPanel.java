import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Classe graphique représentant des points, des lignes de champ analytiques et des champs de forces numériques.
 * @author Paul Du
 */
public class FieldViewPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private Vector2D lastMousePosition;
	
	/**
	 * Coordonnées (dans le repère du graphe) du coin en haut à gauche de la vue 
	 */
	private Vector2D offset;
	/**
	 * Facteur d'agrandissement de la vue
	 */
	private double zoom;
	
	/**
	 * Énumération des types de représentation des points et des lignes de champ.
	 */
	public enum RenderType {
		CROSS,
		RECTANGLE,
		OVAL,
		LINE,
		/**
		 * Rectangles et lignes à la fois
		 */
		RECT_LINE
	}
	
	RenderType type;
	private double strokeSize;
	
	private List<Vector2D> points;
	private List<XYFuncField> xyFields;
	private List<TFuncField> tFields;
	private List<ForceField> forceFields;
	
	/**
	 * Nombre de points par ligne de champ pour la modélisation des champs de force
	 */
	private int numberOfPoints;
	private List<Vector2D> startingPoints;
	
	private Vector2D lineIntegralStartingPoint;
	private boolean lineIntegralVisibility;
	private boolean lineIntegralCircle;
	private double lineIntegralRadius;
	private Color lineIntegralColor;
	
	/**
	 * Mode d'ajout de points (pour la modélisation numérique ou la circulation)
	 */
	private boolean settingPointMode;
	
	public FieldViewPanel() {
		super();
		lastMousePosition = new Vector2D();
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.white);
		
		reset();
		type = RenderType.CROSS;
		strokeSize = 1.;
		
		points = new ArrayList<Vector2D>();
		xyFields = new ArrayList<XYFuncField>();
		tFields = new ArrayList<TFuncField>();
		forceFields = new ArrayList<ForceField>();
		
		numberOfPoints = 1000;
		startingPoints = new ArrayList<Vector2D>();
		
		lineIntegralStartingPoint = new Vector2D();
		lineIntegralVisibility = false;
		lineIntegralCircle = true;
		lineIntegralRadius = 10.;
		lineIntegralColor = Color.blue;
		
		settingPointMode = false;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void addPoint(Vector2D point) {
		points.add(point);
		update();
	}

	public void addField(XYFuncField field) {
		xyFields.add(field);
		update();
	}
	
	public void addField(TFuncField field) {
		tFields.add(field);
		update();
	}
	
	public void addField(ForceField field) {
		forceFields.add(field);
		update();
	}
	
	public void clearXYFuncFields() {
		xyFields.clear();
		update();
	}
	
	public void clearTFuncFields() {
		tFields.clear();
		update();
	}
	
	public void clearForceFields() {
		forceFields.clear();
		update();
	}

	/**
	 * Met à jour l'affichage de la vue.
	 */
	public void update() {
		repaint();
	}
	
	/**
	 * Réinitialise la position et le facteur d'agrandissement de la vue.
	 */
	public void reset() {
		offset = new Vector2D(-2.5,-2.5);
		zoom = 100;
		update();
	}
	
	/**
	 * Transpose dans le repère du graphe la dernière position enregistrée de la souris.
	 * @return Position dans le repère du graphe de la souris
	 */
	public Vector2D getLastMousePositionOnGraph() {
		return new Vector2D(lastMousePosition.x()/zoom + offset.x(),
							(getHeight() - lastMousePosition.y())/zoom + offset.y()); // Inversion des Y
	}
	
	public void setRenderType(RenderType type) {
		this.type = type;
		update();
	}
	
	public double strokeSize() {
		return strokeSize;
	}
	
	public void setStrokeSize(double strokeSize) {
		this.strokeSize = Math.max(1., strokeSize);
		update();
	}
	
	public int numberOfPoints() {
		return numberOfPoints;
	}
	
	public void setNumberOfPoints(int number) {
		numberOfPoints = number;
		update();
	}
	
	public void setStartingPoints(List<Vector2D> startingPoints) {
		this.startingPoints = startingPoints;
		update();
	}
	
	public void setLineIntegralVisibility(boolean visible) {
		lineIntegralVisibility = visible;
		update();
	}
	
	public Vector2D lineIntegralStartingPoint() {
		return lineIntegralStartingPoint;
	}
	
	public void setLineIntegralStartingPoint(Vector2D startingPoint) {
		lineIntegralStartingPoint = startingPoint;
		update();
	}
	
	public void setLineIntegralCircle(boolean circle) {
		lineIntegralCircle = circle;
		update();
	}
	
	public double lineIntegralRadius() {
		return lineIntegralRadius;
	}
	
	public void setLineIntegralRadius(double radius) {
		lineIntegralRadius = Math.max(0., radius);
		update();
	}
	
	public Color lineIntegralColor() {
		return lineIntegralColor;
	}
	
	public void setLineIntegralColor(Color color) {
		lineIntegralColor = color;
		update();
	}
	
	public boolean settingPointMode() {
		return settingPointMode;
	}
	
	public void setSettingPointMode(boolean settingPointMode) {
		this.settingPointMode = settingPointMode;
		if (settingPointMode && getCursor().getType() == Cursor.DEFAULT_CURSOR)
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		else if (!settingPointMode && getCursor().getType() == Cursor.CROSSHAIR_CURSOR)
			setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Arrondit à l'unité un nombre flottant à double précision.
	 * @param value Nombre flottante
	 * @return Nombre entier
	 */
	public static int round(double value) {
		return (int) Math.round(value);
	}
	
	/**
	 * Transpose la composante x depuis le repère du graphe dans le repère de la vue.
	 * @param x Composante x (dans le repère du graphe)
	 * @return Composante x (dans le repère de la vue)
	 */
	private double toScreenX(double x) {
		return (x - offset.x())*zoom;
	}

	/**
	 * Transpose la composante y depuis le repère du graphe dans le repère de la vue.
	 * @param y Composante y (dans le repère du graphe)
	 * @return Composante y (dans le repère de la vue)
	 */
	private double toScreenY(double y) {
		return getHeight() - (y - offset.y())*zoom;
	}
	
	/**
	 * Transpose les coordonnées d'un point depuis le repère du graphe dans le repère de la vue.
	 * @param point Point à transposer (dans le repère du graphe)
	 * @return Point transposé (dans le repère de la vue)
	 */
	private Vector2D toScreen(Vector2D point) {
		return new Vector2D(toScreenX(point.x()), toScreenY(point.y()));
	}
	
	/**
	 * Vérifie si la composante x dans le repère de la vue est incluse ou n'est pas trop éloignée de la vue.
	 * <br>Cette méthode retourne vrai même si le point est en dehors de l'écran.
	 * <br>Cela permet à des lignes ayant des points en dehors de la vue d'être tracées au cas où elles coupent la vue.
	 * @param screenX Composante x (dans le repère de la vue)
	 * @return Vrai si la composante est un nombre et appartient à l'intervalle de la vue ou de tolérance
	 */
	private boolean isOnScreenX(double screenX) {
		return !Double.isNaN(screenX) && screenX > -100 && screenX < getWidth()+100;
	}
	
	/**
	 * Vérifie si la composante y dans le repère de la vue est incluse ou n'est pas trop éloignée de la vue.
	 * <br>Cette méthode retourne vrai même si le point est en dehors de l'écran.
	 * <br>Cela permet à des lignes ayant des points en dehors de la vue d'être tracées au cas où elles coupent la vue.
	 * @param screenY Composante y (dans le repère de la vue)
	 * @return Vrai si la composante est un nombre et appartient à l'intervalle de la vue ou de tolérance
	 */
	private boolean isOnScreenY(double screenY) {
		return !Double.isNaN(screenY) && screenY > -100 && screenY < getHeight()+100;
	}
	
	/**
	 * Vérifie si un point dans le repère de la vue est inclus ou n'est pas trop éloigné de la vue.
	 * <br>Cette méthode retourne vrai même si le point est en dehors de l'écran.
	 * <br>Cela permet à des lignes ayant des points en dehors de la vue d'être tracées au cas où elles coupent la vue.
	 * @param screenPoint Point à vérifier (dans le repère de la vue)
	 * @return Vrai si les coordonnées du point sont des nombres et si le point appartient à la vue ou ne s'en éloigne pas trop
	 */
	private boolean isOnScreen(Vector2D screenPoint) {
		return isOnScreenX(screenPoint.x()) && isOnScreenY(screenPoint.y());
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		drawAxes(graphics);
		
		((Graphics2D) graphics).setStroke(new BasicStroke((float) strokeSize));
		
		drawPoints(graphics);
		
		drawXYFields(graphics);
		
		drawTFields(graphics);
		
		drawForceFields(graphics);
		
		drawLineIntegral(graphics);
	}

	/**
	 * Dessine et gradue les axes.
	 * @param graphics Contexte graphique dans lequel dessiner.
	 */
	private void drawAxes(Graphics graphics){
		graphics.setColor(new Color(0,125,0));
		//graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		// Origine du repère
		graphics.drawString("O", round(toScreenX(0)) - 12, round(toScreenY(0)) + graphics.getFontMetrics().getHeight() + 10);
		
		//Distance (en px) entre les graduations
		final double SPACING = 75.;
		
		if (isOnScreenY(0))
		{
			final int ScreenY0 = round(toScreenY(0));
			// Axe horizontal
			graphics.drawLine(0, ScreenY0, getWidth(), ScreenY0);
			graphics.drawLine(getWidth()-10, ScreenY0-5, getWidth(), ScreenY0);
			graphics.drawLine(getWidth()-10, ScreenY0+5, getWidth(), ScreenY0);
			
			//Graduation des abscisses
		    double min = Math.floor(offset.x());
		    double max = Math.ceil(offset.x()+getWidth()/zoom);
			int step = (int)Math.ceil((max-min)/(getWidth()/SPACING));
			
			for (int i = -step; i >= min; i -= step)  // On part de -1 (par exemple) au lieu de 0 (qui est déjà dessiné)
				drawXAxisTick(graphics, i, ScreenY0); // Et on décrémente pour graduer ensuite 2, -3, -4, etc.
			
			for (int i = step; i <= max; i += step)   // On part de 1 (par exemple) au lieu de 0 (qui est déjà dessiné)
				drawXAxisTick(graphics, i, ScreenY0); // Et on incrémente pour graduer ensuite 2, 3, 4, etc. 
		}
		
		if (isOnScreenX(0))
		{
			final int ScreenX0 = round(toScreenX(0));
			// Axe vertical
			graphics.drawLine(ScreenX0, 0, ScreenX0, getHeight());
			graphics.drawLine(ScreenX0-5, 10, ScreenX0, 0);
			graphics.drawLine(ScreenX0+5, 10, ScreenX0, 0);
			
			//Graduation des ordonnées
		    double min = Math.floor(offset.y());
		    double max = Math.ceil(offset.y()+getHeight()/zoom);
		    int step = (int)Math.ceil((max-min)/(getHeight()/SPACING));
			
			for (int i = -step; i >= min; i -= step)
				drawYAxisTick(graphics, i, ScreenX0);
			
			for (int i = step; i <= max; i += step)
				drawYAxisTick(graphics, i, ScreenX0);
		}
	}
	
	/**
	 * Dessine une graduation sur l'axe horizontal x.
	 * @param graphics Contexte graphique dans lequel dessiner
	 * @param value Valeur de la graduation
	 * @param ScreenY0 Composante y (dans le repère de la vue) de l'axe horizontal x
	 */
	private void drawXAxisTick(Graphics graphics, int value, int ScreenY0) {
		double screenX = toScreenX(value);
		if (isOnScreenX(screenX))
		{
			String number = String.valueOf(value);
			graphics.drawLine(round(screenX), ScreenY0-5, round(screenX), ScreenY0+5);
			graphics.drawString(number, round(screenX) - graphics.getFontMetrics().stringWidth(number)/2,
								ScreenY0 + graphics.getFontMetrics().getHeight() + 10);
		}
	}
	
	/**
	 * Dessine une graduation sur l'axe vertical y.
	 * @param graphics Contexte graphique dans lequel dessiner
	 * @param value Valeur de la graduation
	 * @param ScreenX0 Composante x (dans le repère de la vue) de l'axe vertical y
	 */
	private void drawYAxisTick(Graphics graphics, int value, int ScreenX0) {
		double screenY = toScreenY(value);
		if (isOnScreenY(screenY)) {
			String number = String.valueOf(value);
			graphics.drawLine(ScreenX0-5, round(screenY), ScreenX0 + 5, round(screenY));
			graphics.drawString(number, ScreenX0 - graphics.getFontMetrics().stringWidth(number) - 10,
								round(screenY) + graphics.getFontMetrics().getHeight()/2);
		}
	}
	
	/**
	 * Dessine un point.
	 * @param graphics Contexte graphique dans lequel dessiner
	 * @param x Composante x du point (dans le repère du graphe)
	 * @param y Composante y du point (dans le repère du graphe)
	 */
	private void drawPoint(Graphics graphics, double x, double y) {
		double screenX = toScreenX(x);
		double screenY = toScreenY(y);
		if (isOnScreenX(screenX) && isOnScreenY(screenY))
		{
			int screenXInt = round(screenX);
			int screenYInt = round(screenY);
			switch (type) {
			default:
			case CROSS:
				graphics.drawLine(screenXInt, screenYInt-1, screenXInt, screenYInt+1); //Dessiner des croix est plus rapide
				graphics.drawLine(screenXInt-1, screenYInt, screenXInt-1, screenYInt); //que dessiner des ovales ou rectangles
				break;
			case RECTANGLE:
				graphics.drawRect(screenXInt-1, screenYInt-1, 3, 3);
				break;
			case OVAL:
				graphics.drawOval(screenXInt-1, screenYInt-1, 3, 3);
				break;
			case RECT_LINE:
				graphics.drawRect(screenXInt-2, screenYInt-2, 5, 5);
			}
		}
	}
	
	/**
	 * Dessine un point.
	 * @param graphics Contexte graphique dans lequel dessiner
	 * @param point Point à dessiner (dans le repère du graphe)
	 */
	private void drawPoint(Graphics graphics, Vector2D point) {
		drawPoint(graphics, point.x(), point.y());
	}
	
	/**
	 * Dessine les points.
	 * @param graphics Contexte graphique dans lequel dessiner
	 */
	private void drawPoints(Graphics graphics) {
		graphics.setColor(Color.gray);
		for (Vector2D p : points) {
			drawPoint(graphics, p);
		}
	}
	
	/**
	 * Dessine une ligne avec un point de départ et un point d'arrivée.
	 * @param graphics Contexte graphique dans lequel dessiner
	 * @param start Point de départ (dans le repère du graphe)
	 * @param end Point d'arrivée (dans le repère du graphe)
	 */
	private void drawLine(Graphics graphics, Vector2D start, Vector2D end) {
		Vector2D screenStart = toScreen(start);
		Vector2D screenEnd = toScreen(end);
		if (isOnScreen(screenStart) || isOnScreen(screenEnd)) {
			graphics.drawLine(round(screenStart.x()), round(screenStart.y()),
							  round(screenEnd.x()), round(screenEnd.y()));
		}
	}
	
	/**
	 * Dessine les lignes de champ paramétrées en x ou y.
	 * @param graphics Contexte graphique dans lequel dessiner
	 */
	private void drawXYFields(Graphics graphics) {
		for (XYFuncField field : xyFields) {
			double min, max;
			
			//Détermine les bornes selon si c'est y = f(x) ou x = f(y)
			if (field.type == XYFuncField.Type.X) {
				min = offset.x();
				max = offset.x() + getWidth()/zoom;
			} else {
				min = offset.y();
				max = offset.y() + getHeight()/zoom;
			}
			
			for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
				graphics.setColor(field.color());
				if (type == RenderType.LINE || type == RenderType.RECT_LINE) {
					Vector2D previousPoint = null;
					
					Vector2D actualPoint;
					
					if (field.type() == XYFuncField.Type.X) {
						for (double x = min; x <= max; x += field.step()) {
							try {
								actualPoint = new Vector2D(x, field.compute(x, constant));
								if (previousPoint != null)
									drawLine(graphics, previousPoint, actualPoint);
								previousPoint = actualPoint;
							} catch (ArithmeticException e) { }
						}
					} else {
						for (double y = min; y <= max; y += field.step()) {
							try {
								actualPoint = new Vector2D(field.compute(y, constant), y);
								if (previousPoint != null)
									drawLine(graphics, previousPoint, actualPoint);
								previousPoint = actualPoint;
							} catch (ArithmeticException e) { }
						}
					}
				}
				
				if (type != RenderType.LINE) {
					if (field.type() == XYFuncField.Type.X) {
						for (double x = min; x <= max; x += field.step()) {
							try {
								drawPoint(graphics, x, field.compute(x, constant));
							} catch (ArithmeticException e) { }
						}
					} else {
						for (double y = min; y <= max; y += field.step()) {
							try {
								drawPoint(graphics, field.compute(y, constant), y);
							} catch (ArithmeticException e) { }
						}
					}
				}
			}
		}
	}
	
	/**
	 * Dessine les lignes de champ paramétrées en t.
	 * @param graphics Contexte graphique dans lequel dessiner
	 */
	private void drawTFields(Graphics graphics) {
		for (TFuncField field : tFields) {
			for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
				graphics.setColor(field.color());
				if (type == RenderType.LINE || type == RenderType.RECT_LINE) {
					Vector2D previousPoint = field.compute(field.startT(), constant); //TODO : gérer arithmetic exception
					Vector2D actualPoint;
					for (double t = field.startT() + field.step(); t <= field.endT(); t += field.step()) {
						try {
							actualPoint = field.compute(t, constant);
							drawLine(graphics, previousPoint, actualPoint);
							previousPoint = actualPoint;
						} catch (ArithmeticException e) { }
					}
				}
				
				if (type != RenderType.LINE) {
					for (double t = field.startT() + field.step(); t <= field.endT(); t += field.step()) {
						try {
							drawPoint(graphics, field.compute(t, constant));
						} catch (ArithmeticException e) { }
					}
				}
			}
		}
	}
	
	/**
	 * Dessine les champs de force.
	 * @param graphics Contexte graphique dans lequel dessiner
	 */
	private void drawForceFields(Graphics graphics) {

		for (Vector2D startingPoint : startingPoints)
		{
			for (ForceField field : forceFields) {
				graphics.setColor(field.color());
				
				if (type == RenderType.LINE || type == RenderType.RECT_LINE) {
					Vector2D previousPoint = startingPoint;
					Vector2D actualPoint;
					for (int i = 0; i < numberOfPoints; i++) {
						try {
							actualPoint = field.nextPoint(previousPoint);
							drawLine(graphics, previousPoint, actualPoint);
							previousPoint = actualPoint;
						} catch (ArithmeticException e) {}
					}
				}
				
				if (type != RenderType.LINE) {
					drawPoint(graphics, startingPoint);
					Vector2D actualPoint = startingPoint;
					for (int i = 0; i < numberOfPoints; i++) {
						try {
							actualPoint = field.nextPoint(actualPoint);
							drawPoint(graphics, actualPoint);
						} catch (ArithmeticException e) {}
					}
				}
			}
		}
	}
	
	/**
	 * Dessine le chemin de la circulation.
	 * @param graphics Contexte graphique dans lequel dessiner
	 */
	private void drawLineIntegral(Graphics graphics) {
		if (!lineIntegralVisibility)
			return;
		
		Vector2D screen = toScreen(lineIntegralStartingPoint);
		
		if (screen.isNaN())
			return;
		
		int screenX = round(screen.x());
		int screenY = round(screen.y());
		int screenRadius = round(lineIntegralRadius*zoom);
		int screenDiameter = round(2.*lineIntegralRadius*zoom);
		
		graphics.setColor(lineIntegralColor);
		
		if (lineIntegralCircle)
			graphics.drawArc(screenX, screenY - screenRadius, screenDiameter, screenDiameter, 180, -90); // (x,y) = top-left corner
		else
			graphics.drawLine(screenX, screenY, screenX + screenRadius, screenY - screenRadius);
	}
	
    @Override
    public void mousePressed(MouseEvent event) {
        lastMousePosition.set(event.getX(), event.getY());
    }
	
	@Override
	public void mouseDragged(MouseEvent event) {
		if ((event.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) //Le bouton de gauche n'est pas enfoncé
			return;
		
		Vector2D actualMousePosition = new Vector2D(event.getX(), event.getY());
		Vector2D difference = Vector2D.multiply(Vector2D.sub(actualMousePosition, lastMousePosition), 1./zoom);
		offset.sub(difference.x(), -difference.y()); //y inversé
		lastMousePosition = actualMousePosition;
		setCursor(new Cursor(Cursor.MOVE_CURSOR));
		update();
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		// Seul le clic gauche déclenche le changement de curseur donc c'est seulement quand il est relâché que l'on remet
		if (event.getButton() == MouseEvent.BUTTON1) 
		{
			if (settingPointMode)
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); // Curseur en croix
			else
				setCursor(Cursor.getDefaultCursor());
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
	    lastMousePosition.set(event.getX(), event.getY());
	    updateZoom(Math.pow(1.25, -event.getWheelRotation()));
	}
	
	/**
	 * Modifie l'agrandissement et la position de la vue.
	 * @param scaleFactor Facteur d'agrandissement par rapport à l'agrandissement actuel
	 */
	private void updateZoom(double scaleFactor) {
	    if (zoom * scaleFactor < 1./10000)
	    	return;
	    
	    /* On soustrait la différence entre la position de la souris
	     * (dans le système de coordonnées du graphe) après le zoom et avant le zoom
	     * Ainsi, le point du graphe en dessous de la souris le reste après le zoom
	     * Offset -= PosSourisGrapheAprès - PosSourisGrapheAvant
	     * 		  -= PosSouris/ZoomAprès - PosSouris/ZoomAvant
	     * 		  -= PosSouris/ZoomAvant/FacteurDeZoom - PosSouris/ZoomAvant 
	     * 		  -= PosSouris/ZoomAvant * (1/Facteur de Zoom - 1)
	     * (Pas besoin de tenir compte de l'offset car la soustraction l'annule) */
	    
	    offset.setX(offset.x() - lastMousePosition.x()/zoom*(1./scaleFactor - 1));
	    offset.setY(offset.y() - (getHeight()-lastMousePosition.y())/zoom*(1./scaleFactor - 1)); // Attention à l'inversion des Y
	    zoom *= scaleFactor;
	    update();
	}

	@Override
	public void mouseClicked(MouseEvent event) {

	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}
	
	public Rectangle2D viewBounds() {
		return new Rectangle2D.Double(offset.x(), offset.y(), getWidth()/zoom, getHeight()/zoom);
	}
}
