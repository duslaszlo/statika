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
import java.awt.geom.*;

public class SimpleBasicStrokeExample extends JPanel {
public void initialize() {
  setBackground(Color.white);
  }
  public void paint(Graphics g) {
  Graphics2D g2d = (Graphics2D) g;
    g2d.setStroke(new BasicStroke(22.0f));
    g2d.setPaint(Color.red);
    Ellipse2D ellipse = new Ellipse2D.Double(45,45,250,250);
    g2d.draw(ellipse);
  }
  public static void main(String args[]) {
    JFrame frame = new JFrame("Show thick Stroke");
    SimpleBasicStrokeExample basicStrokeExample = new SimpleBasicStrokeExample();
    frame.getContentPane().add("Center", basicStrokeExample);
    basicStrokeExample.initialize();
    frame.setSize(new Dimension(350, 350));
    frame.show();
  }
} 