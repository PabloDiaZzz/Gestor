import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Gestor2GUI {
	protected static JTabbedPane panel2;
	protected static JTable tabla;
	protected static HashMap<Integer, ArrayList<Object>> listas = new HashMap<>();
	static TabComponent pestaña;
	static ArrayList<Object> value = new ArrayList<>();
	private static JFrame frame;
	private static ArrayList<ArrayList<Dato>> list = new ArrayList<>();
	private JPanel mainPanel;
	private JMenuBar menuBar;
	private JMenu menuTabla;
	private JMenu menu;
	private JFileChooser chooser;
	private JMenuItem ordenItem;
	private JMenuItem ocultarItem;
	private JPanel botones = new JPanel();

	public Gestor2GUI() {
		createGUI();
	}

	private static JTable createTable(int Index) {
		list = (ArrayList<ArrayList<Dato>>) listas.get(Index).getFirst();
		return base.tabla(frame, base.order(list));
	}

	public static void refreshTable(int Index) {
		refreshTable(Index, false, null, true);
	}

	public static void refreshTable(int Index, int[] a) {
		refreshTable(Index, false, a, true);
	}

	public static void refreshTable(int Index, boolean mantainWidth, int[] a) {
		refreshTable(Index, mantainWidth, a, true);
	}

	public static void refreshTable(int Index, boolean mantainWidth, int[] a, boolean checkList) {
		value = listas.get(Index);
		if (! isSameList(Index) && checkList) {
			return;
		}
		int[] anchos;
		if (a != null) {
			anchos = a;
		} else {
			anchos = getColumnWidths(tabla);
		}
		value.set(1, anchos);
		int[] updateArray = (int[]) value.get(2);
		if (anchos.length > updateArray.length) {
			updateArray = Arrays.copyOf(updateArray, anchos.length);
		}
		for (int x = 0; x < anchos.length; x++) {
			if (anchos[x] != 0) {
				updateArray[x] = anchos[x];
			}
		}
		value.set(2, updateArray);
		tabla = createTable(Index);
		panel2.setComponentAt(Index, new JScrollPane(tabla));
		if (mantainWidth) {
			setColumnWidths(tabla, anchos);
		}
		listas.put(Index, value);
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		panel2.getComponentAt(Index).revalidate();
		panel2.getComponentAt(Index).repaint();
		panel2.revalidate();
		panel2.repaint();
	}

	protected static void setColumnWidths(JTable table, int[] widths) {
		if (widths != null && table.getColumnCount() >= widths.length) {
			for (int i = 0; i < widths.length; i++) {
				if (widths[i] != 0) {
					table.getColumnModel().getColumn(i).setMinWidth(10);
					table.getColumnModel().getColumn(i).setMaxWidth(1000);
					table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
				} else {
					table.getColumnModel().getColumn(i).setMinWidth(0);
					table.getColumnModel().getColumn(i).setMaxWidth(0);
					table.getColumnModel().getColumn(i).setPreferredWidth(0);
				}
			}
		}
	}

	protected static int[] getColumnWidths(JTable table) {
		int columnCount = table.getColumnCount();
		int[] widths = new int[columnCount];
		for (int i = 0; i < columnCount; i++) {
			widths[i] = table.getColumnModel().getColumn(i).getWidth();
		}
		return widths;
	}

	private static HashMap<TableColumn, Integer> getAnchos(JTable tabla) {
		HashMap<TableColumn, Integer> anchos = new HashMap<>();
		for (int i = 0; i < tabla.getColumnCount(); i++) {
			anchos.put(tabla.getColumnModel().getColumn(i), tabla.getColumnModel().getColumn(i).getWidth());
		}
		return anchos;
	}

	public static boolean isSameList(int Index) {
		ArrayList<ArrayList<String>> currentList = tablaToArrayList(tabla);
		ArrayList<ArrayList<Dato>> list = (ArrayList<ArrayList<Dato>>) listas.get(Index).getFirst();
		if (list.size() != currentList.size() || list.get(0).size() != currentList.get(0).size()) {
			return false;
		}
		ArrayList<ArrayList<String>> listComp = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			ArrayList<String> fila = new ArrayList<>();
			for (int j = 0; j < list.get(i).size(); j++) {
				fila.add(list.get(i).get(j).getValor());
			}
			listComp.add(fila);
		}
		return currentList.equals(listComp);
	}

	private static ArrayList<ArrayList<String>> tablaToArrayList(JTable tabla) {
		ArrayList<ArrayList<String>> list = new ArrayList<>();
		ArrayList<String> encabezados = new ArrayList<>();
		for (int j = 0; j < tabla.getColumnCount(); j++) {
			encabezados.add(tabla.getColumnName(j));
		}
		list.add(encabezados);
		for (int i = 0; i < tabla.getRowCount(); i++) {
			ArrayList<String> fila = new ArrayList<>();
			for (int j = 0; j < tabla.getColumnCount(); j++) {
				fila.add(String.valueOf(tabla.getValueAt(i, j)));
			}
			list.add(fila);
		}
		return list;
	}

	public static void distribuirAncho(int Index) {
		value = listas.get(Index);
		if (! isSameList(Index)) {
			return;
		}
		int[] anchos = getColumnWidths(tabla);
		value.set(1, anchos);
		int[] updateArray = (int[]) value.get(2);
		if (anchos.length > updateArray.length) {
			updateArray = Arrays.copyOf(updateArray, anchos.length);
		}
		for (int x = 0; x < anchos.length; x++) {
			if (anchos[x] != 0) {
				updateArray[x] = anchos[x];
			}
		}
		tabla = createTable(Index);
		int[] anchosSinCeros = getColumnWidths(tabla);
		for (int x = 0; x < anchos.length; x++) {
			if (anchos[x] == 0) {
				anchosSinCeros[x] = anchos[x];
			}
		}
		value.set(2, updateArray);

		panel2.setComponentAt(Index, new JScrollPane(tabla));
		setColumnWidths(tabla, anchosSinCeros);
		listas.put(Index, value);
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		panel2.getComponentAt(Index).revalidate();
		panel2.getComponentAt(Index).repaint();
		panel2.revalidate();
		panel2.repaint();
	}

	public static void main(String[] args) {
		new Gestor2GUI();
	}

	private void createGUI() {
		AtomicReference<File> f = new AtomicReference<>();
		frame = new JFrame("Gestor2 GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setBackground(Color.WHITE);

		mainPanel = new JPanel(new BorderLayout());

		panel2 = new JTabbedPane();
		panel2.setBackground(Color.GREEN);
		panel2.setMinimumSize(new Dimension(500, 0));
		switchPanel(panel2);

		JButton distAncho = new JButton("<->");
		distAncho.setVisible(false);
		//		distAncho.setPreferredSize(new Dimension(25,25));
		distAncho.addActionListener(e -> {
			distribuirAncho(panel2.getSelectedIndex());
			refreshTable(panel2.getSelectedIndex(), true, getColumnWidths(tabla), false);
		});

		ImageIcon nEntIcono = new ImageIcon(getClass().getClassLoader().getResource("img/nEnt.png"));
		nEntIcono = new ImageIcon(nEntIcono.getImage().getScaledInstance(23, 23, Image.SCALE_DEFAULT));
		RoundImageButton nEnt = new RoundImageButton(nEntIcono, 10, 25);
		nEnt.setBackground(new Color(230, 230, 230));
		nEnt.setPreferredSize(new Dimension(25, 25));
		nEnt.setVisible(false);
		nEnt.addActionListener(e -> {
			ArrayList<Dato> newEntity = new ArrayList<>();
			value = listas.get(panel2.getSelectedIndex());
			ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) value.getFirst();
			newEntity.add(new Dato(String.valueOf(l.size()), - 1, "Clave"));
			for (int i = 1; i < l.get(0).size(); i++) {
				newEntity.add(new Dato(" ", - 1, l.get(0).get(i).getTipo()));
			}
			l.add(newEntity);
			value.set(0, l);
			listas.put(panel2.getSelectedIndex(), value);
			refreshTable(panel2.getSelectedIndex(), true, getColumnWidths(tabla), false);
		});

		ImageIcon nCampoIcono = new ImageIcon(getClass().getClassLoader().getResource("img/nCampo.png"));
		nCampoIcono = new ImageIcon(nCampoIcono.getImage().getScaledInstance(23, 23, Image.SCALE_DEFAULT));
		RoundImageButton nCampo = new RoundImageButton(nCampoIcono, 10, 25);
		nCampo.setBackground(new Color(230, 230, 230));
		nCampo.setPreferredSize(new Dimension(25, 25));
		nCampo.setVisible(false);
		nCampo.addActionListener(e -> {
			String text = JOptionPane.showInputDialog("Introduce el campo: ");
			value = listas.get(panel2.getSelectedIndex());
			ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) value.getFirst();
			if (l.getFirst().stream().anyMatch(dato -> dato.getValor().equalsIgnoreCase(text))) {
				System.out.println("El campo ya existe.");
			} else if (text == null) {
			} else if (text.isBlank()) {
				System.out.println("El campo no puede estar vacío.");
			} else {
				int tipo = JOptionPane.showOptionDialog(frame, "Seleccione el tipo de dato:", "Tipo", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, Dato.tipos, 0);
				l.getFirst().add(new Dato(text, 0, tipo));
				for (int i = 1; i < l.size(); i++) {
					l.get(i).add(new Dato(" ", i, tipo));
				}
				value.set(0, l);
				listas.put(panel2.getSelectedIndex(), value);
				refreshTable(panel2.getSelectedIndex(), true, getColumnWidths(tabla), false);
			}
		});

		menuBar = new JMenuBar();
		menu = new JMenu("Archivo");
		ocultarItem = new JMenuItem("Ocultar campos");
		ocultarItem.setEnabled(false);
		ocultarItem.addActionListener(e -> {
			refreshTable(panel2.getSelectedIndex(), true, getColumnWidths(tabla));
			OcultarCampos ejemplo1 = new OcultarCampos(createTable(panel2.getSelectedIndex()), getAnchos(tabla), frame);
		});

		ordenItem = new JMenuItem("Alternar orden");
		ordenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		ordenItem.addActionListener(e -> {
			value = listas.get(panel2.getSelectedIndex());
			ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) value.getFirst();
			int[] medidas = (int[]) value.get(1);
			base.altOrder(l);
			value.set(0, l);
			listas.put(panel2.getSelectedIndex(), value);
			refreshTable(panel2.getSelectedIndex(), true, medidas, false);
		});
		ordenItem.setEnabled(false);

		JMenuItem nuevo = new JMenuItem("Nuevo");
		nuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		nuevo.addActionListener(e -> {
			pestaña = new TabComponent(panel2, "Nueva lista");
			ArrayList<Object> value = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				value.add(new ArrayList<>());
			}
			ArrayList<ArrayList<Dato>> l = new ArrayList<>();
			l.add(new ArrayList<>());
			value.set(0, l);
			listas.put(panel2.getTabCount(), value);
			panel2.addTab("Nueva lista", new ScrollPane());
			panel2.setTabComponentAt(panel2.getTabCount() - 1, pestaña);
			panel2.setSelectedIndex(panel2.getTabCount() - 1);
			tabla = new JTable();
			String text = JOptionPane.showInputDialog("Introduce el campo: ");
			value = listas.get(panel2.getSelectedIndex());
			if (l.getFirst().stream().anyMatch(dato -> dato.getValor().equalsIgnoreCase(text))) {
				System.out.println("El campo ya existe.");
			} else if (text == null) {
				BotonRedondeado button = (BotonRedondeado) ((TabComponent) panel2.getTabComponentAt(panel2.getSelectedIndex())).getComponent(1);
				if (button != null) {
					MouseListener[] listeners = button.getMouseListeners();
					if (listeners.length > 0) {
						MouseListener listener = listeners[0];
						MouseEvent me = new MouseEvent(button, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false);
						listener.mouseClicked(me);
					}
				}
			} else if (text.isBlank()) {
				System.out.println("El campo no puede estar vacío.");
			} else {
				int tipo = JOptionPane.showOptionDialog(frame, "Seleccione el tipo de dato:", "Tipo", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, Dato.tipos, 0);
				l.getFirst().add(new Dato(text, 0, tipo));
				for (int i = 1; i < l.size(); i++) {
					l.get(i).add(new Dato(" ", i, tipo));
				}
				value.set(0, l);
				value.set(1, getColumnWidths(tabla));
				value.set(2, getColumnWidths(tabla));
				listas.put(panel2.getSelectedIndex(), value);
				refreshTable(panel2.getSelectedIndex(), true, getColumnWidths(tabla), false);
				distAncho.setVisible(true);
				nEnt.setVisible(true);
				nCampo.setVisible(true);
			}
		});

		JMenuItem abrirItem = new JMenuItem("Abrir");
		abrirItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		abrirItem.addActionListener(e -> {
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = chooser.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				f.set(chooser.getSelectedFile());
				pestaña = new TabComponent(panel2, f.get().getName());
				ArrayList<Object> value = new ArrayList<>();
				value.add(Gestor2.cargarLista(f.get().getAbsolutePath()));
				value.add(getColumnWidths(base.tabla(frame, (ArrayList<ArrayList<Dato>>) value.getFirst())));
				listas.put(panel2.getTabCount(), value);
				panel2.addTab(f.get().getName(), new ScrollPane());
				panel2.setTabComponentAt(panel2.getTabCount() - 1, pestaña);
				panel2.setSelectedIndex(panel2.getTabCount() - 1);
				tabla = createTable(panel2.getSelectedIndex());
				value.add(getColumnWidths(tabla));
				listas.put(panel2.getTabCount(), value);
				setColumnWidths(tabla, getColumnWidths(tabla));
				refreshTable(panel2.getSelectedIndex(), false, null);
				distAncho.setVisible(true);
				nEnt.setVisible(true);
				nCampo.setVisible(true);
				if (! listas.get(panel2.getSelectedIndex()).isEmpty()) {
					ocultarItem.setEnabled(true);
					ordenItem.setEnabled(true);
				}
				botones.setEnabled(true);
			}
		});

		JMenuItem guardarComoItem = new JMenuItem("Guardar como");
		guardarComoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
		guardarComoItem.addActionListener(e -> {
			chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			int result = chooser.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				f.set(chooser.getSelectedFile());
				Gestor2.guardarLista((ArrayList<ArrayList<Dato>>) listas.get(panel2.getSelectedIndex()).getFirst(), f.get().getAbsolutePath(), 0);
				pestaña = (TabComponent) panel2.getTabComponentAt(panel2.getSelectedIndex());
				pestaña.setName(f.get().getName());
				panel2.repaint();
				panel2.revalidate();
			}
		});

		JMenuItem guardarItem = new JMenuItem("Guardar");
		guardarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		guardarItem.addActionListener(e -> {
			try {
				Gestor2.guardarLista((ArrayList<ArrayList<Dato>>) listas.get(panel2.getSelectedIndex()).getFirst(), f.get().getAbsolutePath(), 0);
			} catch (NullPointerException ignored) {
				guardarComoItem.doClick();
			}
		});

		JMenuItem salir = new JMenuItem("Salir");
		salir.addActionListener(e -> frame.dispose());

		menuTabla = new JMenu("Tabla");
		botones.setLayout(new FlowLayout(FlowLayout.LEFT));
		botones.setEnabled(false);
		botones.revalidate();
		botones.repaint();
		menuTabla.add(ordenItem);
		menu.add(nuevo);
		menu.add(abrirItem);
		menu.add(guardarItem);
		menu.add(guardarComoItem);
		menu.add(salir);
		menuTabla.add(ocultarItem);
		menuBar.add(menu);
		menuBar.add(menuTabla);
		botones.add(distAncho);
		botones.add(nEnt);
		botones.add(nCampo);
		frame.setJMenuBar(menuBar);
		frame.add(botones, BorderLayout.NORTH);
		frame.add(mainPanel);
		frame.setVisible(true);
	}

	private void switchPanel(JTabbedPane panel) {
		mainPanel.removeAll();
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
}
