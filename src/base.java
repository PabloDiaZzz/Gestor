import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class base {

	public static JTable tabla(JFrame frame, ArrayList<ArrayList<Dato>> list, int[] anchos) {
		String[] columns = new String[list.getFirst().size()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = list.getFirst().get(i).getValor();
		}
		String[][] data = new String[list.size()-1][columns.length];
		for (int i = 1; i < list.size(); i++) {
			for (int j = 0; j < columns.length; j++) {
				data[i-1][j] = list.get(i).get(j).getValor();
			}
		}
		TableModel model = new DefaultTableModel(data, columns);
		final JTable[] tabla = {new JTable(model)};

		tabla[0].getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override public void columnAdded(TableColumnModelEvent e) {}
			@Override public void columnRemoved(TableColumnModelEvent e) {}

			private boolean isResettingColumn = false;
			@Override public void columnMoved(TableColumnModelEvent e) {
				if (isResettingColumn) {
					return;
				}

				int campo = e.getFromIndex();
				int campo2 = e.getToIndex();

				if (campo == campo2) {
					return;
				}
				ArrayList<Object> value = Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex());
				value.set(1, moverArray((int[]) value.get(1), campo, campo2));
				value.set(2, moverArray((int[]) value.get(2), campo, campo2));
				if (campo == 0 || campo2 == 0) {
					isResettingColumn = true;
					SwingUtilities.invokeLater(() -> {
						tabla[0].getColumnModel().moveColumn(campo2, campo);
						Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex());
						isResettingColumn = false;
					});
					JOptionPane.showMessageDialog(frame, "The first column cannot be moved.");
				} else if (tabla[0].getTableHeader().getColumnModel().getColumn(campo).getHeaderValue().equals(list.getFirst().get(campo2).getValor())) {
					for (int x = 0; x < list.size(); x++) {
						Dato temp = list.get(x).get(campo);
						list.get(x).set(campo, list.get(x).get(campo2));
						list.get(x).set(campo2, temp);
					}
				}
				ArrayList<ArrayList<Dato>> l = (ArrayList<ArrayList<Dato>>) Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex()).getFirst();

				OcultarCampos.columns.clear();
				TableColumnModel columnModel = tabla[0].getColumnModel();
				OcultarCampos.columns.addAll(Collections.list(columnModel.getColumns()));
				OcultarCampos.checkBoxColumnMap = new LinkedHashMap<>();
				OcultarCampos.listaCheckBoxPanel.removeAll();
				OcultarCampos.columns.stream().skip(1)  // Excluir la columna ID si es necesario
						.forEach(column -> {
							JCheckBox checkBox_1 = new JCheckBox(column.getHeaderValue().toString());
							checkBox_1.setSelected(column.getWidth() > 0);
							OcultarCampos.checkBoxColumnMap.put(checkBox_1, column);
							OcultarCampos.listaCheckBoxPanel.add(checkBox_1);
							if (checkBox_1.isSelected()) {
								OcultarCampos.columnasVisibles.add(column);
								OcultarCampos.columnasOcultas.remove(column);
							} else {
								OcultarCampos.columnasOcultas.add(column);
								OcultarCampos.columnasVisibles.remove(column);
							}
						});
				OcultarCampos.listaCheckBoxPanel.repaint();
				OcultarCampos.listaCheckBoxPanel.revalidate();
			}
			@Override public void columnMarginChanged(ChangeEvent e) {}
			@Override public void columnSelectionChanged(ListSelectionEvent e) {}
		});
		tabla[0].getModel().addTableModelListener(e -> {

			if (e.getType() == TableModelEvent.UPDATE) {
				TableModel modelo = (TableModel) e.getSource();
				int row = e.getFirstRow();
				int col = e.getColumn();
				Dato antiguoDato = new Dato(list.get(row+1).get(col).getValor(), row+1, list.getFirst().get(col).getTipo());
				Object value = modelo.getValueAt(row, col);
				list.get(row+1).get(col).setValor(value.toString());
				if (!list.get(row+1).get(col).checkTipo(0, false)) {
					JOptionPane.showMessageDialog(frame, "El dato no es valido, se intentara revertir el cambio");
					list.get(row+1).set(col, antiguoDato);
					list.get(row+1).get(col).checkTipo(1, true);
				} else {
					list.get(row+1).get(col).checkTipo(2, true);
				}
				if (!list.get(row+1).get(col).getValor().equals(tabla[0].getValueAt(row, col))) {
					SwingUtilities.invokeLater(() -> tabla[0].setValueAt(list.get(row+1).get(col).getValor(), row, col));
					Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex(), true, Gestor2GUI.getColumnWidths(tabla[0]), false);
				}
			}
		});

		tabla[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				int row = tabla[0].rowAtPoint(e.getPoint()) + 1;
				int col = tabla[0].columnAtPoint(e.getPoint());
				int[] rows = Arrays.stream(tabla[0].getSelectedRows()).map(r -> r + 1).toArray();
				if (rows.length == 0) {
					rows = new int[]{row};
				}
				if (col >= 0) {
					JPopupMenu popupMenu = new JPopupMenu();
					if (rows[0] > 0) {
						JMenuItem editItem = new JMenuItem("Editar");
						JMenuItem deleteRowItem = new JMenuItem("Borrar Entidad");
						JMenuItem info = new JMenuItem("Informacion");
						editItem.addActionListener(evt -> {
							String oldValue = list.get(row).get(col).getValor();
							String newValue = JOptionPane.showInputDialog(frame, "Edit value for " + list.get(row).get(0).getValor(), oldValue);
							if (newValue != null) {
								list.get(row).get(col).setValor(newValue);
								tabla[0].setValueAt(newValue, row - 1, col);
							}
						});
						info.addActionListener(evt -> {
							Info dialogo = new Info(row, col, frame);
						});
						int[] finalRows = rows;
						deleteRowItem.addActionListener(evt -> {
							int[] rowsReverse = Arrays.stream(finalRows).boxed().sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue).toArray();
							for (int r : rowsReverse) {
								list.remove(r);
								((DefaultTableModel) tabla[0].getModel()).removeRow(r - 1);
							}
						});
						if (rows.length == 1) {
							popupMenu.add(editItem);
							popupMenu.add(info);
						}
						popupMenu.add(deleteRowItem);
						popupMenu.show(tabla[0], e.getX(), e.getY());
					}
				}
			}
		});

		tabla[0].getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) showPopup(e);
			}

			private void showPopup(MouseEvent e) {

				int col = tabla[0].getTableHeader().columnAtPoint(e.getPoint());
				int[] cols = tabla[0].getSelectedColumns();
				if (cols.length == 0) {
					cols = new int[]{col};
				}
				if (cols[0] >= 0) {
					Gestor2GUI.value = Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex());
					int[] anchos1 = (int[]) Gestor2GUI.value.get(1);
					int[] anchos2 = (int[]) Gestor2GUI.value.get(2);
					JPopupMenu popupMenu = new JPopupMenu();
					JMenuItem deleteCampoItem = new JMenuItem("Borrar Campo");
					JMenuItem ordenar = new JMenuItem("Ordenar");
					JMenuItem tipo = new JMenuItem("Cambiar Tipo");
					int[] finalCols = cols;
					deleteCampoItem.addActionListener(evt -> {
						int[] colsReversed = Arrays.stream(finalCols).boxed().sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue).toArray();
						for (int c : colsReversed) {
							anchos1[c] = -1;
							anchos2[c] = -1;
							int columnCount = tabla[0].getModel().getColumnCount();
							DefaultTableModel newModel = new DefaultTableModel();
							for (int i = 0; i < columnCount; i++) {
								if (i != c) {
									newModel.addColumn(tabla[0].getModel().getColumnName(i));
								}
							}
							for (int i = 0; i < tabla[0].getRowCount(); i++) {
								Object[] fila = new Object[columnCount - 1];
								int columnIndex = 0;
								for (int j = 0; j < columnCount; j++) {
									if (j != c) {
										fila[columnIndex++] = tabla[0].getModel().getValueAt(i, j);
									}
								}
								newModel.addRow(fila);
							}
							tabla[0].setModel(newModel);

							for (ArrayList<Dato> d : list) {
								d.remove(col);
							}
						}
						Gestor2GUI.value.set(1, Arrays.stream(anchos1).filter(a -> a != -1).toArray());
						Gestor2GUI.value.set(2, Arrays.stream(anchos2).filter(a -> a != -1).toArray());
						Gestor2GUI.listas.put(Gestor2GUI.panel2.getSelectedIndex(), Gestor2GUI.value);
						Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex(), true, Gestor2GUI.getColumnWidths(tabla[0]), false);
					});
					ordenar.addActionListener(evt -> {
						Gestor2.sortBy = col;
						Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex(), true, Gestor2GUI.getColumnWidths(tabla[0]), false);
					});
					int[] finalCols1 = cols;
					tipo.addActionListener(evt -> {
						int n = JOptionPane.showOptionDialog(frame, "Si los valores no son validos se reestableceran", "Cambiar Tipo", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, Dato.tipos, 0);
						for (int campo : finalCols1) {
							for (int x = 1; x < list.size(); x++) {
								list.get(x).get(campo).setTipo(n);
								list.get(x).get(campo).formatTipo();
								if (list.get(x).get(campo).getId() != 0) {
									list.get(x).get(campo).checkTipo(1, true);
								}
								Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex(), true, Gestor2GUI.getColumnWidths(tabla[0]), false);
							}
						}
						Gestor2GUI.refreshTable(Gestor2GUI.panel2.getSelectedIndex());
					});
					if (cols.length == 1) {
						popupMenu.add(deleteCampoItem);
						popupMenu.add(ordenar);
					}

					if (cols[0] != 0) {
						popupMenu.add(tipo);
					}
					popupMenu.show(tabla[0].getTableHeader(), e.getX(), e.getY());
				}
			}
		});
		if (anchos == null) {
			for (int i = 0; i < tabla[0].getColumnCount(); i++) {
				tabla[0].getColumnModel().getColumn(i).setMinWidth(tabla[0].getTableHeader().getColumnModel().getColumn(i).getHeaderValue().toString().length() * 11);
			}
		} else {
			Gestor2GUI.setColumnWidths(tabla[0], anchos);
		}
		Gestor2.showInfo(list);
		for (int i = 0; i < tabla[0].getColumnCount(); i++) {
			TableColumn column = tabla[0].getColumnModel().getColumn(i);
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER); // o JLabel.LEFT, dependiendo de la alineación que desees
			column.setCellRenderer(renderer);
		}
		return tabla[0];
	}

	public static JTable tabla(JFrame frame, ArrayList<ArrayList<Dato>> list) {
		return tabla(frame, list, null);
	}

	public static ArrayList<ArrayList<Dato>> order(ArrayList<ArrayList<Dato>> list) {
		boolean changed = false;
		for (int x = 0; x < list.getFirst().size(); x++) {
			if (list.getFirst().get(x).getValor().matches(".*[△▽]")) {
				list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("△", ""));

				if (list.getFirst().get(x).getValor().matches(".*▽")) {
					list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("▽", ""));
					list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(x).getValor() + "▽");
					Gestor2.ordenar(list, Gestor2.sortBy,1);
				} else {
					list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(Gestor2.sortBy).getValor() + "△");
					Gestor2.ordenar(list, Gestor2.sortBy);
				}
				changed = true;
				break;
			}
		}
		if (!changed) {
			if (!list.getFirst().isEmpty()) {
				list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(Gestor2.sortBy).getValor() + "△");
			}
			Gestor2.ordenar(list, Gestor2.sortBy);
		}
		return list;
	}

	public static ArrayList<ArrayList<Dato>> altOrder(ArrayList<ArrayList<Dato>> list) {
		boolean changed = false;
		for (int x = 0; x < list.getFirst().size(); x++) {
			if (x == Gestor2.sortBy && list.getFirst().get(x).getValor().matches(".*△")) {
				list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("△", ""));
				list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(Gestor2.sortBy).getValor() + "▽");
				Gestor2.ordenar(list, Gestor2.sortBy, 1);
				changed = true;
			} else if (x == Gestor2.sortBy) {
				list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("▽", ""));
				list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(Gestor2.sortBy).getValor() + "△");
				Gestor2.ordenar(list, Gestor2.sortBy,0);
				changed = true;
			} else {
				list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("△", ""));
				list.getFirst().get(x).setValor(list.getFirst().get(x).getValor().replace("▽", ""));
			}


		}
		if (!changed) {
			list.getFirst().get(Gestor2.sortBy).setValor(list.getFirst().get(Gestor2.sortBy).getValor() + "△");
			Gestor2.ordenar(list, Gestor2.sortBy,0);
		}

		return list;
	}

	public static <T> T[] moverArray(T[] array, int index1, int index2) {
		T temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
		return array;
	}

	public static int[] moverArray(int[] array, int index1, int index2) {
		Integer[] arrayInt = Arrays.stream(array).boxed().toArray(Integer[]::new);
		return Arrays.stream(moverArray(arrayInt, index1, index2)).mapToInt(Integer::intValue).toArray();
	}
}
