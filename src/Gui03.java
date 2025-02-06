import javax.swing.*;
import java.awt.*;

public class Gui03 extends JFrame {

	public Gui03() {
		super("Ejemplo de Layout");
		// BorderLayout
		setLayout(new BorderLayout(5, 10));
		Container panel = new Container();
		panel.setLayout(new BorderLayout(5, 10));
		JButton aceptar = new JButton("Aceptar");
		aceptar.setSize(100, 50);
		panel.add(aceptar, BorderLayout.NORTH);
		JTextArea cancelar = new JTextArea();
		cancelar.setMinimumSize(new Dimension(50, 50));
		cancelar.setMaximumSize(new Dimension(150, 150));
		cancelar.setEditable(true);
		cancelar.setPreferredSize(new Dimension(50, 50));
		panel.add(cancelar, BorderLayout.SOUTH);
		add(panel, BorderLayout.WEST);
		setSize(500, 600); //pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		Gui03 aplicacion = new Gui03();
	}
}