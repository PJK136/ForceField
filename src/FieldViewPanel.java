/**
 * @author Paul Du
 *
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class FieldViewPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private Vector2D lastMousePosition;
	
	private Vector2D offset;
	private double zoom;
	
	public enum RenderType {
		CROSS,
		RECTANGLE,
		OVAL,
		LINE,
		RECT_LINE
	}
	
	RenderType type;
	
	private List<Vector2D> points;
	private List<YFuncField> yFields;
	private List<ForceField> forceFields;
	
	private int numberOfPoints;
	private List<Vector2D> startingPoints;
	
	private Vector2D lineIntegralStartingPoint;
	private boolean lineIntegralVisibility;
	private boolean lineIntegralCircle;
	private double lineIntegralRadius;
	
	private Color yColor;
	private Color forceColor;
	private Color lineIntegralColor;
	
	public FieldViewPanel() {
		super();
		lastMousePosition = new Vector2D();
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.white);
		
		reset();
		type = RenderType.CROSS;
		
		points = new ArrayList<Vector2D>();
		yFields = new ArrayList<YFuncField>();
		forceFields = new ArrayList<ForceField>();
		
		numberOfPoints = 1000;
		startingPoints = new ArrayList<Vector2D>();
		
		lineIntegralStartingPoint = new Vector2D();
		lineIntegralVisibility = false;
		lineIntegralCircle = true;
		lineIntegralRadius = 10.;
		
		yColor = Color.green;
		forceColor = Color.red;
		lineIntegralColor = Color.blue;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void addPoint(Vector2D point) {
		points.add(point);
		update();
	}
	
	public void addField(YFuncField field) {
		yFields.add(field);
		update();
	}
	
	public void addField(ForceField field) {
		forceFields.add(field);
		update();
	}
	
	public void clearYFuncFields() {
		yFields.clear();
		update();
	}
	
	public void clearForceFields() {
		forceFields.clear();
		update();
	}
	
	public void setRenderType(RenderType type) {
		this.type = type;
		update();
	}
	
	public void update() {
		repaint();
	}
	
	public void reset() {
		offset = new Vector2D(-2.5,-2.5);
		zoom = 100;
		update();
	}
	
	public Vector2D getLastMousePositionOnGraph() {
		return new Vector2D(lastMousePosition.x()/zoom + offset.x(),
							(getHeight() - lastMousePosition.y())/zoom + offset.y()); // Inversion des Y
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
	
	public void setLineIntegralStartingPoint(Vector2D startingPoint) {
		lineIntegralStartingPoint = startingPoint;
		update();
	}
	
	public void setLineIntegralCircle(boolean circle) {
		lineIntegralCircle = circle;
		update();
	}
	
	public void setLineIntegralRadius(double radius) {
		if (radius >= 0)
		{
			lineIntegralRadius = radius;
			update();
		}
	}
	
	public Color yColor() {
		return yColor;
	}
	
	public Color forceColor() {
		return forceColor;
	}
	
	public Color lineIntegralColor() {
		return lineIntegralColor;
	}
	
	public void setYColor(Color color) {
		yColor = color;
		update();
	}
	
	public void setForceColor(Color color) {
		forceColor = color;
		update();
	}
	
	public void setLineIntegralColor(Color color) {
		lineIntegralColor = color;
		update();
	}
	
	public static long clamp(long value, long min, long max) {
	    return Math.max(min, Math.min(max, value));
	}
	
	public static int round(double value) {
		return (int) Math.round(value);
	}
	
	private double toScreenX(double x) {
		return (x - offset.x())*zoom;
	}

	private double toScreenY(double y) {
		return getHeight() - (y - offset.y())*zoom;
	}
	
	private Vector2D toScreen(Vector2D point) {
		return new Vector2D(toScreenX(point.x()), toScreenY(point.y()));
	}
	
	private boolean isOnScreenX(double screenX) {
		return !Double.isNaN(screenX) && screenX > -100 && screenX < getWidth()+100;
	}
	
	private boolean isOnScreenY(double screenY) {
		return !Double.isNaN(screenY) && screenY > -100 && screenY < getHeight()+100;
	}
	
	private boolean isOnScreen(Vector2D screenPoint) {
		return isOnScreenX(screenPoint.x()) && isOnScreenY(screenPoint.y());
	}
	
	private int clampToScreenX(double x) {
		return (int) clamp(Math.round(toScreenX(x)), -100, getWidth()+100); // Attention, Math.round -> long
	}

	private int clampToScreenY(double y) {
		return (int) clamp(Math.round(toScreenY(y)), -100, getHeight()+100); //Inversion des Y
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		drawAxes(graphics);
		
		drawPoints(graphics);
		
		drawYFields(graphics);
		
		drawForceFields(graphics);
		
		drawLineIntegral(graphics);
	}

	private void drawAxes(Graphics graphics){
		graphics.setColor(new Color(0,125,0));
		//graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		final int ScreenX0 = clampToScreenX(0);
		final int ScreenY0 = clampToScreenY(0);
		
		// Axe horizontal
		graphics.drawLine(0, ScreenY0, getWidth(), ScreenY0);
		graphics.drawLine(getWidth()-10, ScreenY0-5, getWidth(), ScreenY0);
		graphics.drawLine(getWidth()-10, ScreenY0+5, getWidth(), ScreenY0);
		
		// Axe vertical
		graphics.drawLine(ScreenX0, 0, ScreenX0, getHeight());
		graphics.drawLine(ScreenX0-5, 10, ScreenX0, 0);
		graphics.drawLine(ScreenX0+5, 10, ScreenX0, 0);
		
		// Origine du repère
		graphics.drawString("O", ScreenX0 - 12, ScreenY0 + graphics.getFontMetrics().getHeight() + 10);
		
		final double SPACING = 75.;
		
		//Graduation des abscisses
	    double min = Math.floor(offset.x());
	    double max = Math.ceil(offset.x()+getWidth()/zoom);
		int step = (int)Math.ceil((max-min)/(getWidth()/SPACING));
		
		for (int i = -step; i >= min; i -= step)
			drawXAxisTick(graphics, i, ScreenY0);
		
		for (int i = step; i <= max; i += step)
			drawXAxisTick(graphics, i, ScreenY0);
		
		//Graduation des ordonnées
	    min = Math.floor(offset.y());
	    max = Math.ceil(offset.y()+getHeight()/zoom);
	    step = (int)Math.ceil((max-min)/(getHeight()/SPACING));
		
		for (int i = -step; i >= min; i -= step)
			drawYAxisTick(graphics, i, ScreenX0);
		
		for (int i = step; i <= max; i += step)
			drawYAxisTick(graphics, i, ScreenX0);
	}
	
	private void drawXAxisTick(Graphics graphics, int value, int ScreenY0) {
		String number = String.valueOf(value);
		graphics.drawLine(clampToScreenX(value), ScreenY0-5, clampToScreenX(value), ScreenY0+5);
		graphics.drawString(number, clampToScreenX(value) - graphics.getFontMetrics().stringWidth(number)/2,
							ScreenY0 + graphics.getFontMetrics().getHeight() + 10);
	}
	
	private void drawYAxisTick(Graphics graphics, int value, int ScreenX0) {
		String number = String.valueOf(value);
		graphics.drawLine(ScreenX0-5, clampToScreenY(value), ScreenX0 + 5, clampToScreenY(value));
		graphics.drawString(number, ScreenX0 - graphics.getFontMetrics().stringWidth(number) - 10,
							clampToScreenY(value) + graphics.getFontMetrics().getHeight()/2);
	}
	
	private void drawPoint(Graphics graphics, double x, double y) {
		double screenX = toScreenX(x);
		double screenY = toScreenY(y);
		if (isOnScreenX(screenX) && isOnScreenY(screenY))
		{
			int screenXInt = (int) Math.round(screenX);
			int screenYInt = (int) Math.round(screenY);
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
				graphics.drawOval((int) Math.round(screenX)-1, (int) Math.round(screenY)-1, 3, 3);
				break;
			case RECT_LINE:
				graphics.drawRect(screenXInt-2, screenYInt-2, 5, 5);
			}
		}
	}
	
	private void drawPoint(Graphics graphics, Vector2D point) {
		drawPoint(graphics, point.x(), point.y());
	}
	
	private void drawPoints(Graphics graphics) {
		graphics.setColor(Color.gray);
		for (Vector2D p : points) {
			drawPoint(graphics, p);
		}
	}
	
	private void drawLine(Graphics graphics, Vector2D start, Vector2D end) {
		Vector2D screenStart = toScreen(start);
		Vector2D screenEnd = toScreen(end);
		if (isOnScreen(screenStart) || isOnScreen(screenEnd)) {
			graphics.drawLine(round(screenStart.x()), round(screenStart.y()),
							  round(screenEnd.x()), round(screenEnd.y()));
		}
	}
	
	private void drawYFields(Graphics graphics) {
		graphics.setColor(yColor);
		
		double min = offset.y();
		double max = offset.y() + getHeight()/zoom;
		
		for (YFuncField field : yFields) {
			for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
				if (type == RenderType.LINE || type == RenderType.RECT_LINE) {
					Vector2D previousPoint = new Vector2D(field.compute(min, constant), min);
					Vector2D actualPoint;
					for (double y = min + field.step(); y <= max; y += field.step()) {
						actualPoint = new Vector2D(field.compute(y, constant), y);
						drawLine(graphics, previousPoint, actualPoint);
						previousPoint = actualPoint;
					}
				}
				
				if (type != RenderType.LINE) {
					for (double y = min; y <= max; y += field.step())
						drawPoint(graphics, field.compute(y, constant), y);
				}
			}
		}
	}
	
	private void drawForceFields(Graphics graphics) {
		graphics.setColor(forceColor);

		for (Vector2D startingPoint : startingPoints)
		{
			for (ForceField field : forceFields) {
				for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
					if (type == RenderType.LINE || type == RenderType.RECT_LINE) {
						Vector2D previousPoint = startingPoint;
						Vector2D actualPoint;
						for (int i = 0; i < numberOfPoints; i++) {
							actualPoint = field.nextPoint(previousPoint, constant);
							drawLine(graphics, previousPoint, actualPoint);
							previousPoint = actualPoint;
						}
					}
					
					if (type != RenderType.LINE) {
						drawPoint(graphics, startingPoint);
						Vector2D actualPoint = startingPoint;
						for (int i = 0; i < numberOfPoints; i++) {
							actualPoint = field.nextPoint(actualPoint, constant);
							drawPoint(graphics, actualPoint);
						}
					}
				}
			}
		}
	}
	
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
		if (event.getButton() == MouseEvent.BUTTON1) // Seul le clic gauche déclenche le changement de curseur
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Donc c'est seulement quand il est relâché que l'on remet
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
	    lastMousePosition.set(event.getX(), event.getY());
	    updateZoom(Math.pow(1.25, -event.getWheelRotation()));
	}
	
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
}
