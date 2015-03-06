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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.Icon;

public class UserIcon implements Icon {
	
	private static final Color WHITE = Color.WHITE;
	private static final Color DARK_GRAY = new Color(186, 189, 189);
	private static final Color LIGHT_GRAY = new Color(208, 208, 208);

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return 50;
	}

	@Override
	public int getIconWidth() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D graphic = (Graphics2D)g;
		graphic.setBackground(WHITE);
		
		Arc2D a = new Arc2D.Double(0, 21, 55, 142, 45, 90, Arc2D.CHORD);
		graphic.setColor(LIGHT_GRAY);
		graphic.fill(a);
		graphic.setColor(WHITE);
		graphic.draw(a);
		
		Ellipse2D e = new Ellipse2D.Double(16, 11, 22, 22);
		graphic.setColor(DARK_GRAY);
		graphic.fill(e);
		graphic.setColor(DARK_GRAY);
		graphic.draw(e);
		
		Ellipse2D above = new Ellipse2D.Double(17, 10, 21, 21);
		graphic.setColor(LIGHT_GRAY);
		graphic.fill(above);
		graphic.setColor(WHITE);
		graphic.draw(above);
	}

}
