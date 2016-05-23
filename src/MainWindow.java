/**
 * @author Paul Du
 *
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow implements ChangeListener, ActionListener, MouseListener {
	private JFrame window;
	private JSplitPane splitPane;
	private JPanel sidePanel;
	private FieldViewPanel fieldViewPanel;
	
	private JSpinner analyticStep;
	private JSpinner numericStep;
	private JSpinner constantStart;
	private JSpinner constantEnd;
	private JSpinner constantStep;
	private JList<Vector2D> startingPointList;
	private JToggleButton addPoint;
	private JButton removePoint;
	
	private YFuncField yField;
	private ForceField forceField;
	
	public class Vector2DListModel extends AbstractListModel<Vector2D> {
		List<Vector2D> vectors;

		public Vector2DListModel() {
			vectors = new ArrayList<Vector2D>();
		}
		
		@Override
		public Vector2D getElementAt(int index) {
			return vectors.get(index);
		}

		@Override
		public int getSize() {
			return vectors.size();
		}
		
		public void addElement(Vector2D element) {
			vectors.add(element);
			fireIntervalAdded(this, vectors.size()-1, vectors.size()-1);
		}
		
		public void remove(int index) {
			vectors.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		
		public List<Vector2D> getList() {
			return vectors;
		}
	}
	
	MainWindow(YFuncField yField, ForceField forceField) {
		window = new JFrame();
		window.setTitle("Force Field Vizualizer");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.yField = yField;
		this.forceField = forceField;
		
		sidePanel = new JPanel(new GridLayout(0, 2, 5, 5));
		sidePanel.add(new JLabel("Pas analytique : "));
		analyticStep = new JSpinner(new SpinnerNumberModel(yField.step(), 0.001, 10, 0.001));
		sidePanel.add(analyticStep);
		sidePanel.add(new JLabel("Constante début : "));
		constantStart = new JSpinner(new SpinnerNumberModel(yField.constantStart(), -10., 10., 1.));
		sidePanel.add(constantStart);
		sidePanel.add(new JLabel("Constante fin :"));
		constantEnd = new JSpinner(new SpinnerNumberModel(yField.constantEnd(), -10., 10., 1.));
		sidePanel.add(constantEnd);
		sidePanel.add(new JLabel("Pas de la constante :"));
		constantStep = new JSpinner(new SpinnerNumberModel(yField.constantStep(), 0.01, 10, 0.01));
		sidePanel.add(constantStep);
		sidePanel.add(new JLabel("Pas numérique : "));
		numericStep = new JSpinner(new SpinnerNumberModel(forceField.step(), 0.001, 10, 0.001));
		sidePanel.add(numericStep);
		sidePanel.add(new JLabel("Points de départ :"));
		startingPointList = new JList<Vector2D>(new Vector2DListModel());
		sidePanel.add(startingPointList);
		addPoint = new JToggleButton("+");
		sidePanel.add(addPoint);
		removePoint = new JButton("-");
		sidePanel.add(removePoint);

		sidePanel.add(new JLabel("Circulation théorique :"));
		final double R = 10.;
		final double circulation = Math.pow(R, 3.)*(Math.PI/4.-1./3.)+ R*R/2. + 2.*R;
		sidePanel.add(new JLabel(String.valueOf(circulation)));
		
		((Vector2DListModel) startingPointList.getModel()).addElement(new Vector2D(0.5, 0));
		
		fieldViewPanel = new FieldViewPanel();
		fieldViewPanel.addField(yField);
		fieldViewPanel.addField(forceField);
		fieldViewPanel.addMouseListener(this);
		fieldViewPanel.setStartingPoints(((Vector2DListModel) startingPointList.getModel()).getList());
		
		JPanel wrapSidePanel = new JPanel(); //Pour éviter l'étirement du GridLayout
		wrapSidePanel.add(sidePanel);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wrapSidePanel, fieldViewPanel);
		window.getContentPane().add(splitPane);
		
		analyticStep.addChangeListener(this);
		numericStep.addChangeListener(this);
		constantStart.addChangeListener(this);
		constantEnd.addChangeListener(this);
		constantStep.addChangeListener(this);

		addPoint.addActionListener(this);
		removePoint.addActionListener(this);
		
		window.pack();
		window.setVisible(true);
	}
	
	public FieldViewPanel fieldViewPanel() {
		return fieldViewPanel;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == analyticStep)
			yField.setStep((double) analyticStep.getValue());
		else if (event.getSource() == numericStep)
			forceField.setStep((double) numericStep.getValue());
		else if (event.getSource() == constantStart) {
			yField.setConstantStart((double) constantStart.getValue());
			if ((double)constantStart.getValue() > (double)constantEnd.getValue())
				constantEnd.setValue(constantStart.getValue());
		}		
		else if (event.getSource() == constantEnd) {
			yField.setConstantEnd((double) constantEnd.getValue());
			if ((double)constantEnd.getValue() < (double)constantStart.getValue())
				constantStart.setValue(constantEnd.getValue());
		}
		else if (event.getSource() == constantStep)
			yField.setConstantStep((double) constantStep.getValue());
		
		fieldViewPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == removePoint) {
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			int[] selected = startingPointList.getSelectedIndices();
			for (int i = selected.length-1; i >= 0; i--) {
				list.remove(selected[i]);
			}
			fieldViewPanel.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (addPoint.isSelected()) {
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			list.addElement(fieldViewPanel.getLastMousePositionOnGraph());
			addPoint.setSelected(false);
			fieldViewPanel.repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
