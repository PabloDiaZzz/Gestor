import javax.swing.*;
import java.awt.*;

public class BotonRedondeado extends JButton {
	private int radio;
	private int diametro;

	public BotonRedondeado(String texto, int radio, int diametro) {
		super(texto);
		this.radio = radio;
		this.diametro = diametro;
		setPreferredSize(new Dimension(diametro, diametro));
		setContentAreaFilled(false);
		setBorder(BorderFactory.createEmptyBorder());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Fondo del botón
		if (getModel().isRollover()) {
			g2.setColor(new Color(255, 0, 0, 100)); // Color al pasar el ratón
		} else {
			g2.setColor(new Color(255, 255, 255, 0)); // Fondo transparente
		}
		g2.fillRoundRect(0, 0, diametro, diametro, radio, radio);

		// Dibuja el texto o el icono
		super.paintComponent(g2);
		g2.dispose();
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(new Color(0,0,0,0));
		g2.drawRoundRect(0, 0, diametro - 1, diametro - 1, radio, radio);
		g2.dispose();
	}

	public static JButton crearBotonRedondeado(String texto, int radio, int diametro) {
		return new BotonRedondeado(texto, radio, diametro);
	}
}
