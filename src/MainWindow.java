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

/**
 * Classe gérant l'affichage de la fenêtre et le fonctionnement du panneau de contrôle.
 * @author Paul Du
 */
public class MainWindow implements ChangeListener, MouseListener, ItemListener, ActionListener {
	private JFrame window;
	private JSplitPane splitPane;
	private JPanel sidePanel;
	private FieldViewPanel fieldViewPanel;
	
	private JComboBox<String> renderType;
	private JSpinner strokeSize;
	private JSpinner parameter;
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
	
	private XYFuncField analyticField;
	private ForceField numericField;
	
	/**
	 * Cette classe stocke la liste des points de départ pour la modélisation numérique.
	 */
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
		
		/**
		 * Ajoute un point à la liste.
		 * @param element Point à ajouter
		 */
		public void addElement(Vector2D element) {
			vectors.add(element);
			fireIntervalAdded(this, vectors.size()-1, vectors.size()-1);
		}
		
		/**
		 * Enlève un point à la liste identifié par son index
		 * @param index Index du point
		 */
		public void remove(int index) {
			vectors.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		
		public List<Vector2D> getList() {
			return vectors;
		}
	}
	
	MainWindow(XYFuncField analyticField, ForceField numericField) {
		window = new JFrame();
		window.setTitle("Force Field Vizualizer");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.analyticField = analyticField;
		this.numericField = numericField;
		
		fieldViewPanel = new FieldViewPanel();
		
		sidePanel = new JPanel(new GridBagLayout());
		
		renderType = new JComboBox<String>(new String[] {"Croix", "Rectangle", "Ovale", "Ligne", "Rectangle & ligne"});
		addLabelComponentToSidePanel("Type de Rendu", renderType);
		
		strokeSize = new JSpinner(new SpinnerNumberModel(fieldViewPanel.strokeSize(), 1., 25., 1.));
		addLabelComponentToSidePanel("Épaisseur du trait :", strokeSize);
			
		parameter = new JSpinner(new SpinnerNumberModel(analyticField.parameter(), .01, 5., 0.01));
		addLabelComponentToSidePanel("Paramètre :", parameter);
		
		reset = new JButton("Réinitialiser le graphe");
		addOneComponentToSidePanel(reset);
		
		addSpaceToSidePanel();
		
		analytic = new JCheckBox("Modélisation analytique : ", false);
		analytic.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(analytic);
		
		analyticStep = new JSpinner(new SpinnerNumberModel(analyticField.step(), 0.001, 10, 0.001));
		addLabelComponentToSidePanel("Pas analytique :", analyticStep);
		
		constantStart = new JSpinner(new SpinnerNumberModel(analyticField.constantStart(), -100., 100., 1.));
		addLabelComponentToSidePanel("Constante début :", constantStart);
		
		constantEnd = new JSpinner(new SpinnerNumberModel(analyticField.constantEnd(), -100., 100., 1.));
		addLabelComponentToSidePanel("Constante fin :", constantEnd);
		
		constantStep = new JSpinner(new SpinnerNumberModel(analyticField.constantStep(), 0.01, 10, 0.01));
		addLabelComponentToSidePanel("Pas de constante :", constantStep);
		
		analyticColor = new JButton("Changer...");
		addLabelComponentToSidePanel("Couleur de la courbe :", analyticColor);

		addSpaceToSidePanel();
		
		numeric = new JCheckBox("Modélisation numérique : ", true);
		numeric.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(numeric);
		
		numericStep = new JSpinner(new SpinnerNumberModel(numericField.step(), 0.001, 10, 0.001));
		addLabelComponentToSidePanel("Pas numérique :", numericStep);
		
		numberOfPoints = new JSpinner(new SpinnerNumberModel(fieldViewPanel.numberOfPoints(), 0, 10000, 100));
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
		
		lineIntegralRadius = new JSpinner(new SpinnerNumberModel(fieldViewPanel.lineIntegralRadius(), 0., 100., 1.));
		addLabelComponentToSidePanel("Rayon :", lineIntegralRadius);
		
		lineIntegralStep = new JSpinner(new SpinnerNumberModel(0.001, 0.000001, 1, 0.000001));
		//Modifie la précision de la valeur stockée
		lineIntegralStep.setEditor(new JSpinner.NumberEditor(lineIntegralStep, "0.000000"));
		addLabelComponentToSidePanel("Pas :", lineIntegralStep);
		
		lineIntegralStartingPoint = fieldViewPanel.lineIntegralStartingPoint();
		lineIntegralStartingPointLabel = new JLabel(lineIntegralStartingPoint.toString());
		addLabelComponentToSidePanel("Point de départ :", lineIntegralStartingPointLabel);
		
		lineIntegralSetStartingPoint = new JToggleButton("Définir le point de départ");
		addOneComponentToSidePanel(lineIntegralSetStartingPoint);
		
		lineIntegralColor = new JButton("Changer...");
		addLabelComponentToSidePanel("Couleur de la courbe :", lineIntegralColor);
		
		lineIntegralAnalytic = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Circulation analytique :", lineIntegralAnalytic);
		
		lineIntegralNumeric = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Circulation numérique :", lineIntegralNumeric);
		
		lineIntegralAbsoluteError = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Erreur absolue :", lineIntegralAbsoluteError);
		
		lineIntegralRelativeError = new JLabel(String.valueOf(0.0));
		addLabelComponentToSidePanel("Erreur relative :", lineIntegralRelativeError);
		
		{ //Pousse le contenu vers le haut
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.weighty = 1;
			sidePanel.add(Box.createGlue(), constraints);
		}
		
		((Vector2DListModel) startingPointList.getModel()).addElement(new Vector2D(0.5, 0));
		// /!\ Le tableau est partagé startingPointList et fielViewPanel
		fieldViewPanel.setStartingPoints(((Vector2DListModel) startingPointList.getModel()).getList()); 
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidePanel, fieldViewPanel);
		window.getContentPane().add(splitPane);
		
		renderType.addItemListener(this);
		strokeSize.addChangeListener(this);
		parameter.addChangeListener(this);
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
		addPoint.addActionListener(this);
		removePoint.addActionListener(this);
		numericColor.addActionListener(this);

		lineIntegral.addItemListener(this);
		lineIntegralType.addItemListener(this);
		lineIntegralRadius.addChangeListener(this);
		lineIntegralStep.addChangeListener(this);
		lineIntegralSetStartingPoint.addActionListener(this);
		lineIntegralColor.addActionListener(this);
		
		fieldViewPanel.addMouseListener(this);
		
		/*Comme les cases à cocher ont été initialisées avec des valeurs opposées,
		les intructions qui suivent vont déclencher les listeners pour finir d'ajuster l'interface :
		activation/désactivation des champs correspondants, etc.*/
		analytic.setSelected(true);
		numeric.setSelected(false);
		lineIntegral.setSelected(false);
		
		window.pack();
		window.setVisible(true);
	}
	
	/**
	 * Ajoute deux composants sur une ligne du panneau latéral. Le second composant s'étendra au maximum.
	 * @param component1 Premier composant à ajouter
	 * @param component2 Second composant à ajouter
	 */
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
	
	/**
	 * Ajoute un composant précédé d'un titre sur une ligne du panneau latéral.
	 * @param label Titre du composant
	 * @param component Composant à ajouter
	 */
	private void addLabelComponentToSidePanel(String label, Component component) {
		addComponentsToSidePanel(new JLabel(label), component);
	}
	
	/**
	 * Ajoute un composant occupant tout une ligne du panneau latéral.
	 * @param component Composant à ajouter
	 */
	private void addOneComponentToSidePanel(Component component) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		sidePanel.add(component, constraints);
	}
	
	/**
	 * Ajoute deux composants sur une ligne du panneau latéral ayant une même largeur.
	 * @param component1 Premier composant à ajouter
	 * @param component2 Seconde composant à ajouter
	 */
	private void addTwoComponentsToSidePanel(Component component1, Component component2) {
		//Fait en sorte que les deux composants aient la même largeur
		JPanel wrapComponents = new JPanel(new GridLayout(1, 2));
		wrapComponents.add(component1);
		wrapComponents.add(component2);
		addOneComponentToSidePanel(wrapComponents);
	}
	
	/**
	 * Ajoute un petit espace vertical sur toute une ligne du panneau latéral.
	 */
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
		if (event.getSource() == strokeSize)
			fieldViewPanel.setStrokeSize((double) strokeSize.getValue());
		else if (event.getSource() == parameter) {
			analyticField.setParameter((double) parameter.getValue());
			numericField.setParameter((double) parameter.getValue());
			updateLineIntegral();
			fieldViewPanel.update();
		} else if (event.getSource() == analyticStep)
			analyticField.setStep((double) analyticStep.getValue());
		else if (event.getSource() == constantStart) {
			analyticField.setConstantStart((double) constantStart.getValue());
			if ((double)constantStart.getValue() > (double)constantEnd.getValue())
				constantEnd.setValue(constantStart.getValue());
		}		
		else if (event.getSource() == constantEnd) {
			analyticField.setConstantEnd((double) constantEnd.getValue());
			if ((double)constantEnd.getValue() < (double)constantStart.getValue())
				constantStart.setValue(constantEnd.getValue());
		}
		else if (event.getSource() == constantStep)
			analyticField.setConstantStep((double) constantStep.getValue());
		else if (event.getSource() == numericStep)
			numericField.setStep((double) numericStep.getValue());
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
				fieldViewPanel.addField(analyticField);
			else
				fieldViewPanel.clearXYFuncFields();
		} else if (event.getSource() == numeric) {
			boolean enabled = numeric.isSelected();
			numericStep.setEnabled(enabled);
			numberOfPoints.setEnabled(enabled);
			addPoint.setEnabled(enabled);
			removePoint.setEnabled(enabled);
			startingPointList.setEnabled(enabled);
			
			if (enabled)
				fieldViewPanel.addField(numericField);
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
		else if (event.getSource() == addPoint) {
			if (addPoint.isSelected())
				fieldViewPanel.setSettingPointMode(true);
			else if (!addPoint.isSelected() && !lineIntegralSetStartingPoint.isSelected())
				fieldViewPanel.setSettingPointMode(false);
		}
		else if (event.getSource() == removePoint) {
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			int[] selected = startingPointList.getSelectedIndices();
			for (int i = selected.length-1; i >= 0; i--) {
				list.remove(selected[i]);
			}
			fieldViewPanel.update();
		}
		else if (event.getSource() == analyticColor || event.getSource() == numericColor)
		{
			Field field;
			if (event.getSource() == analyticColor)
				field = analyticField;
			else
				field = numericField;
			
			Color color = JColorChooser.showDialog(window, "Choisissez une couleur", field.color());
			if (color != null) {
				field.setColor(color);
				fieldViewPanel.update();
			}
		}
		else if (event.getSource() == lineIntegralColor)
		{
			Color color = JColorChooser.showDialog(window, "Choisissez une couleur", fieldViewPanel.lineIntegralColor());
			if (color != null)
				fieldViewPanel.setLineIntegralColor(color);
		}
		else if (event.getSource() == lineIntegralSetStartingPoint) {
			if (lineIntegralSetStartingPoint.isSelected())
				fieldViewPanel.setSettingPointMode(true);
			else if (!addPoint.isSelected() && !lineIntegralSetStartingPoint.isSelected())
				fieldViewPanel.setSettingPointMode(false);
		}
	}
	
	/**
	 * Met à jour l'affichage des résultats des calculs de circulation.
	 */
	private void updateLineIntegral() {
		if (!lineIntegral.isSelected()) {
			lineIntegralAnalytic.setText(String.valueOf(0.0));
			lineIntegralNumeric.setText(String.valueOf(0.0));
			lineIntegralAbsoluteError.setText(String.valueOf(0.0));
			lineIntegralRelativeError.setText(String.valueOf(0.0));
		} else {
			double analytic = analyticField.computeLineIntegral(lineIntegralStartingPoint, (double)lineIntegralRadius.getValue(), lineIntegralType.getSelectedIndex());
			double numeric = numericField.computeLineIntegral(lineIntegralStartingPoint, (double)lineIntegralRadius.getValue(), lineIntegralType.getSelectedIndex(), (double)lineIntegralStep.getValue());
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
