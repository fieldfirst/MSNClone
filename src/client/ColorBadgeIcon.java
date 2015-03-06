/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

public class ColorBadgeIcon implements Icon {
	
	public static final Color GREEN = new Color(94, 213, 98);
	public static final Color RED = new Color(215, 100, 100);
	public static final Color BLUE = new Color(100, 135, 215);
	public static final Color YELLOW = new Color(211, 215, 100);
	public static final Color ORANGE = new Color(215, 146, 100);
	public static final Color CYAN = new Color(100, 215, 207);
	public static final Color PINK = new Color(215, 100, 203);
	
	private Color color;
	
	public ColorBadgeIcon(Color c) {
		this.color = c;
	}

	@Override
	public int getIconHeight() {
		return 20;
	}

	@Override
	public int getIconWidth() {
		return 20;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D graphic = (Graphics2D) g;
		Rectangle2D rect = new Rectangle2D.Float(2, 2, 20, 20);
		graphic.setColor(color);
		graphic.draw(rect);
		graphic.fill(rect);
	}

}
