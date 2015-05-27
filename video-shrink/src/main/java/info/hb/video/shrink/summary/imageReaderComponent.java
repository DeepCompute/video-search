package info.hb.video.shrink.summary;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * Component class used in conjunction with imageReader.
 * @author Christopher Mangus
 * @author Louis Schwartz
 */
public class imageReaderComponent extends JComponent {

	private static final long serialVersionUID = -3903920207348650855L;

	/**
	 * Paint component method.
	 */
	@Override
	public void paintComponent(Graphics g) {

		// Recover Graphics2D
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img, 0, 0, this);
	}

	/**
	 * Sets this img to the new img.
	 * @param newimg The new BufferedImage
	 */
	public void setImg(BufferedImage newimg) {
		this.img = newimg;
	}

	private BufferedImage img;

}