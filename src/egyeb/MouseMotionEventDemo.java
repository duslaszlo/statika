/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egyeb;

/**
 *
 * @author duslaszlo
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MouseMotionEventDemo extends JPanel implements MouseMotionListener {
  private int mX, mY;

  public MouseMotionEventDemo() {
    addMouseMotionListener(this);
    setVisible(true);
  }

  public void mouseMoved(MouseEvent me) {
    mX = (int) me.getPoint().getX();
    mY = (int) me.getPoint().getY();
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    mouseMoved(me);
  }

  public void paint(Graphics g) {
    g.setColor(Color.blue);
    g.fillRect(mX, mY, 5, 5);
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.getContentPane().add(new MouseMotionEventDemo());
    f.setSize(800, 600);
    f.show();
  }

}