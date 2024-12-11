import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class tabla extends JPanel {
	private JTable tabla;
	private JPanel panel1;
	private JScrollPane scroll;
	private JButton button1;

	public tabla() {
		this.setVisible(true);
		this.setSize(300, 200);
		ArrayList<ArrayList<Dato>> list = Gestor2.cargarLista("lista.dat");
		Gestor2.showInfo(list);
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
		tabla = new JTable(model);
		scroll = new JScrollPane(tabla);
		this.add(scroll);
		button1 = new JButton("Button 1");
		button1.addActionListener(e -> ((CardLayout) this.getParent().getLayout()).previous(this.getParent()));
		this.add(button1);
	}
}