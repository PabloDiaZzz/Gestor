import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Info extends JDialog{
	public Info(int r, int c, JFrame parent) {
		super(parent, "Info - " + Gestor2GUI.panel2.getTitleAt(Gestor2GUI.panel2.getSelectedIndex()), false);
		JPanel main = new JPanel();
		setMinimumSize(new Dimension(160,160));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(parent);

		Gestor2GUI.value = Gestor2GUI.listas.get(Gestor2GUI.panel2.getSelectedIndex());
		ArrayList<ArrayList<Dato>> list = (ArrayList<ArrayList<Dato>>) Gestor2GUI.value.getFirst();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		JLabel labelTipo = new JLabel("<html><strong style=\"font-size: 110%;\">Tipo:</strong> " + list.get(r).get(c).getTipo());
		JLabel labelValor = new JLabel("<html><strong style=\"font-size: 110%;\">Valor: </strong>" +  list.get(r).get(c).getValor());
		JLabel labelPos = new JLabel("<html><strong style=\"font-size: 110%;\">Columna: </strong>" + c+1 + "<html><strong style=\"font-size: 110%;\"> - Fila: </strong>" + r);

		labelTipo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		labelValor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		labelPos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		main.add(labelTipo);
		main.add(labelValor);
		main.add(labelPos);

		setMinimumSize(new Dimension(170,160));

		revalidate();
		repaint();
		pack();

		setContentPane(main);
	}
}
