import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Classe gérant l'affichage de la fenêtre et le fonctionnement du panneau de contrôle.
 * @author Paul Du
 */
public class MainWindow implements ChangeListener, MouseListener, ItemListener, ActionListener, DocumentListener {
	private JFrame window;
	private JSplitPane splitPane;
	private JScrollPane sideScrollPane;
	private JPanel sidePanel;
	private FieldViewPanel fieldViewPanel;

	private JComboBox<String> renderType;
	private JSpinner strokeSize;
	private JSpinner parameter;
	private JButton reset;

	private JCheckBox analytic;
	private JComboBox<String> analyticExpressionType;
	private JTextField analyticExpression;
	private JSpinner analyticStep;
	private JSpinner constantStart;
	private JSpinner constantEnd;
	private JSpinner constantStep;
	private JButton analyticColor;

	private JCheckBox numeric;
	private JTextField numericXExpression;
	private JTextField numericYExpression;
	private JSpinner numericStep;
	private JSpinner numberOfPoints;
	private JList<String> startingPointList;
	private JToggleButton addPoint;
	private JButton removePoint;
	private JButton autoAddPoints;
	private JSpinner autoPointsCount;
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

	private EvaluatedXYFuncField analyticField;
	private EvaluatedForceField numericField;

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

	MainWindow(EvaluatedXYFuncField analyticField, EvaluatedForceField numericField) {
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

		parameter = new JSpinner(new SpinnerNumberModel(analyticField.parameter(), -5, 5., 0.1));
		addLabelComponentToSidePanel("Paramètre c :", parameter);

		reset = new JButton("Réinitialiser le graphe");
		addOneComponentToSidePanel(reset);

		addSpaceToSidePanel();

		analytic = new JCheckBox("Modélisation analytique : ", false);
		analytic.setHorizontalTextPosition(JCheckBox.LEFT);
		addOneComponentToSidePanel(analytic);

		analyticExpressionType = new JComboBox<>(new String[] {"y = ", "x = "});
		if (analyticField.type() == XYFuncField.Type.X)
			analyticExpressionType.setSelectedIndex(0);
		else
			analyticExpressionType.setSelectedIndex(1);

		analyticExpression = new JTextField(analyticField.expression());
		addComponentsToSidePanel(analyticExpressionType, analyticExpression);

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

		numericXExpression = new JTextField(numericField.xExpression());
		addLabelComponentToSidePanel("Fx = ", numericXExpression);

		numericYExpression = new JTextField(numericField.yExpression());
		addLabelComponentToSidePanel("Fy = ", numericYExpression);

		numericStep = new JSpinner(new SpinnerNumberModel(numericField.step(), 0.001, 10, 0.001));
		addLabelComponentToSidePanel("Pas numérique :", numericStep);

		numberOfPoints = new JSpinner(new SpinnerNumberModel(fieldViewPanel.numberOfPoints(), 0, 10000, 100));
		addLabelComponentToSidePanel("Nombre de points :", numberOfPoints);

		startingPointList = new JList<String>(new Vector2DListModel());
		addLabelComponentToSidePanel("Points de départ :", new JScrollPane(startingPointList));

		addPoint = new JToggleButton("+");
		removePoint = new JButton("-");
		addTwoComponentsToSidePanel(addPoint, removePoint);

		autoAddPoints = new JButton("Ajout automatique : ");
		autoPointsCount = new JSpinner(new SpinnerNumberModel(1000, 0, 10000, 100));
		addComponentsToSidePanel(autoAddPoints, autoPointsCount);

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

		sideScrollPane = new JScrollPane(sidePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideScrollPane, fieldViewPanel);
		window.getContentPane().add(splitPane);

		renderType.addItemListener(this);
		strokeSize.addChangeListener(this);
		parameter.addChangeListener(this);
		reset.addActionListener(this);

		analytic.addItemListener(this);
		analyticExpressionType.addActionListener(this);
		analyticExpression.getDocument().addDocumentListener(this);
		analyticStep.addChangeListener(this);
		constantStart.addChangeListener(this);
		constantEnd.addChangeListener(this);
		constantStep.addChangeListener(this);
		analyticColor.addActionListener(this);

		numeric.addItemListener(this);
		numericXExpression.getDocument().addDocumentListener(this);
		numericYExpression.getDocument().addDocumentListener(this);
		numericStep.addChangeListener(this);
		numberOfPoints.addChangeListener(this);
		addPoint.addActionListener(this);
		removePoint.addActionListener(this);
		autoAddPoints.addActionListener(this);
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

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Menu");
		JMenuItem quit = new JMenuItem("Quitter");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.dispose();
			}
		});
		menu.add(quit);

		JMenu question = new JMenu("?");
		JMenuItem about = new JMenuItem("À propos...");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(window, "Force Field\n\n"+
													  "Logiciel de modélisation de lignes de champ à partir d'une équation\n"+
													  "de lignes de champ ou d'une expression d'un champ (de force).\n\n"+
													  "Copyright © Paul DU 2016-2017", "À propos de Force Field", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		question.add(about);

		menuBar.add(menu);
		menuBar.add(question);
		window.setJMenuBar(menuBar);

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
			analyticExpressionType.setEnabled(enabled);
			analyticExpression.setEnabled(enabled);
			analyticStep.setEnabled(enabled);
			constantStart.setEnabled(enabled);
			constantEnd.setEnabled(enabled);
			constantStep.setEnabled(enabled);
			analyticColor.setEnabled(enabled);

			if (enabled)
				fieldViewPanel.addField(analyticField);
			else
				fieldViewPanel.clearXYFuncFields();
		} else if (event.getSource() == numeric) {
			boolean enabled = numeric.isSelected();
			numericXExpression.setEnabled(enabled);
			numericYExpression.setEnabled(enabled);
			numericStep.setEnabled(enabled);
			numberOfPoints.setEnabled(enabled);
			addPoint.setEnabled(enabled);
			removePoint.setEnabled(enabled);
			startingPointList.setEnabled(enabled);
			autoAddPoints.setEnabled(enabled);
			autoPointsCount.setEnabled(enabled);
			numericColor.setEnabled(enabled);

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
		else if (event.getSource() == analyticExpressionType)
			updateAnalyticExpression();
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
		else if (event.getSource() == autoAddPoints)
		{
			Vector2DListModel list = (Vector2DListModel) startingPointList.getModel();
			while (list.getSize() > 0)
				list.remove(0);

			Rectangle2D viewBounds = fieldViewPanel.viewBounds();
			for (double x = viewBounds.getX(); x < viewBounds.getX()+viewBounds.getWidth(); x += viewBounds.getWidth()/Math.sqrt((int) autoPointsCount.getValue())) {
				for (double y = viewBounds.getY(); y < viewBounds.getY()+viewBounds.getHeight(); y += viewBounds.getHeight()/Math.sqrt((int) autoPointsCount.getValue())) {
					list.addElement(new Vector2D(x, y));
				}
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

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
	}

	private void updateAnalyticExpression() {
		try {
			if (analyticExpressionType.getSelectedItem().equals("y = "))
				analyticField.setExpression(XYFuncField.Type.X, analyticExpression.getText());
			else if  (analyticExpressionType.getSelectedItem().equals("x = "))
				analyticField.setExpression(XYFuncField.Type.Y, analyticExpression.getText());

			analyticExpression.setForeground(Color.black);
			fieldViewPanel.update();
			updateLineIntegral();
		} catch (ParseException e) {
			analyticExpression.setForeground(Color.red);
		}
	}

	private void updateNumericXExpression() {
		try {
			numericField.setXExpression(numericXExpression.getText());
			numericXExpression.setForeground(Color.black);
			fieldViewPanel.update();
			updateLineIntegral();
		} catch (ParseException e) {
			numericXExpression.setForeground(Color.red);
		}
	}

	private void updateNumericYExpression() {
		try {
			numericField.setYExpression(numericYExpression.getText());
			numericYExpression.setForeground(Color.black);
			fieldViewPanel.update();
			updateLineIntegral();
		} catch (ParseException e) {
			numericYExpression.setForeground(Color.red);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		if (event.getDocument() == analyticExpression.getDocument()) {
			updateAnalyticExpression();
		} else if (event.getDocument() == numericXExpression.getDocument()) {
			updateNumericXExpression();
		} else if (event.getDocument() == numericYExpression.getDocument()) {
			updateNumericYExpression();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		if (event.getDocument() == analyticExpression.getDocument()) {
			updateAnalyticExpression();
		} else if (event.getDocument() == numericXExpression.getDocument()) {
			updateNumericXExpression();
		} else if (event.getDocument() == numericYExpression.getDocument()) {
			updateNumericYExpression();
		}
	}
}
