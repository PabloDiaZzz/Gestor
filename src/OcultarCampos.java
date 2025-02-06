import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.*;

public class OcultarCampos extends JDialog {

	protected static final Set<TableColumn> columnasOcultas = new HashSet<>();
	protected static final Set<TableColumn> columnasVisibles = new HashSet<>();
	protected static Map<JCheckBox, TableColumn> checkBoxColumnMap = new LinkedHashMap<>();
	protected static List<TableColumn> columns = new ArrayList<>();
	protected static JPanel listaCheckBoxPanel = new JPanel();
	private final JTable tabla;
	private JCheckBox checkBox_1;

	public OcultarCampos(JTable t, HashMap<TableColumn, Integer> columnWidthsMap, JFrame parent) {
		super(parent, "Ocultar Campos - " + Gestor2GUI.panel2.getTitleAt(Gestor2GUI.panel2.getSelectedIndex()), false);
		columns.clear();
		this.tabla = t;
		Gestor2GUI.setColumnWidths(tabla, (int[]) Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex()).get(1));

		TableColumnModel columnModel = tabla.getColumnModel();
		ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex()).getFirst();
		columns.addAll(Collections.list(columnModel.getColumns()));
		JPanel principal = new JPanel(new BorderLayout());
		setModal(true);
		setContentPane(principal);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(new Dimension(300, 400));
		setMinimumSize(new Dimension(300, 200));
		setLocationRelativeTo(parent);


		listaCheckBoxPanel = new JPanel();
		listaCheckBoxPanel.setLayout(new BoxLayout(listaCheckBoxPanel, BoxLayout.Y_AXIS));
		listaCheckBoxPanel.setMaximumSize(new Dimension(300, Short.MAX_VALUE));

		checkBoxColumnMap = new LinkedHashMap<>();
		columns.stream().skip(1).forEach(column -> {
			checkBox_1 = new JCheckBox(column.getHeaderValue().toString());
			checkBox_1.setSelected(column.getWidth() > 0);
			checkBoxColumnMap.put(checkBox_1, column);
			listaCheckBoxPanel.add(checkBox_1);
			if (checkBox_1.isSelected()) {
				columnasVisibles.add(column);
				columnasOcultas.remove(column);
			} else {
				columnasOcultas.add(column);
				columnasVisibles.remove(column);
			}
		});

		JScrollPane scrollLista = new JScrollPane(listaCheckBoxPanel);
		scrollLista.setPreferredSize(new Dimension(120, 27));
		scrollLista.setMaximumSize(new Dimension(300, Short.MAX_VALUE)); // Ajuste para que ocupe el espacio
		scrollLista.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollLista.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel botonesSeleccion = new JPanel();
		botonesSeleccion.setLayout(new GridBagLayout());

		JButton sTodo = new JButton("Seleccionar Todo");
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.fill = GridBagConstraints.HORIZONTAL;
		gbc1.insets = new Insets(5, 5, 5, 5);
		gbc1.weightx = 1.0;
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		botonesSeleccion.add(sTodo, gbc1);
		sTodo.setMaximumSize(new Dimension(150, 23));
		sTodo.setPreferredSize(new Dimension(150, 23));
		sTodo.addActionListener(e -> {
			Arrays.stream(listaCheckBoxPanel.getComponents()).forEach(component -> {
				((JCheckBox) component).setSelected(true);
				columnasOcultas.remove(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
				columnasVisibles.add(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
			});
			listaCheckBoxPanel.repaint();
			listaCheckBoxPanel.revalidate();
		});

		JButton dTodo = new JButton("Deseleccionar Todo");
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.fill = GridBagConstraints.HORIZONTAL;
		gbc2.insets = new Insets(5, 5, 5, 5);
		gbc2.weightx = 1.0;
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		botonesSeleccion.add(dTodo, gbc2);
		dTodo.setMaximumSize(new Dimension(150, 23));
		dTodo.setPreferredSize(new Dimension(150, 23));
		dTodo.addActionListener(e -> {
			Arrays.stream(listaCheckBoxPanel.getComponents()).forEach(component -> {
				((JCheckBox) component).setSelected(false);
				columnasOcultas.add(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
				columnasVisibles.remove(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
			});
			listaCheckBoxPanel.repaint();
			listaCheckBoxPanel.revalidate();
		});

		JButton invS = new JButton("Invertir Selección");
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.fill = GridBagConstraints.HORIZONTAL;
		gbc3.insets = new Insets(5, 5, 5, 5);
		gbc3.weightx = 1.0;
		gbc3.gridx = 0;
		gbc3.gridy = 2;
		botonesSeleccion.add(invS, gbc3);
		invS.setMaximumSize(new Dimension(150, 23));
		invS.setPreferredSize(new Dimension(150, 23));
		invS.addActionListener(e -> {
			Arrays.stream(listaCheckBoxPanel.getComponents()).forEach(component -> {
				((JCheckBox) component).setSelected(! ((JCheckBox) component).isSelected());
				if (((JCheckBox) component).isSelected()) {
					columnasVisibles.add(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
					columnasOcultas.remove(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
				} else {
					columnasOcultas.add(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
					columnasVisibles.remove(columns.get(Arrays.asList(listaCheckBoxPanel.getComponents()).indexOf(component)));
				}
			});
			listaCheckBoxPanel.repaint();
			listaCheckBoxPanel.revalidate();
		});

		// Dividimos el espacio entre la lista y los botones usando BorderLayout
		JPanel panelCampos = new JPanel(new BorderLayout());
		panelCampos.add(scrollLista, BorderLayout.CENTER);
		panelCampos.add(botonesSeleccion, BorderLayout.EAST);

		principal.add(panelCampos, BorderLayout.CENTER);

		// Panel de botones inferiores
		JButton aplicarButton = new JButton("Aplicar");
		JButton aceptarButton = new JButton("Aceptar");
		JButton cancelarButton = new JButton("Cancelar");

		aplicarButton.addActionListener(e -> aplicarCambios());
		aceptarButton.addActionListener(e -> {
			aplicarCambios();
			dispose();
		});
		cancelarButton.addActionListener(e -> dispose());

		JPanel botones = new JPanel();
		botones.add(aplicarButton);
		botones.add(aceptarButton);
		botones.add(cancelarButton);
		principal.add(botones, BorderLayout.SOUTH);

		setVisible(true);
	}

	private void aplicarCambios() {

		Gestor2GUI.value = Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex());
		int[] widths = (int[]) Gestor2GUI.value.get(2);
		TableColumnModel columnModel = tabla.getColumnModel();
		int index = 0;

		for (Map.Entry<JCheckBox, TableColumn> entry : checkBoxColumnMap.entrySet()) {
			TableColumn column = entry.getValue();
			JCheckBox checkBox = entry.getKey();

			if (! checkBox.isSelected()) {
				columnasVisibles.remove(column);
				columnasOcultas.add(column);

				column.setMinWidth(0);
				column.setPreferredWidth(0);
				column.setMaxWidth(0);
				index++;
			} else {
				columnasOcultas.remove(column);
				columnasVisibles.add(column);
				column.setMaxWidth(1000);
				column.setMinWidth(10);
				column.setPreferredWidth(widths[index]);
				column.setWidth(widths[index++]);
			}
		}
		Gestor2GUI.value.set(1, Gestor2GUI.getColumnWidths(tabla));
		Gestor2GUI.listas.put(Gestor2GUI.panel2.getSelectedIndex(), Gestor2GUI.value);
		Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex(), true, (int[]) Gestor2GUI.value.get(1));
		Gestor2GUI.panel2.setComponentAt(Gestor2GUI.panel2.getSelectedIndex(), new JScrollPane(tabla));
	}
}

