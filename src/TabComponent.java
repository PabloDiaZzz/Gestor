import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

public class TabComponent extends JPanel {
	private final JTabbedPane tabbedPane;

	public TabComponent(JTabbedPane tabbedPane, String title) {
		SpringLayout springLayout = new SpringLayout();
		
		this.tabbedPane = tabbedPane;

		setOpaque(false);
		setLayout(new FormLayout(new ColumnSpec[] {
			ColumnSpec.decode("55px"),
			ColumnSpec.decode("7px"),
			FormSpecs.DEFAULT_COLSPEC
		},
		new RowSpec[] {
			RowSpec.decode("max(20px;default)")
		}));
																						
		JLabel label = new JLabel(title);
		springLayout.putConstraint(SpringLayout.NORTH, label, 114, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, this);
		add(label, "1, 1, left, center");

		BotonRedondeado closeButton = new BotonRedondeado("X", 100, 14);
		springLayout.putConstraint(SpringLayout.NORTH, closeButton, 134, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, closeButton, -10, SpringLayout.EAST, this);
		closeButton.setBounds(closeButton.getWidth() + 100, closeButton.getHeight(), closeButton.getWidth(), closeButton.getHeight());

		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = tabbedPane.indexOfTabComponent(TabComponent.this);
				if (index != -1) {
					tabbedPane.remove(index);
					for (int i = Gestor2GUI.panel2.getTabCount(); i >= index; i--) {
						Gestor2GUI.listas.put(i, Gestor2GUI.listas.get(i+1));
					}
					Gestor2GUI.listas.remove(Gestor2GUI.panel2.getTabCount());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				closeButton.setBackground(new Color(255, 0, 0, 100));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				closeButton.setBackground(new Color(0,0,0,0));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				closeButton.setBackground(new Color(255, 0, 0, 150));
			}
		});

		add(closeButton, "3, 1, right, center");
	}
}
