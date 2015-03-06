/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class Message extends JLabel {
	
	private Color color;
	private String position;
	
	private static final long serialVersionUID = 8505494595288729L;
	
	public static final Color GREEN = new Color(94, 213, 98);
	public static final Color RED = new Color(215, 100, 100);
	public static final Color BLUE = new Color(100, 135, 215);
	public static final Color YELLOW = new Color(211, 215, 100);
	public static final Color ORANGE = new Color(215, 146, 100);
	public static final Color CYAN = new Color(100, 215, 207);
	public static final Color PINK = new Color(215, 100, 203);
	
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	
	public Message(String str, Color color, String position) {
		super(str);
		setBorder(new EmptyBorder(5, 10, 12, 10));
		setForeground(Color.WHITE);
		this.color = color;
		this.position = position;
		setVerticalAlignment(CENTER);
		setHorizontalAlignment(CENTER);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D graph = (Graphics2D) g;
		RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight()-7, 10, 10);
		graph.setColor(color);
		graph.fill(rect);
		graph.draw(rect);

		Polygon p = new Polygon();
		
		if (position.equals("LEFT")) {
			p.addPoint(7, this.getHeight()-7);
			p.addPoint(5, this.getHeight());
			p.addPoint(20, this.getHeight()-7);
		}
		else {
			p.addPoint(this.getWidth()-7, this.getHeight()-7);
			p.addPoint(this.getWidth()-5, this.getHeight());
			p.addPoint(this.getWidth()-20, this.getHeight()-7);
		}
		graph.fill(p);
		graph.drawPolygon(p);
		
		super.paintComponent(g);
		
	}
	
}
