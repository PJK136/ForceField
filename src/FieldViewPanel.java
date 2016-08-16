/**
 * @author Paul Du
 *
 */

import java.awt.Color;
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
	
	public List<Vector2D> points;
	public List<YFuncField> yFields;
	public List<ForceField> forceFields;
	
	public List<Vector2D> startingPoints;
	
	public FieldViewPanel() {
		lastMousePosition = new Vector2D();
		setPreferredSize(new Dimension(800, 600));
		setBackground(Color.white);
		
		offset = new Vector2D(-2.5,-2.5);
		zoom = 100;
		
		points = new ArrayList<Vector2D>();
		yFields = new ArrayList<YFuncField>();
		forceFields = new ArrayList<ForceField>();
		
		startingPoints = new ArrayList<Vector2D>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	public void addPoint(Vector2D point) {
		points.add(point);
	}
	
	public void addField(YFuncField field) {
		yFields.add(field);
	}
	
	public void addField(ForceField field) {
		forceFields.add(field);
	}
	
	public static long clamp(long value, long min, long max) {
	    return Math.max(min, Math.min(max, value));
	}
	
	private double toScreenX(double x) {
		return (x - offset.x())*zoom;
	}

	private double toScreenY(double y) {
		return getHeight() - (y - offset.y())*zoom;
	}
	
	private boolean isOnScreenX(double screenX) {
		return !Double.isNaN(screenX) && screenX > -100 && screenX < getWidth()+100;
	}
	
	private boolean isOnScreenY(double screenY) {
		return !Double.isNaN(screenY) && screenY > -100 && screenY < getHeight()+100;
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
			graphics.drawLine(screenXInt, screenYInt-1, screenXInt, screenYInt+1); //Dessiner des lignes est plus rapide
			graphics.drawLine(screenXInt-1, screenYInt, screenXInt-1, screenYInt); //que dessiner des ovales ou rectangles
			//graphics.drawRect(screenXInt-1, screenYInt-1, 3, 3);
			//graphics.drawOval((int) Math.round(screenX)-1, (int) Math.round(screenY)-1, 3, 3);
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
	
	private void drawYFields(Graphics graphics) {
		graphics.setColor(Color.green);
		
		double min = offset.y();
		double max = offset.y() + getHeight()/zoom;
		
		for (YFuncField field : yFields) {
			for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
				for (double y = min; y <= max; y += field.step()) {
					drawPoint(graphics, field.compute(y, constant), y);
				}
			}
		}
	}
	
	private void drawForceFields(Graphics graphics) {
		graphics.setColor(Color.red);

		for (Vector2D startingPoint : startingPoints)
		{
			for (ForceField field : forceFields) {
				for (double constant = field.constantStart(); constant <= field.constantEnd(); constant += field.constantStep()) {
					Vector2D actualPoint = startingPoint;
					for (int i = 0; i < 1000; i++) {
						actualPoint = field.nextPoint(actualPoint, constant);
						drawPoint(graphics, actualPoint);
					}
				}
			}
		}
	}
	
	public Vector2D getLastMousePositionOnGraph() {
		return new Vector2D(lastMousePosition.x()/zoom + offset.x(),
							(getHeight() - lastMousePosition.y())/zoom + offset.y()); // Inversion des Y
	}
	
	public void setStartingPoints(List<Vector2D> startingPoints) {
		this.startingPoints = startingPoints;
		repaint();
	}
	
    @Override
    public void mousePressed(MouseEvent event) {
        lastMousePosition.set(event.getX(), event.getY());
    }
	
	@Override
	public void mouseDragged(MouseEvent event) {
		Vector2D actualMousePosition = new Vector2D(event.getX(), event.getY());
		Vector2D difference = Vector2D.multiply(Vector2D.sub(actualMousePosition, lastMousePosition), 1./zoom);
		offset.sub(difference.x(), -difference.y()); //y inversé
		lastMousePosition = actualMousePosition;
		repaint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
	    lastMousePosition.set(event.getX(), event.getY());
	    updateZoom(Math.pow(1.25, -event.getWheelRotation()));
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			if (event.getButton() == MouseEvent.BUTTON1)
				updateZoom(1.25*1.25*1.25);
			else if (event.getButton() == MouseEvent.BUTTON3)
				updateZoom(1./(1.25*1.25*1.25));
		}
	}
	
	private void updateZoom(double scaleFactor) {
	    if (zoom * scaleFactor < 1./10000)
	    	return;
	    
	    /* On soustrait la différence entre la position de la souris
	     * (par rapport à la portion du graphe affiché) après le zoom et avant le zoom
	     * Ainsi, le point du graphe en dessous de la souris le reste après le zoom
	     * Offset -= PosSourisGrapheAprès - PosSourisGrapheAvant
	     * 		  -= PosSouris/ZoomAprès - PosSouris/ZoomAvant
	     * 		  -= PosSouris/ZoomAvant/FacteurDeZoom - PosSouris/ZoomAvant 
	     * 		  -= PosSouris/ZoomAvant * (1/Facteur de Zoom - 1) */
	    
	    offset.setX(offset.x() - lastMousePosition.x()/zoom*(1./scaleFactor - 1));
	    offset.setY(offset.y() - (getHeight()-lastMousePosition.y())/zoom*(1./scaleFactor - 1)); // Attention à l'inversion des Y
	    zoom *= scaleFactor;
	    repaint();
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

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
}
