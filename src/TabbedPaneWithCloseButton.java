import javax.swing.*;

public class TabbedPaneWithCloseButton {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("TabbedPane con Botones de Cierre");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);

			JPanel panel = new JPanel();
//			panel.add(new JLabel("Contenido de Tab" + i));
//			tabbedPane.addTab("Tab" + i, panel);
//			tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabComponent(tabbedPane, "Tab" + i));
//
//
//			frame.add(tabbedPane);
			frame.setVisible(true);
		});
	}
}
