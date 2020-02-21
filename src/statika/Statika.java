/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

/**
 *
 * @author SD-LEAP
 */
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import java.awt.event.*;
import java.awt.*;
/*
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */
public class Statika extends JFrame
        implements ActionListener {

    JDesktopPane desktop;

    public Statika() {
        super("Statikai számítások és keresztmetszeti jellemők");

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        //createFrame_kettamaszu(); //create first "window"
        setContentPane(desktop);
        setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //A Projekt-ek bállítása
        JMenu menu0 = new JMenu("Projekt-beállítás");
        menu0.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu0);
        JMenuItem menuItem = new JMenuItem("Projektek");
        menuItem.setMnemonic(KeyEvent.VK_K);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("projekt");
        menuItem.addActionListener(this);
        menu0.add(menuItem);

        //Kéttámaszú és konzolos tartók szilárdsági számításai
        JMenu menu1 = new JMenu("Határozott tartók");
        menu1.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu1);
        JMenuItem menuItem1 = new JMenuItem("A projekt tartói");
        menuItem1.setMnemonic(KeyEvent.VK_K);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem1.setActionCommand("tartok");
        menuItem1.addActionListener(this);
        menu1.add(menuItem1);
        menuItem1 = new JMenuItem("A tartó erői");
        menuItem1.setMnemonic(KeyEvent.VK_A);
        menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem1.setActionCommand("tartoero");
        menuItem1.addActionListener(this);
        menu1.add(menuItem1);

        /*
         // Keretszerkezetek
         JMenu menu2 = new JMenu("Keretszerkezetek");
         menu2.setMnemonic(KeyEvent.VK_D);
         menuBar.add(menu2);
         */
        // Rácsos szerkezetek
        JMenu menu3 = new JMenu("Rácsos szerkezetek");
        menu3.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu3);
        JMenuItem menuItem3 = new JMenuItem("Drótvázak megjelenítése");
        menuItem3.setMnemonic(KeyEvent.VK_K);
        menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem3.setActionCommand("drotvaz");
        menuItem3.addActionListener(this);
        menu3.add(menuItem3);
        // Drótváztervezés - szekciók
        menuItem3 = new JMenuItem("Rácsszerkezet tervező ");
        menuItem3.setMnemonic(KeyEvent.VK_K);
        menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem3.setActionCommand("racstervezo");
        menuItem3.addActionListener(this);
        menu3.add(menuItem3);
        menuItem3 = new JMenuItem("Kockaváz tervezés");
        menuItem3.setMnemonic(KeyEvent.VK_K);
        menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem3.setActionCommand("kockatervezo");
        menuItem3.addActionListener(this);
        menu3.add(menuItem3);
        /*
         // Síkszerkezetek
         JMenu menu4 = new JMenu("Síkszerkezetek");
         menu4.setMnemonic(KeyEvent.VK_D);
         menuBar.add(menu4);
         */

        // Keresztmetszeti jellemzők kiszámoltatása
        JMenu menu5 = new JMenu("Keresztmetszeti jellemzők");
        menu5.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu5);
        JMenuItem menuItem5 = new JMenuItem("Keresztmetszeti jellemzők");
        menuItem5.setMnemonic(KeyEvent.VK_K);
        menuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
        menuItem5.setActionCommand("kmjell");
        menuItem5.addActionListener(this);
        menu5.add(menuItem5);
        menuItem5 = new JMenuItem("Lemezek definiálása");
        menuItem5.setMnemonic(KeyEvent.VK_A);
        menuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem5.setActionCommand("lemez");
        menuItem5.addActionListener(this);
        menu5.add(menuItem5);
        menuItem5 = new JMenuItem("Összetett szelvények");
        menuItem5.setMnemonic(KeyEvent.VK_A);
        menuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem5.setActionCommand("összetett");
        menuItem5.addActionListener(this);
        menu5.add(menuItem5);
        /*
         // Egyéb cuccok
         JMenu menu6 = new JMenu("Egyéb alkalmazások");
         menu6.setMnemonic(KeyEvent.VK_Q);
         menuBar.add(menu6);      */

        // A kilépés
        JMenu menu7 = new JMenu("Kilépés");
        menu7.setMnemonic(KeyEvent.VK_Q);
        menuBar.add(menu7);
        menuItem = new JMenuItem("Kilépés");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        menu7.add(menuItem);

        return menuBar;
    }

    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
        // A projektek        
        if ("projekt".equals(e.getActionCommand())) {
            createFrame_Projekt();
        } // Határozott tartók
        else if ("tartok".equals(e.getActionCommand())) {
            createFrame_Tartok();
        } else if ("tartoero".equals(e.getActionCommand())) {
            createFrame_Kettamaszu();
        } // Keretszerkezetek
        // Rácsos szerkezetek
        // Síkszerkezetek
        // Keresztmetszeti jellemzők
        else if ("kmjell".equals(e.getActionCommand())) {
            createFrame_Kmjell();
        } else if ("lemez".equals(e.getActionCommand())) {
            createFrame_Lemez();
        } else if ("összetett".equals(e.getActionCommand())) {
            createFrame_Kmjellossz();
        } else if ("drotvaz".equals(e.getActionCommand())) {
            createFrame_drotvaz();
        } else if ("racstervezo".equals(e.getActionCommand())) {
            createFrame_racstervezo();
        } else if ("kockatervezo".equals(e.getActionCommand())) {
            createFrame_kockatervezo();
        } else {
            quit();
        }

    }
    //Create a new internal frame.

    protected void createFrame_Projekt() {
        projekt frame = new projekt();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_Tartok() {
        tartok frame = new tartok();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_Kettamaszu() {
        kettamaszu frame = new kettamaszu();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_Kmjell() {
        kmjell frame = new kmjell();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_Lemez() {
        kmjellossz frame = new kmjellossz();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_Kmjellossz() {
        kmjellossz frame = new kmjellossz();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_drotvaz() {
        drotvaz frame = new drotvaz();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    protected void createFrame_racstervezo() {
        racstervezo frame = new racstervezo();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }
    
    protected void createFrame_kockatervezo() {
        kocka frame = new kocka();
        frame.setVisible(true); //necessary as of 1.3
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }
   
    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        Statika frame = new Statika();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}