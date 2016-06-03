/**
 * @author Paul Du
 *
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainWindow implements ChangeListener, MouseListener, ItemListener, ActionListener {
	private JFrame window;
	private JSplitPane splitPane;
	private JPanel sidePanel;
	private FieldViewPanel fieldViewPanel;
	
	private JComboBox<String> renderType;
	private JSpinner parameterC;
	private JButton reset;
	
	private JCheckBox analytic;
	private JSpinner analyticStep;
	private JSpinner constantStart;
	private JSpinner constantEnd;
	private JSpinner constantStep;
	private JButton analyticColor;
	
	private JCheckBox numeric;
	private JSpinner numericStep;
	private JSpinner numberOfPoints;
	private JList<String> startingPointList;
	private JToggleButton addPoint;
	private JButton removePoint;
	private JButton numericColor;
	
	private JCheckBox lineIntegral;
	private JComboBox<String> lineIntegralType;
	private JSpinner lineIntegralRadius;
	private JSpinner lineIntegralStep;
	private JLabel lineIntegralStartingPointLabel;
	private JToggleButton lineIntegralSetStartingPoint;
	private JLabel lineIntegralAnalytic;
	private JLabel lineIntegralNumeric;
	private JLabel lineIntegralAbsoluteError;
	private JLabel lineIntegralRelativeError;
	private Vector2D lineIntegralStartingPoint;
	private JButton lineIntegralColor;
	
	private YFuncField yField;
	private ForceField forceField;
	
	public class Vector2DListModel extends AbstractListModel<String> {
		List<Vector2D> vectors;

		public Vector2DListModel() {
			vectors = new ArrayList<Vector2D>();
		}
		
		@Override
		public String getElementAt(int index) {
			return vectors.get(index).toStringNdecimals(2);
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
		
		sidePanel = new JPanel(new GridBagLayout());
		
		renderType = new JComboBox<String>(new String[] {"Croix", "Rectangle", "Ovale", "Ligne", "Rectangle & ligne"});
		addLabelComponentToSidePanel("Type de Rendu", renderType);
		
		parameterC = new JSpinner(new SpinnerNumberModel(yField.parameter(), .01, 5., 0.01));
		addLabelComponentToSidePanel("Paramètre c :", parameterC);
		
		reset = new JButton("Réinitialiser le graphe");
		addOneComponentToSidePanel(reset);
		
		addSpaceToSidePanel();
		
		analytic = new JCheckBox("Modélisation analytique : ", false);
		analytic.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(analytic);
		
		analyticStep = new JSpinner(new SpinnerNumberModel(yField.step(), 0.001, 10, 0.001));
		addLabelComponentToSidePanel("Pas analytique :", analyticStep);
		
		constantStart = new JSpinner(new SpinnerNumberModel(yField.constantStart(), -100., 100., 1.));
		addLabelComponentToSidePanel("Constante début :", constantStart);
		
		constantEnd = new JSpinner(new SpinnerNumberModel(yField.constantEnd(), -100., 100., 1.));
		addLabelComponentToSidePanel("Constante fin :", constantEnd);
		
		constantStep = new JSpinner(new SpinnerNumberModel(yField.constantStep(), 0.01, 10, 0.01));
		addLabelComponentToSidePanel("Pas de constante :", constantStep);
		
		analyticColor = new JButton("Changer...");
		addLabelComponentToSidePanel("Couleur de la courbe :", analyticColor);

		addSpaceToSidePanel();
		
		numeric = new JCheckBox("Modélisation numérique : ", true);
		numeric.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(numeric);
		
		numericStep = new JSpinner(new SpinnerNumberModel(forceField.step(), 0.001, 10, 0.001));
		addLabelComponentToSidePanel("Pas numérique :", numericStep);
		
		numberOfPoints = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 100));
		addLabelComponentToSidePanel("Nombre de points :", numberOfPoints);
		
		startingPointList = new JList<String>(new Vector2DListModel());
		addLabelComponentToSidePanel("Points de départ :", new JScrollPane(startingPointList));
		
		addPoint = new JToggleButton("+");
		removePoint = new JButton("-");
		addTwoComponentsToSidePanel(addPoint, removePoint);
		
		numericColor = new JButton("Changer...");
		addLabelComponentToSidePanel("Couleur de la courbe :", numericColor);
		
		addSpaceToSidePanel();
		
		lineIntegral = new JCheckBox("Circulation :", true);
		lineIntegral.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(lineIntegral);
		
		lineIntegralType = new JComboBox<String>(new String[] {"Arc de cercle", "Droite"});
		addLabelComponentToSidePanel("Ligne :", lineIntegralType);
		
		lineIntegralRadius = new JSpinner(new SpinnerNumberModel(10., 0., 100., 1.));
		addLabelComponentToSidePanel("Rayon :", lineIntegralRadius);
		
		lineIntegralStep = new JSpinner(new SpinnerNumberModel(0.001, 0.000001, 1, 0.000001));
		//Modifie la précision de la valeur stockée
		lineIntegralStep.setEditor(new JSpinner.NumberEditor(lineIntegralStep, "0.000000"));
		addLabelComponentToSidePanel("Pas :", lineIntegralStep);
		
		lineIntegralStartingPoint = new Vector2D();
		lineIntegralStartingPointLabel = new JLabel(lineIntegralStartingPoint.toString());
		addLabelComponentToSidePanel("Point de départ :", lineIntegralStartingPointLabel);
		
		lineIntegralSetStartingPoint = new JToggleButton("Définir le point de départ");
		addOneComponentToSidePanel(lineIntegralSetStartingPoint);
		
		lineIntegralAnalytic = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Circulation analytique :", lineIntegralAnalytic);
		
		lineIntegralNumeric = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Circulation numérique :", lineIntegralNumeric);
		
		lineIntegralAbsoluteError = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Erreur absolue :", lineIntegralAbsoluteError);
		
		lineIntegralRelativeError = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Erreur relative :", lineIntegralRelativeError);
		
		lineIntegralColor = new JButton("Changer...");
		addLabelComponentToSidePanel("Couleur de la courbe :", lineIntegralColor);
		
		addSpaceToSidePanel();
		
		((Vector2DListModel) startingPointList.getModel()).addElement(new Vector2D(0.5, 0));
		
		{ //Pousse le contenu vers le haut
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.weighty = 1;
			sidePanel.add(Box.createGlue(), constraints);
		}
		
		fieldViewPanel = new FieldViewPanel();
		// /!\ Le tableau est partagé startingPointList et fielViewPanel 
		fieldViewPanel.setStartingPoints(((Vector2DListModel) startingPointList.getModel()).getList()); 
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, fieldViewPanel);
		window.getContentPane().add(splitPane);
		
		renderType.addItemListener(this);
		parameterC.addChangeListener(this);
		reset.addActionListener(this);
		
		analytic.addItemListener(this);
		analyticStep.addChangeListener(this);
		constantStart.addChangeListener(this);
		constantEnd.addChangeListener(this);
		constantStep.addChangeListener(this);
		analyticColor.addActionListener(this);

		numeric.addItemListener(this);
		numericStep.addChangeListener(this);
		numberOfPoints.addChangeListener(this);
		removePoint.addActionListener(this);
		numericColor.addActionListener(this);

		lineIntegral.addItemListener(this);
		lineIntegralType.addItemListener(this);
		lineIntegralRadius.addChangeListener(this);
		lineIntegralStep.addChangeListener(this);
		
		fieldViewPanel.addMouseListener(this);
		
		analytic.setSelected(true);
		numeric.setSelected(false);
		lineIntegral.setSelected(false);
		lineIntegralColor.addActionListener(this);
		
		window.pack();
		window.setVisible(true);
	}
	
	private void addComponentsToSidePanel(Component component1, Component component2) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(3, 3, 3, 3);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		sidePanel.add(component1, constraints);
		constraints.weightx = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		sidePanel.add(component2, constraints);
	}
	
	private void addLabelComponentToSidePanel(String label, Component component) {
		addComponentsToSidePanel(new JLabel(label), component);
	}
	
	private void addOneComponentToSidePanel(Component component) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		sidePanel.add(component, constraints);
	}
	
	private void addTwoComponentsToSidePanel(Component component1, Component component2) {
		//Fait en sorte que les deux composants aient la même largeur
		JPanel wrapComponents = new JPanel(new GridLayout(1, 2));
		wrapComponents.add(component1);
		wrapComponents.add(component2);
		addOneComponentToSidePanel(wrapComponents);
	}
	
	private void addSpaceToSidePanel() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.weighty = 0.05;
		sidePanel.add(Box.createGlue(), constraints);
	}
	
	public FieldViewPanel fieldViewPanel() {
		return fieldViewPanel;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == parameterC) {
			yField.setParameter((double) parameterC.getValue());
			forceField.setParameter((double) parameterC.getValue());
			updateLineIntegral();
			fieldViewPanel.update();
		} else if (event.getSource() == analyticStep)
			yField.setStep((double) analyticStep.getValue());
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
		else if (event.getSource() == numericStep)
			forceField.setStep((double) numericStep.getValue());
		else if (event.getSource() == numberOfPoints)
			fieldViewPanel.setNumberOfPoints((int) numberOfPoints.getValue());
		else if (event.getSource() == lineIntegralRadius) {
			fieldViewPanel.setLineIntegralRadius((double) lineIntegralRadius.getValue());
			updateLineIntegral();
		} else if (event.getSource() == lineIntegralStep) {
			updateLineIntegral();
		}
		
		fieldViewPanel.update();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getSource() == renderType) {
			switch (renderType.getSelectedIndex()) {
			default:
			case 0:
				fieldViewPanel.setRenderType(FieldViewPanel.RenderType.CROSS);
				break;
			case 1:
				fieldViewPanel.setRenderType(FieldViewPanel.RenderType.RECTANGLE);
				break;
			case 2:
				fieldViewPanel.setRenderType(FieldViewPanel.RenderType.OVAL);
				break;
			case 3:
				fieldViewPanel.setRenderType(FieldViewPanel.RenderType.LINE);
				break;
			case 4:
				fieldViewPanel.setRenderType(FieldViewPanel.RenderType.RECT_LINE);
				break;
			}
		} else if (event.getSource() == analytic) {
			boolean enabled = analytic.isSelected();
			analyticStep.setEnabled(enabled);
			constantStart.setEnabled(enabled);
			constantEnd.setEnabled(enabled);
			constantStep.setEnabled(enabled);
			
			if (enabled)
				fieldViewPanel.addField(yField);
			else
				fieldViewPanel.clearYFuncFields();
		} else if (event.getSource() == numeric) {
			boolean enabled = numeric.isSelected();
			numericStep.setEnabled(enabled);
			numberOfPoints.setEnabled(enabled);
			addPoint.setEnabled(enabled);
			removePoint.setEnabled(enabled);
			startingPointList.setEnabled(enabled);
			
			if (enabled)
				fieldViewPanel.addField(forceField);
			else
				fieldViewPanel.clearForceFields();
		} else if (event.getSource() == lineIntegral) {
			boolean enabled = lineIntegral.isSelected();
			lineIntegralType.setEnabled(enabled);
			lineIntegralRadius.setEnabled(enabled);
			lineIntegralStep.setEnabled(enabled);
			lineIntegralStartingPointLabel.setEnabled(enabled);
			lineIntegralSetStartingPoint.setEnabled(enabled);
			lineIntegralAnalytic.setEnabled(enabled);
			lineIntegralNumeric.setEnabled(enabled);
			lineIntegralAbsoluteError.setEnabled(enabled);
			lineIntegralRelativeError.setEnabled(enabled);
			
			fieldViewPanel.setLineIntegralVisibility(enabled);
			updateLineIntegral();
		} else if (event.getSource() == lineIntegralType) {
			fieldViewPanel.setLineIntegralCircle(lineIntegralType.getSelectedIndex() == 0);
			updateLineIntegral();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (addPoint.isSelected()) {
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			list.addElement(fieldViewPanel.getLastMousePositionOnGraph());
			fieldViewPanel.update();
		}
		
		if (lineIntegralSetStartingPoint.isSelected()) {
			lineIntegralStartingPoint = fieldViewPanel.getLastMousePositionOnGraph();
			lineIntegralStartingPointLabel.setText(lineIntegralStartingPoint.toStringNdecimals(2));
			fieldViewPanel.setLineIntegralStartingPoint(lineIntegralStartingPoint);
			updateLineIntegral();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == reset)
			fieldViewPanel.reset();
		else if (event.getSource() == removePoint) {
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			int[] selected = startingPointList.getSelectedIndices();
			for (int i = selected.length-1; i >= 0; i--) {
				list.remove(selected[i]);
			}
			fieldViewPanel.update();
		}
		else if (event.getSource() == analyticColor)
		{
			Color color = JColorChooser.showDialog(window, "Choisissez une couleur", fieldViewPanel.yColor());
			if (color != null)
				fieldViewPanel.setYColor(color);
		}
		else if (event.getSource() == numericColor)
		{
			Color color = JColorChooser.showDialog(window, "Choisissez une couleur", fieldViewPanel.forceColor());
			if (color != null)
				fieldViewPanel.setForceColor(color);
		}
		else if (event.getSource() == lineIntegralColor)
		{
			Color color = JColorChooser.showDialog(window, "Choisissez une couleur", fieldViewPanel.lineIntegralColor());
			if (color != null)
				fieldViewPanel.setLineIntegralColor(color);
		}
	}
	
	private void updateLineIntegral() {
		if (!lineIntegral.isSelected()) {
			lineIntegralAnalytic.setText(String.valueOf(0.0));
			lineIntegralNumeric.setText(String.valueOf(0.0));
			lineIntegralAbsoluteError.setText(String.valueOf(0.0));
			lineIntegralRelativeError.setText(String.valueOf(0.0));
		} else {
			boolean circle = lineIntegralType.getSelectedIndex() == 0;
			double analytic = yField.computeLineIntegral(lineIntegralStartingPoint, (double)lineIntegralRadius.getValue(), circle);
			double numeric = forceField.computeLineIntegral(lineIntegralStartingPoint, (double)lineIntegralRadius.getValue(), (double)lineIntegralStep.getValue(), circle);
			lineIntegralAnalytic.setText(String.valueOf(String.format("%.10f", analytic))); // Affichage de 10 décimales
			lineIntegralNumeric.setText(String.valueOf(String.format("%.10f", numeric)));
			lineIntegralAbsoluteError.setText(String.valueOf(String.format("%.10f", numeric-analytic)));
			lineIntegralRelativeError.setText(String.valueOf(String.format("%.10f", (numeric-analytic)*100./numeric) + " %"));
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
