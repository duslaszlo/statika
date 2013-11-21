/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egyeb;

/**
 *
 * @author SD-LEAP
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

class CustomCellRenderer
	extends		JLabel
	implements	TableCellRenderer
{
	private	boolean		isSelected;
	private	boolean		hasFocus;
	private	ImageIcon[]	suitImages;

	public CustomCellRenderer()
	{
		suitImages = new ImageIcon[4];
		suitImages[0] = new ImageIcon( "clubs.gif" );
		suitImages[1] = new ImageIcon( "diamonds.gif" );
		suitImages[2] = new ImageIcon( "spades.gif" );
		suitImages[3] = new ImageIcon( "hearts.gif" );
	}
	
	public Component getTableCellRendererComponent( JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int row, int column )
	{
		String	sText = (String)value;
		this.isSelected = isSelected;
		this.hasFocus = hasFocus;
		
		if( isSelected )
			setForeground( Color.red );
		else
			setForeground( Color.black );
		if( hasFocus )
			setForeground( Color.cyan );

		setIcon( suitImages[column] );

		setText( "" + row );
		return this;
	}

	// This is a hack to paint the background.  Normally a JLabel can
	// paint its own background, but due to an apparent bug or
	// limitation in the TreeCellRenderer, the paint method is
	// required to handle this.
	public void paint( Graphics g )
	{
		Color		bColor;

		// Set the correct background colour
		if( isSelected )
			bColor = Color.cyan;
		else
			bColor = Color.white;
		if( hasFocus )
			bColor = Color.red;
		g.setColor( bColor );

		// Draw a rectangle in the background of the cell
		g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );

		super.paint( g );
	}
}