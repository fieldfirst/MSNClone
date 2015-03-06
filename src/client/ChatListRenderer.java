/*
 * 
 *  นายวรวุฒิ  เนตรลือชา  5510405791
 * 
 */
package client;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ChatListRenderer extends JLabel implements ListCellRenderer<Object> {

	private static final long serialVersionUID = -3392635087157534753L;

    private static final Color textSelectionColor = Color.BLACK;
    private static final Color backgroundSelectionColor = new Color(202, 246, 247);
    private static final Color textNonSelectionColor = Color.BLACK;
    private static final Color backgroundNonSelectionColor = Color.WHITE;
	
	public ChatListRenderer(){
        setOpaque(true);
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String txt = value.toString().split(":")[1];
		String color = value.toString().split(":")[0];
		
		iconSet(color);
        setText(txt);
        setIconTextGap(40);
        
        if (isSelected) {
            setBackground(backgroundSelectionColor);
            setForeground(textSelectionColor);
        } else {
            setBackground(backgroundNonSelectionColor);
            setForeground(textNonSelectionColor);
        }

        return this;
	}
	
	private void iconSet(String color) {
		switch (color) {
		case "GREEN" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.GREEN));
			break;
		case "RED" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.RED));
			break;
		case "BLUE" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.BLUE));
			break;
		case "YELLOW" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.YELLOW));
			break;
		case "ORANGE" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.ORANGE));
			break;
		case "CYAN" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.CYAN));
			break;
		case "PINK" :
			setIcon(new ColorBadgeIcon(ColorBadgeIcon.PINK));
			break;
		}
		
	}
	
}
