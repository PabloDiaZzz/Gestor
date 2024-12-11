import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundImageButton extends JButton {

	private final int radius;
	private boolean isMouseOver = false;
	private boolean isMouseDown = false;
	private final int borderHeight;

	public RoundImageButton(ImageIcon icon, int radius, int borderHeight) {
		super(icon);
		this.radius = radius;
		this.borderHeight = borderHeight;
		setBorder(new RoundBorder(radius, borderHeight));
		setContentAreaFilled(false);
		setOpaque(false);
		setFocusPainted(false);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				isMouseOver = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isMouseOver = false;
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				isMouseDown = true;
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				isMouseDown = false;
				repaint();
			}
		});
	}

	public static class RoundBorder extends AbstractBorder {
		private final int radius;
		private final int borderHeight;

		public RoundBorder(int radius, int borderHeight) {
			this.radius = radius;
			this.borderHeight = borderHeight;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(new Color(255, 255, 255, 0));
			g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(borderHeight, radius, borderHeight, radius);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (isMouseDown) {
			g2d.setColor(new Color(180, 180, 180)); // pressed color
		} else if (isMouseOver) {
			g2d.setColor(new Color(186, 232, 227)); // hover color
		} else {
			g2d.setColor(getBackground());
		}

		g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
		super.paintComponent(g);
	}
}