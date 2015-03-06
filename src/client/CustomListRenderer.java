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

public class CustomListRenderer extends JLabel implements ListCellRenderer<Object>{

	private static final long serialVersionUID = 4318803389290272883L;
    private static final Color textSelectionColor = Color.BLACK;
    private static final Color backgroundSelectionColor = new Color(202, 246, 247);
    private static final Color textNonSelectionColor = Color.BLACK;
    private static final Color backgroundNonSelectionColor = Color.WHITE;
	
	public CustomListRenderer(){
        setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setIcon(new UserIcon());
        setText(value.toString());
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

}
