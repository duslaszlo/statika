/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SD-LEAP
 */
public class racstervezo extends javax.swing.JInternalFrame {

    /**
     * Creates new form racselemtervezo
     */
    static Connection co;
    static Statement st;
    static ResultSet rs;
    racstervezoadatok racs = new racstervezoadatok();

    public racstervezo() {
        initComponents();
        racs.nev = "";
        racselemek_kijelzo_torles();
        mentes.setEnabled(false);
        // A mintaelem beolvasása
        racs.mintaindexf = 0;
        racs.mintaindexv = 0;
        racs.mintarudindex = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A függőleges mintakoordináták beolvasása
            racs.parancs = "SELECT x,y,z,jellegxy,jellegyz,kezdcspxy,vegecspxy,kezdcspyz,vegecspyz FROM mintacsp where irany = 1 order by csomopont;";
            rs = st.executeQuery(racs.parancs);
            while (rs.next()) {
                racs.mintaindexf++;
                racs.mintacspf[racs.mintaindexf][0] = rs.getFloat("x");
                racs.mintacspf[racs.mintaindexf][1] = rs.getFloat("y");
                racs.mintacspf[racs.mintaindexf][2] = rs.getFloat("z");
                racs.mintacspfjelleg[racs.mintaindexf][0] = rs.getInt("jellegxy");
                racs.mintacspfjelleg[racs.mintaindexf][1] = rs.getInt("jellegyz");
                racs.mintacspfjelleg[racs.mintaindexf][2] = rs.getInt("kezdcspxy");
                racs.mintacspfjelleg[racs.mintaindexf][3] = rs.getInt("vegecspxy");
                racs.mintacspfjelleg[racs.mintaindexf][4] = rs.getInt("kezdcspyz");
                racs.mintacspfjelleg[racs.mintaindexf][5] = rs.getInt("vegecspyz");
            }
            rs.close();
            // A vízszintes mintakoordináták beolvasása
            racs.parancs = "SELECT x,y,z FROM mintacsp where irany = 2 order by csomopont;";
            rs = st.executeQuery(racs.parancs);
            while (rs.next()) {
                racs.mintaindexv++;
                racs.mintacspv[racs.mintaindexv][0] = rs.getInt("x");
                racs.mintacspv[racs.mintaindexv][1] = rs.getInt("y");
                racs.mintacspv[racs.mintaindexv][2] = rs.getInt("z");
            }
            rs.close();
            racs.parancs = "SELECT irany,tipus,verzio,kezdocsp,vegecsp FROM mintarud order by irany,tipus,verzio;";
            rs = st.executeQuery(racs.parancs);
            while (rs.next()) {
                racs.mintarudindex++;
                racs.mintarud[racs.mintarudindex][0] = rs.getInt("irany");
                racs.mintarud[racs.mintarudindex][1] = rs.getInt("tipus");
                racs.mintarud[racs.mintarudindex][2] = rs.getInt("verzio");
                racs.mintarud[racs.mintarudindex][3] = rs.getInt("kezdocsp");
                racs.mintarud[racs.mintarudindex][4] = rs.getInt("vegecsp");
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        //System.out.println(racs.mintaindexf + "  "+racs.mintaindexv + "  " + racs.mintarudindex);
        drotvazak.removeAllItems();
        drotvazak.addItem("Válassz");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A projekt nevének a beolvasása
            racs.parancs = "SELECT distinct nev FROM racsalap order by nev;";
            rs = st.executeQuery(racs.parancs);
            while (rs.next()) {
                drotvazak.addItem(rs.getString("nev"));
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A szelvények feltöltése
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A szelvénytár beolvasása
            rs = st.executeQuery("SELECT nev FROM szelveny order by nev ");
            racsrudnev1.addItem("");
            racsrudnev2.addItem("");
            racsrudnev3.addItem("");
            racsrudnev4.addItem("");
            racsrudnev5.addItem("");
            racsrudnev6.addItem("");
            racsrudnev7.addItem("");
            racsrudnev8.addItem("");
            while (rs.next()) {
                racsrudnev1.addItem(rs.getString(1));
                racsrudnev2.addItem(rs.getString(1));
                racsrudnev3.addItem(rs.getString(1));
                racsrudnev4.addItem(rs.getString(1));
                racsrudnev5.addItem(rs.getString(1));
                racsrudnev6.addItem(rs.getString(1));
                racsrudnev7.addItem(rs.getString(1));
                racsrudnev8.addItem(rs.getString(1));
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        racsrudnev1.setEnabled(false);
        racsrudnev2.setEnabled(false);
        racsrudnev3.setEnabled(false);
        racsrudnev4.setEnabled(false);
        racsrudnev5.setEnabled(false);
        racsrudnev6.setEnabled(false);
        racsrudnev7.setEnabled(false);
        racsrudnev8.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ujelemnev = new javax.swing.JTextField();
        ujelem = new javax.swing.JButton();
        drotvazak = new javax.swing.JComboBox();
        drotvaz_kivalaszto = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        szekcioadatok = new javax.swing.JTable();
        Elemmodosito = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        teljes_nezet = new javax.swing.JLabel();
        szekcio_nezet = new javax.swing.JLabel();
        szekciok = new javax.swing.JComboBox();
        szekcio_kivalaszto = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        felsoszelyz = new javax.swing.JTextField();
        elemhozzado = new javax.swing.JButton();
        felsoszelxy = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        keresni = new javax.swing.JRadioButton();
        alsoszelyz = new javax.swing.JTextField();
        irany = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        alsoszelxy = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        Magassag = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        kapcsx = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        elemszamok = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        kapcsy = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        kapcsz = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        eltolasxy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        eltolasyz = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        konzolhossz = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        csomopontszam = new javax.swing.JTextField();
        rudszam = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        teljes_suly = new javax.swing.JTextField();
        szekcio_suly = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel23 = new javax.swing.JLabel();
        ftipus7 = new javax.swing.JSlider();
        ftipus6 = new javax.swing.JSlider();
        ftipus5 = new javax.swing.JSlider();
        ftipus4 = new javax.swing.JSlider();
        ftipus3 = new javax.swing.JSlider();
        ftipus2 = new javax.swing.JSlider();
        ftipus1 = new javax.swing.JSlider();
        ftipus8 = new javax.swing.JSlider();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        vtipus1 = new javax.swing.JSlider();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        vtipus2 = new javax.swing.JSlider();
        vtipus3 = new javax.swing.JSlider();
        vtipus4 = new javax.swing.JSlider();
        vtipus5 = new javax.swing.JSlider();
        vtipus6 = new javax.swing.JSlider();
        kozok = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        kozkivalaszto = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        ftipus1text = new javax.swing.JTextField();
        ftipus2text = new javax.swing.JTextField();
        ftipus3text = new javax.swing.JTextField();
        ftipus4text = new javax.swing.JTextField();
        ftipus6text = new javax.swing.JTextField();
        ftipus5text = new javax.swing.JTextField();
        ftipus7text = new javax.swing.JTextField();
        ftipus8text = new javax.swing.JTextField();
        vtipus1text = new javax.swing.JTextField();
        vtipus2text = new javax.swing.JTextField();
        vtipus3text = new javax.swing.JTextField();
        vtipus4text = new javax.swing.JTextField();
        vtipus5text = new javax.swing.JTextField();
        vtipus6text = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        racsrudnev1 = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        racsrudnev2 = new javax.swing.JComboBox();
        racsrudnev4 = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        racsrudnev3 = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        racsrudnev5 = new javax.swing.JComboBox();
        jLabel41 = new javax.swing.JLabel();
        racsrudnev6 = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        racsrudnev7 = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        racsrudnev8 = new javax.swing.JComboBox();
        Szelveny_hozzarendelo = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        csomopontlista = new javax.swing.JTable();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        rudlista = new javax.swing.JTable();
        rudlista_megjelolo = new javax.swing.JButton();
        csomopontlista_megjelolo = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        racskoz_valtoztato = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        racskozok = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        szekciohossz = new javax.swing.JTextField();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel49 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        szelvenysulyok = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        vastagvonalak = new javax.swing.JRadioButton();
        mentes = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Drótváztervező ");
        setPreferredSize(new java.awt.Dimension(1300, 700));

        ujelem.setText("Új drótváz");
        ujelem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ujelemActionPerformed(evt);
            }
        });

        drotvaz_kivalaszto.setText("Kiválaszt");
        drotvaz_kivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drotvaz_kivalasztoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabel1.setText("Meglévő drótváz:");

        szekcioadatok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null,  new Boolean(true), null}
            },
            new String [] {
                "Sorszám", "Magasság", "AlsóXY", "AlsóYZ", "FelsőXY", "FelsőYZ", "DiffX", "DiffY", "DiffZ", "Eltolásx", "EltolásY", "Konzol", "Függ.", "Törölni"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(szekcioadatok);

        Elemmodosito.setText("Szekció-elemek módosítása");
        Elemmodosito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ElemmodositoActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setToolTipText("");

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        teljes_nezet.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        teljes_nezet.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                teljes_nezetMouseWheelMoved(evt);
            }
        });
        teljes_nezet.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                teljes_nezetMouseDragged(evt);
            }
        });

        szekcio_nezet.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        szekcio_nezet.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                szekcio_nezetMouseWheelMoved(evt);
            }
        });
        szekcio_nezet.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                szekcio_nezetMouseDragged(evt);
            }
        });

        szekcio_kivalaszto.setText("Mehet");
        szekcio_kivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szekcio_kivalasztoActionPerformed(evt);
            }
        });

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        felsoszelyz.setText("100");

        elemhozzado.setText("Szekció hozzáadás");
        elemhozzado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elemhozzadoActionPerformed(evt);
            }
        });

        felsoszelxy.setText("200");

        jLabel8.setText("Felső szélesség YZ:");

        keresni.setText("Keresni");

        alsoszelyz.setText("300");

        irany.setText("Vízszintes");

        jLabel7.setText("Felső szélesség XY:");

        alsoszelxy.setText("300");

        jLabel6.setText("Alsó szélesség YZ:");

        Magassag.setText("500");

        jLabel5.setText("Alsó szélesség XY:");

        kapcsx.setText("0");

        jLabel9.setText("Kapcsolati X-koord:");

        elemszamok.setText("2");

        jLabel3.setText("Elemszám:");

        jLabel10.setText("Kapcsolati Y-koord:");

        kapcsy.setText("0");

        jLabel11.setText("Kapcsolati Z-koord:");

        kapcsz.setText("0");

        jLabel12.setText("Eltolás XY:");

        eltolasxy.setText("0");

        jLabel13.setText("Eltolás YZ:");

        eltolasyz.setText("0");

        jLabel4.setText("Magasság:");

        jLabel14.setText("Konzol:");

        konzolhossz.setText("0");

        jLabel32.setText("Csomópontszám:");

        jLabel44.setText("Rúdszám:");

        csomopontszam.setEditable(false);
        csomopontszam.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        csomopontszam.setText(" ");

        rudszam.setEditable(false);
        rudszam.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rudszam.setText(" ");

        jLabel45.setText("Összsúly:");

        jLabel46.setText("Szekciósúly:");

        teljes_suly.setEditable(false);
        teljes_suly.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        szekcio_suly.setEditable(false);
        szekcio_suly.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel47.setText("Kg");

        jLabel48.setText("Kg");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(61, 61, 61)
                        .addComponent(elemhozzado)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel45))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(csomopontszam)
                                .addGap(31, 31, 31))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(teljes_suly, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel47)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel44))
                            .addComponent(jLabel46))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rudszam, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(szekcio_suly))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)
                        .addGap(72, 72, 72))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Magassag, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(elemszamok, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(alsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(alsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(felsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(felsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kapcsx, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kapcsy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kapcsz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(eltolasxy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(eltolasyz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(konzolhossz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(irany, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(keresni, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(26, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(265, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(Magassag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(elemszamok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(keresni))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(irany))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(felsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(felsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(elemhozzado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kapcsx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kapcsy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kapcsz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eltolasxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eltolasyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(konzolhossz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(csomopontszam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(rudszam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(teljes_suly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(jLabel46)
                    .addComponent(szekcio_suly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addContainerGap(64, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(66, 66, 66)
                    .addComponent(jLabel5)
                    .addContainerGap(379, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Új szekció", jPanel1);

        jLabel2.setText("Tipus4:");

        jLabel15.setText("Tipus5:");

        jLabel16.setText("Tipus6:");

        jLabel17.setText("Tipus7:");

        jLabel18.setText("Tipus8:");

        jLabel19.setText("Tipus1:");

        jLabel20.setText("Tipus2:");

        jLabel21.setText("Tipus3:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Függőleges elemek:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("Vízszintes elemek:");

        ftipus7.setMaximum(6);
        ftipus7.setMinorTickSpacing(1);
        ftipus7.setValue(0);
        ftipus7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus7StateChanged(evt);
            }
        });

        ftipus6.setMaximum(6);
        ftipus6.setMinorTickSpacing(1);
        ftipus6.setValue(0);
        ftipus6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus6StateChanged(evt);
            }
        });

        ftipus5.setMaximum(6);
        ftipus5.setMinorTickSpacing(1);
        ftipus5.setValue(0);
        ftipus5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus5StateChanged(evt);
            }
        });

        ftipus4.setMaximum(8);
        ftipus4.setMinorTickSpacing(1);
        ftipus4.setValue(0);
        ftipus4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus4StateChanged(evt);
            }
        });

        ftipus3.setMaximum(3);
        ftipus3.setMinorTickSpacing(1);
        ftipus3.setToolTipText("");
        ftipus3.setValue(0);
        ftipus3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus3StateChanged(evt);
            }
        });

        ftipus2.setMajorTickSpacing(1);
        ftipus2.setMaximum(2);
        ftipus2.setValue(0);
        ftipus2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus2StateChanged(evt);
            }
        });

        ftipus1.setMaximum(4);
        ftipus1.setMinimum(1);
        ftipus1.setMinorTickSpacing(1);
        ftipus1.setSnapToTicks(true);
        ftipus1.setValue(0);
        ftipus1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus1StateChanged(evt);
            }
        });

        ftipus8.setMaximum(6);
        ftipus8.setMinorTickSpacing(1);
        ftipus8.setValue(0);
        ftipus8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus8StateChanged(evt);
            }
        });

        jLabel26.setText("Tipus1:");

        jLabel27.setText("Tipus2:");

        vtipus1.setMaximum(2);
        vtipus1.setMinimum(1);
        vtipus1.setMinorTickSpacing(1);
        vtipus1.setSnapToTicks(true);
        vtipus1.setValue(1);
        vtipus1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus1StateChanged(evt);
            }
        });

        jLabel28.setText("Tipus3:");

        jLabel29.setText("Tipus4:");

        jLabel30.setText("Tipus5:");

        jLabel31.setText("Tipus6:");

        vtipus2.setMaximum(1);
        vtipus2.setMinorTickSpacing(1);
        vtipus2.setSnapToTicks(true);
        vtipus2.setValue(0);
        vtipus2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus2StateChanged(evt);
            }
        });

        vtipus3.setMaximum(1);
        vtipus3.setMinorTickSpacing(1);
        vtipus3.setSnapToTicks(true);
        vtipus3.setValue(0);
        vtipus3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus3StateChanged(evt);
            }
        });

        vtipus4.setMaximum(3);
        vtipus4.setMinimum(1);
        vtipus4.setMinorTickSpacing(1);
        vtipus4.setSnapToTicks(true);
        vtipus4.setValue(0);
        vtipus4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus4StateChanged(evt);
            }
        });

        vtipus5.setMaximum(2);
        vtipus5.setMinorTickSpacing(1);
        vtipus5.setSnapToTicks(true);
        vtipus5.setValue(0);
        vtipus5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus5StateChanged(evt);
            }
        });

        vtipus6.setMaximum(3);
        vtipus6.setMinorTickSpacing(1);
        vtipus6.setSnapToTicks(true);
        vtipus6.setValue(0);
        vtipus6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vtipus6StateChanged(evt);
            }
        });

        kozkivalaszto.setText("Mehet");
        kozkivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kozkivalasztoActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setText("Szekción belüli közök:");

        ftipus1text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus1textActionPerformed(evt);
            }
        });

        ftipus2text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus2textActionPerformed(evt);
            }
        });

        ftipus3text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus3textActionPerformed(evt);
            }
        });

        ftipus4text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus4textActionPerformed(evt);
            }
        });

        ftipus6text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus6textActionPerformed(evt);
            }
        });

        ftipus5text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus5textActionPerformed(evt);
            }
        });

        ftipus7text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus7textActionPerformed(evt);
            }
        });

        ftipus8text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus8textActionPerformed(evt);
            }
        });

        vtipus1text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus1textActionPerformed(evt);
            }
        });

        vtipus2text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus2textActionPerformed(evt);
            }
        });

        vtipus3text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus3textActionPerformed(evt);
            }
        });

        vtipus4text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus4textActionPerformed(evt);
            }
        });

        vtipus5text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus5textActionPerformed(evt);
            }
        });

        vtipus6text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vtipus6textActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Szelvény hozzárendelés:");

        jLabel38.setText("Rúd1:");

        jLabel35.setText("Rúd2:");

        jLabel39.setText("Rúd4:");

        jLabel36.setText("Rúd3:");

        jLabel40.setText("Rúd5:");

        jLabel41.setText("Rúd6:");

        jLabel42.setText("Rúd7:");

        jLabel43.setText("Rúd8:");

        Szelveny_hozzarendelo.setText("Hozzárendelés");
        Szelveny_hozzarendelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Szelveny_hozzarendeloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ftipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(63, 63, 63))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ftipus7text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus8, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus8text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator6)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(kozok, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(kozkivalaszto))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel19)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ftipus1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ftipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel20))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(ftipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel2)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(ftipus2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                            .addComponent(ftipus4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ftipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ftipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel30)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(vtipus5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel28)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(vtipus3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(vtipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(vtipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel29)
                                            .addComponent(jLabel31))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(vtipus6, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                            .addComponent(vtipus4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(vtipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(vtipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vtipus1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vtipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vtipus2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(vtipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel23))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel42)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev7, 0, 123, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel40)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel41)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev6, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel43)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel36)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel38)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(racsrudnev1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35)
                                    .addComponent(jLabel39))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(racsrudnev4, 0, 142, Short.MAX_VALUE)
                                    .addComponent(racsrudnev2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                                .addComponent(Szelveny_hozzarendelo)))
                        .addGap(0, 50, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(kozok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kozkivalaszto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftipus1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(ftipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ftipus2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel21)
                        .addComponent(ftipus3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(ftipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(ftipus4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(ftipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(ftipus5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(ftipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ftipus6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ftipus7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(ftipus7text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ftipus8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ftipus8text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(jLabel27)
                        .addComponent(vtipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vtipus1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vtipus2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vtipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel29)
                        .addComponent(vtipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vtipus3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vtipus4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vtipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(vtipus5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vtipus6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(vtipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vtipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(Szelveny_hozzarendelo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(racsrudnev1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(racsrudnev2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(racsrudnev3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39)
                    .addComponent(racsrudnev4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(racsrudnev5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(racsrudnev6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(racsrudnev7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(racsrudnev8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        vtipus3.getAccessibleContext().setAccessibleName("80");

        jTabbedPane1.addTab("Szerkezetsablon", jPanel2);

        csomopontlista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ssz", "ID", "X-koord", "Y-koord", "Z-koord", "Kijelzés"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(csomopontlista);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setText("Csomópontlista:");

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel51.setText("Rúdlista:");

        rudlista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ssz", "ID", "Kezd", "Vége", "Szelvény", "Hossz", "Súly (kg)", "Kijelzés"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(rudlista);

        rudlista_megjelolo.setText("Rúdlista megjelölés");
        rudlista_megjelolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rudlista_megjeloloActionPerformed(evt);
            }
        });

        csomopontlista_megjelolo.setText("Csomópont megjelölés");
        csomopontlista_megjelolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csomopontlista_megjeloloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                        .addComponent(csomopontlista_megjelolo, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rudlista_megjelolo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(csomopontlista_megjelolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rudlista_megjelolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114))
        );

        jTabbedPane1.addTab("Alkotóelemek", jPanel3);

        racskoz_valtoztato.setText("Változtat");
        racskoz_valtoztato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                racskoz_valtoztatoActionPerformed(evt);
            }
        });

        racskozok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sorszám", "Magasság/hossz"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(racskozok);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("A szekció magassága/hossza:");

        szekciohossz.setEditable(false);

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel49.setText("Szelvényfelhasználás:");

        szelvenysulyok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sorszám", "Szelvénynév", "Hossz (mm)", "Súly (Kg)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(szelvenysulyok);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(271, 271, 271)
                            .addComponent(racskoz_valtoztato))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(szekciohossz, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jSeparator8))
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(szekciohossz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(racskoz_valtoztato)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Rácsközök", jPanel4);

        jLabel25.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        jLabel25.setText("Aktuális szekció:");

        jLabel37.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        jLabel37.setText("Teljes drótváz:");

        vastagvonalak.setText("Aktuális rúdvastagságok");
        vastagvonalak.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vastagvonalakStateChanged(evt);
            }
        });

        mentes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/bigfolder.png"))); // NOI18N
        mentes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mentesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 26, Short.MAX_VALUE)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(szekciok, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(szekcio_kivalaszto, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(szekcio_nezet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(vastagvonalak, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(teljes_nezet, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ujelemnev, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ujelem)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drotvazak, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(drotvaz_kivalaszto, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Elemmodosito, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mentes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(drotvazak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(drotvaz_kivalaszto)
                            .addComponent(ujelemnev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ujelem))
                        .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mentes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Elemmodosito)
                        .addGap(9, 9, 9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(szekciok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(szekcio_kivalaszto)
                            .addComponent(jLabel25)
                            .addComponent(jLabel37)
                            .addComponent(vastagvonalak))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(teljes_nezet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(szekcio_nezet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator4)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Rácsszerkezet beállítás");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void drotvaz_kivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drotvaz_kivalasztoActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        szekciok.removeAllItems();
        racs.nev = drotvazak.getSelectedItem().toString();
        racs.adatbeolvaso();
        szelvenysulyok_tablatorlo();
        csomopontlista_tablatorlo();
        rudlista_tablatorlo();
        csomopontszam.setText(String.valueOf(racs.csomopontindex));
        rudszam.setText(String.valueOf(racs.rudindex));
        szekciok.addItem("Válassz");
        if (racs.szekcioszam != 0) {
            for (int i = 1; i <= racs.szekcioszam; i++) {
                szekciok.addItem(racs.nev + " - szekció: " + i);
            }
            // vannak rácselemek
            int j = tableModel.getRowCount();
            if (j > 0) {
                for (int k = 0; k < j; k++) {
                    tableModel.removeRow(0);
                }
            }
            for (int i = 1; i <= racs.szekcioszam; i++) {
                String[] data = new String[13];
                for (int k = 0; k < 12; k++) {
                    data[k] = String.valueOf(racs.adatok[i][k]);
                }
                tableModel.addRow(data);
                if (racs.adatok[i][12] == 1) {
                    tableModel.setValueAt(true, i - 1, 12);
                } else {
                    tableModel.setValueAt(false, i - 1, 12);
                }
            }
            // A 2-4 -es oszlopok középre igazítása
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            for (int k = 0; k < 12; k++) {
                szekcioadatok.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
            }
            // A tábla oszlopszélességei
            szekcioadatok.setAutoResizeMode(szekcioadatok.AUTO_RESIZE_OFF);
            szekcioadatok.getColumnModel().getColumn(0).setPreferredWidth(70);
            szekcioadatok.getColumnModel().getColumn(1).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(2).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(3).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(4).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(5).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(6).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(7).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(8).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(9).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(10).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(11).setPreferredWidth(75);
            // A logikai kapcsolók
            szekcioadatok.getColumnModel().getColumn(12).setPreferredWidth(70);
            szekcioadatok.getColumnModel().getColumn(13).setPreferredWidth(70);
            szekcioadatok.setModel(tableModel);
            szekcioadatok.setShowGrid(true);
            racs.racselemek();
            racs.racsrudvastagsag();
            // kirajzoltatás    
            racskozok_torlo();
            racselemek_kijelzo_torles();
            rudnevek_kijelzo();
            szekcio_keptorlo();
            teljes_kepkitevo();
            szekciohossz.setText("");
            teljes_suly.setText("");
            szekcio_suly.setText("");
            /*System.out.println("A csomóponti adatok:");
             for (int k = 0; k < racs.csomopontindex; k++) {
             System.out.println("csp:" + k
             + "; szekcio:" + racs.csomopont[k][0]
             + "; x:" + racs.csomopont[k][1]
             + "; y:" + racs.csomopont[k][2]
             + "; z:" + racs.csomopont[k][3]);
             } */
        }
    }//GEN-LAST:event_drotvaz_kivalasztoActionPerformed

    private void racselemek_kijelzo_torles() {
        vtipus1.setEnabled(false);
        vtipus2.setEnabled(false);
        vtipus3.setEnabled(false);
        vtipus4.setEnabled(false);
        vtipus5.setEnabled(false);
        vtipus6.setEnabled(false);
        ftipus1.setEnabled(false);
        ftipus2.setEnabled(false);
        ftipus3.setEnabled(false);
        ftipus4.setEnabled(false);
        ftipus5.setEnabled(false);
        ftipus6.setEnabled(false);
        ftipus7.setEnabled(false);
        ftipus8.setEnabled(false);
    }

    private void rudnevek_kijelzo() {
        racsrudnev1.setEnabled(false);
        racsrudnev2.setEnabled(false);
        racsrudnev3.setEnabled(false);
        racsrudnev4.setEnabled(false);
        racsrudnev5.setEnabled(false);
        racsrudnev6.setEnabled(false);
        racsrudnev7.setEnabled(false);
        racsrudnev8.setEnabled(false);
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][1] > 0) {
            racsrudnev1.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][2] > 0) {
            racsrudnev2.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][3] > 0) {
            racsrudnev3.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][4] > 0) {
            racsrudnev4.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][5] > 0) {
            racsrudnev5.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][6] > 0) {
            racsrudnev6.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][7] > 0) {
            racsrudnev7.setEnabled(true);
        }
        if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][8] > 0) {
            racsrudnev8.setEnabled(true);
        }
    }

    private void racselemek_kijelzo() {
        racselemek_kijelzo_torles();
        if (szekciok.getSelectedIndex() != 0) {
            if (racs.adatok[szekciok.getSelectedIndex()][12] == 1) {
                ftipus1.setEnabled(true);
                ftipus2.setEnabled(true);
                ftipus3.setEnabled(true);
                ftipus4.setEnabled(true);
                ftipus5.setEnabled(true);
                ftipus6.setEnabled(true);
                ftipus7.setEnabled(true);
                ftipus8.setEnabled(true);
                // A fúggőleges elemek kijelzése
                ftipus1.setValue(racs.racselemek[1]);
                ftipus2.setValue(racs.racselemek[2]);
                ftipus3.setValue(racs.racselemek[3]);
                ftipus4.setValue(racs.racselemek[4]);
                ftipus5.setValue(racs.racselemek[5]);
                ftipus6.setValue(racs.racselemek[6]);
                ftipus7.setValue(racs.racselemek[7]);
                ftipus8.setValue(racs.racselemek[8]);
            } else {
                // a vízszintes elemek kijelzése
                vtipus1.setEnabled(true);
                vtipus2.setEnabled(true);
                vtipus3.setEnabled(true);
                vtipus4.setEnabled(true);
                vtipus5.setEnabled(true);
                vtipus6.setEnabled(true);
                vtipus1.setValue(racs.racselemek[1]);
                vtipus2.setValue(racs.racselemek[2]);
                vtipus3.setValue(racs.racselemek[3]);
                vtipus4.setValue(racs.racselemek[4]);
                vtipus5.setValue(racs.racselemek[5]);
                vtipus6.setValue(racs.racselemek[6]);
            }
        }
    }

    private void szekcio_kepkitevo() {
        int vastag = 0;
        if (vastagvonalak.isSelected()) {
            vastag = 1;
        }
        racs.pngrajz(szekciok.getSelectedIndex(), vastag, kozok.getSelectedIndex() + 1);
        ImageIcon icon = new ImageIcon(racs.bi1);
        icon.getImage().flush();
        szekcio_nezet.setIcon(icon);
        szekcio_nezet.updateUI();
    }

    private void szekcio_keptorlo() {
        ImageIcon icon = new ImageIcon(racs.bi3);
        icon.getImage().flush();
        szekcio_nezet.setIcon(icon);
        szekcio_nezet.updateUI();
    }

    private void teljes_kepkitevo() {
        int vastag = 0;
        if (vastagvonalak.isSelected()) {
            vastag = 1;
        }
        racs.pngrajz(0, vastag, szekciok.getSelectedIndex());
        ImageIcon icon = new ImageIcon(racs.bi2);
        icon.getImage().flush();
        teljes_nezet.setIcon(icon);
        teljes_nezet.updateUI();
    }

    private void ujelemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ujelemActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        racs.nev = ujelemnev.getText();
        ujelemnev.removeAll();
        // A szekcióadatok tábla kitörlése
        int j = tableModel.getRowCount();
        if (j > 0) {
            for (int k = 0; k < j; k++) {
                tableModel.removeRow(0);
            }
        }
        racs.szekcioszam = 0;
        racselemek_kijelzo_torles();
    }//GEN-LAST:event_ujelemActionPerformed

    private void racskozok_torlo() {
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        int j = tableModel.getRowCount();
        if (j > 0) {
            for (int k = 0; k < j; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void elemhozzadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elemhozzadoActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        float adat = 0;
        float alsoxy, alsoyz, felsoxy, felsoyz;
        if (!racs.nev.toString().equals("")) {
            racs.szekcioszam++;
            racs.adatok[racs.szekcioszam][0] = racs.szekcioszam;
            racs.adatok[racs.szekcioszam][1] = Integer.parseInt(Magassag.getText());
            racs.adatok[racs.szekcioszam][2] = Integer.parseInt(alsoszelxy.getText());
            racs.adatok[racs.szekcioszam][3] = Integer.parseInt(alsoszelyz.getText());
            alsoxy = Float.parseFloat(alsoszelxy.getText());
            alsoyz = Float.parseFloat(alsoszelyz.getText());
            racs.adatok[racs.szekcioszam][4] = Integer.parseInt(felsoszelxy.getText());
            racs.adatok[racs.szekcioszam][5] = Integer.parseInt(felsoszelyz.getText());
            racs.adatok[racs.szekcioszam][6] = Integer.parseInt(kapcsx.getText());
            racs.adatok[racs.szekcioszam][7] = Integer.parseInt(kapcsy.getText());
            racs.adatok[racs.szekcioszam][8] = Integer.parseInt(kapcsz.getText());
            racs.adatok[racs.szekcioszam][9] = Integer.parseInt(eltolasxy.getText());
            racs.adatok[racs.szekcioszam][10] = Integer.parseInt(eltolasyz.getText());
            racs.adatok[racs.szekcioszam][11] = Integer.parseInt(konzolhossz.getText());
            racs.adatok[racs.szekcioszam][12] = 1;
            if (irany.isSelected()) {
                racs.adatok[racs.szekcioszam][12] = 2;
            }
            // A Jtable-be való beleírás
            int j = tableModel.getRowCount();
            if (j > 0) {
                for (int k = 0; k < j; k++) {
                    tableModel.removeRow(0);
                }
            }
            for (int i = 1; i <= racs.szekcioszam; i++) {
                String[] data = new String[14];
                for (int k = 0; k < 12; k++) {
                    data[k] = String.valueOf(racs.adatok[i][k]);
                }
                tableModel.addRow(data);
                if (racs.adatok[i][12] == 1) {
                    tableModel.setValueAt(true, i - 1, 12);
                } else {
                    tableModel.setValueAt(false, i - 1, 12);
                }
                if (racs.adatok[i][13] == 1) {
                    tableModel.setValueAt(true, i - 1, 13);
                } else {
                    tableModel.setValueAt(false, i - 1, 13);
                }
                if (racs.adatok[i][14] == 1) {
                    tableModel.setValueAt(true, i - 1, 14);
                } else {
                    tableModel.setValueAt(false, i - 1, 14);
                }
            }
            // Az összes oszlop középre igazítása
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            for (int k = 0; k < 12; k++) {
                szekcioadatok.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
            }
            // A tábla oszlopszélességei
            szekcioadatok.setAutoResizeMode(szekcioadatok.AUTO_RESIZE_OFF);
            szekcioadatok.getColumnModel().getColumn(0).setPreferredWidth(70);
            szekcioadatok.getColumnModel().getColumn(1).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(2).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(3).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(4).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(5).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(6).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(7).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(8).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(9).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(10).setPreferredWidth(75);
            szekcioadatok.getColumnModel().getColumn(11).setPreferredWidth(75);
            // A logikai kapcsolók
            szekcioadatok.getColumnModel().getColumn(12).setPreferredWidth(70);
            szekcioadatok.getColumnModel().getColumn(13).setPreferredWidth(70);
            szekcioadatok.setModel(tableModel);
            szekcioadatok.setShowGrid(true);

            // A szekciókon belüli rácselemek
            if (elemszamok.getText().length() > 0) {
                for (int i = 1; i <= Integer.parseInt(elemszamok.getText()); i++) {
                    racs.kozszam++;
                    // A köz sorszáma
                    racs.adatok1[racs.kozszam][11] = i;
                    racs.adatok1[racs.kozszam][0] = racs.szekcioszam;
                    racs.adatok1[racs.kozszam][2] = Integer.parseInt(String.valueOf(alsoxy));
                    racs.adatok1[racs.kozszam][3] = Integer.parseInt(String.valueOf(alsoyz));
                    adat = (Integer.parseInt(alsoszelxy.getText()) - Integer.parseInt(felsoszelxy.getText())) / Integer.parseInt(elemszamok.getText());
                    felsoxy = Integer.parseInt(alsoszelxy.getText()) - i * adat;
                    racs.adatok1[racs.kozszam][4] = Integer.parseInt(String.valueOf(felsoxy));
                    adat = (Integer.parseInt(alsoszelyz.getText()) - Integer.parseInt(felsoszelyz.getText())) / Integer.parseInt(elemszamok.getText());
                    felsoyz = Integer.parseInt(alsoszelyz.getText()) - i * adat;
                    racs.adatok1[racs.kozszam][5] = Integer.parseInt(String.valueOf(felsoyz));
                    alsoxy = felsoxy;
                    alsoyz = felsoyz;
                    // eltolasxy
                    adat = Float.parseFloat(eltolasxy.getText()) / Float.parseFloat(elemszamok.getText());
                    racs.adatok1[racs.kozszam][9] = Integer.parseInt(String.valueOf(adat));
                    //  eltolasyz
                    adat = Float.parseFloat(eltolasyz.getText()) / Float.parseFloat(elemszamok.getText());
                    racs.adatok1[racs.kozszam][10] = Integer.parseInt(String.valueOf(adat));
                    racs.adatok1[racs.kozszam][12] = 1;
                    racs.adatok1[racs.kozszam][13] = 1;
                    if (!irany.isSelected()) {
                        // A függőleges szakaszok
                        // x            
                        adat = (Integer.parseInt(eltolasxy.getText()) / Integer.parseInt(elemszamok.getText())) * 2;
                        //System.out.println("adat:"+adat);
                        adat += ((Integer.parseInt(alsoszelxy.getText()) - Integer.parseInt(felsoszelxy.getText())) / Integer.parseInt(elemszamok.getText())) / 2;
                        //System.out.println("adat:"+adat);
                        adat = Integer.parseInt(kapcsx.getText()) + (i - 1) * adat;
                        racs.adatok1[racs.kozszam][6] = Integer.parseInt(String.valueOf(adat));
                        // y 
                        adat = Integer.parseInt(Magassag.getText()) / Integer.parseInt(elemszamok.getText());
                        adat = Integer.parseInt(kapcsy.getText()) + (i - 1) * adat;
                        racs.adatok1[racs.kozszam][7] = Integer.parseInt(String.valueOf(adat));
                        // z 
                        adat = (Integer.parseInt(eltolasyz.getText()) / Integer.parseInt(elemszamok.getText())) * 2;
                        adat += ((Integer.parseInt(alsoszelyz.getText()) - Integer.parseInt(felsoszelyz.getText())) / Integer.parseInt(elemszamok.getText())) / 2;
                        adat = Integer.parseInt(kapcsz.getText()) + (i - 1) * adat;
                        racs.adatok1[racs.kozszam][8] = Integer.parseInt(String.valueOf(adat));
                    } else {
                        // A vízszintes szakaszok
                        // x 
                        adat = Integer.parseInt(Magassag.getText()) / Integer.parseInt(elemszamok.getText());
                        adat = Integer.parseInt(kapcsx.getText()) + (i - 1) * adat;
                        racs.adatok1[racs.kozszam][6] = Integer.parseInt(String.valueOf(adat));
                        // y                                                        
                        adat = (Integer.parseInt(eltolasxy.getText()) / Integer.parseInt(elemszamok.getText())) * 2;
                        adat += ((Integer.parseInt(alsoszelxy.getText()) - Integer.parseInt(felsoszelxy.getText())) / Integer.parseInt(elemszamok.getText())) / 2;
                        adat = Integer.parseInt(kapcsy.getText()) + (i - 1) * adat;
                        racs.adatok1[racs.kozszam][7] = Integer.parseInt(String.valueOf(adat));
                        // z                             
                        adat = (Integer.parseInt(eltolasyz.getText()) / Integer.parseInt(elemszamok.getText())) * 2;
                        adat += ((Integer.parseInt(alsoszelyz.getText()) - Integer.parseInt(felsoszelyz.getText())) / Integer.parseInt(elemszamok.getText())) / 2;
                        racs.adatok1[racs.kozszam][8] = Integer.parseInt(String.valueOf(adat));
                    }
                    // A köz magassága
                    adat = Integer.parseInt(Magassag.getText()) / Integer.parseInt(elemszamok.getText());
                    racs.adatok1[racs.kozszam][1] = Integer.parseInt(String.valueOf(adat));
                }
            }
            // A szekciok lista feltöltése
            szekciok.removeAll();
            szekciok.addItem("Válassz");
            for (int i = 1; i <= racs.szekcioszam; i++) {
                szekciok.addItem(racs.nev + " - szekció: " + String.valueOf(i));
            }
        }
        mentes.setEnabled(true);
    }//GEN-LAST:event_elemhozzadoActionPerformed

    private void szelvenysulyok_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) szelvenysulyok.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void csomopontlista_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void rudlista_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void szelvenysulyok_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) szelvenysulyok.getModel();
        szelvenysulyok_tablatorlo();
        String[] data = new String[4];
        for (int j = 1; j <= 8; j++) {
            data[0] = String.valueOf(j);
            data[1] = String.valueOf(racs.rudnevek[szekciok.getSelectedIndex()][j]);
            if (racs.szelvenyrudhossz[szekciok.getSelectedIndex()][j] == 0) {
                data[2] = "";
                data[3] = "";
            } else {
                data[2] = String.valueOf(racs.szelvenyrudhossz[szekciok.getSelectedIndex()][j]);
                data[3] = String.format("%.2f", ((racs.rudsuly[szekciok.getSelectedIndex()][j]
                        * racs.szelvenyrudhossz[szekciok.getSelectedIndex()][j]) / 1000));
                //System.out.println("i:"+szekciok.getSelectedIndex()+" j:"+j+" suly:"+racs.rudsuly[szekciok.getSelectedIndex()][j]);
            }
            tableModel.addRow(data);
        }
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
        szelvenysulyok.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        szelvenysulyok.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        szelvenysulyok.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        szelvenysulyok.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        // A tábla oszlopszélességei
        szelvenysulyok.setAutoResizeMode(szelvenysulyok.AUTO_RESIZE_OFF);
        szelvenysulyok.getColumnModel().getColumn(0).setPreferredWidth(60);
        szelvenysulyok.getColumnModel().getColumn(1).setPreferredWidth(140);
        szelvenysulyok.getColumnModel().getColumn(2).setPreferredWidth(80);
        szelvenysulyok.getColumnModel().getColumn(3).setPreferredWidth(70);
        szelvenysulyok.setModel(tableModel);
        szelvenysulyok.setShowGrid(true);
    }

    private void csomopontlista_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        csomopontlista_tablatorlo();
        String[] data = new String[5];
        int k = 1;
        for (int i = 1; i <= racs.csomopontindex; i++) {
            if (racs.csomopont[i][0] == szekciok.getSelectedIndex()) {
                data[0] = String.valueOf(k++);
                data[1] = String.valueOf(i);
                data[2] = String.valueOf(racs.csomopont[i][1]);  // x
                data[3] = String.valueOf(racs.csomopont[i][2]);  // y
                data[4] = String.valueOf(racs.csomopont[i][3]);  // z
                tableModel.addRow(data);                
                if (racs.csomopont[i][4] == 1) {
                    tableModel.setValueAt(true, k - 2, 5);
                } else {
                    tableModel.setValueAt(false, k - 2, 5);
                }
            }
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
        csomopontlista.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        csomopontlista.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        csomopontlista.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        csomopontlista.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        csomopontlista.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        // A tábla oszlopszélességei
        csomopontlista.setAutoResizeMode(csomopontlista.AUTO_RESIZE_OFF);
        csomopontlista.getColumnModel().getColumn(0).setPreferredWidth(40);
        csomopontlista.getColumnModel().getColumn(1).setPreferredWidth(40);
        csomopontlista.getColumnModel().getColumn(2).setPreferredWidth(60);
        csomopontlista.getColumnModel().getColumn(3).setPreferredWidth(60);
        csomopontlista.getColumnModel().getColumn(4).setPreferredWidth(60);
        csomopontlista.setModel(tableModel);
        csomopontlista.setShowGrid(true);
    }

    private void rudlista_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        int k = 1;
        rudlista_tablatorlo();
        String[] data = new String[8];
        for (int i = 1; i <= racs.rudindex; i++) {
            if (racs.rud[i][0] == szekciok.getSelectedIndex()) {
                data[0] = String.valueOf(k++);
                data[1] = String.valueOf(i);
                data[2] = String.valueOf(racs.rud[i][1]);  // kezdcsp
                data[3] = String.valueOf(racs.rud[i][2]);  // vegecsp
                data[4] = String.valueOf(racs.rudnevek[szekciok.getSelectedIndex()][racs.rud[i][6]]);  // szelvény
                data[5] = String.valueOf(racs.rud[i][7]);  // hossz
                data[6] = String.format("%.2f", racs.rudsuly[szekciok.getSelectedIndex()][racs.rud[i][6]]);  // súly
                tableModel.addRow(data);
                if (racs.rud[i][4] == 1) {
                    tableModel.setValueAt(true, k - 2, 7);
                } else {
                    tableModel.setValueAt(false, k - 2, 7);
                }
            }
        }
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
        rudlista.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        rudlista.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        // A tábla oszlopszélességei
        rudlista.setAutoResizeMode(rudlista.AUTO_RESIZE_OFF);
        rudlista.getColumnModel().getColumn(0).setPreferredWidth(30);
        rudlista.getColumnModel().getColumn(1).setPreferredWidth(30);
        rudlista.getColumnModel().getColumn(2).setPreferredWidth(35);
        rudlista.getColumnModel().getColumn(3).setPreferredWidth(35);
        rudlista.getColumnModel().getColumn(4).setPreferredWidth(80);
        rudlista.getColumnModel().getColumn(5).setPreferredWidth(50);
        rudlista.getColumnModel().getColumn(6).setPreferredWidth(50);
        rudlista.setModel(tableModel);
        rudlista.setShowGrid(true);
    }

    private void szekcio_kivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_szekcio_kivalasztoActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        float szekciosuly = 0, totalsuly = 0;
        String szoveg;
        /*for (int i = 1; i <= racs.rudindex; i++) {
         System.out.println("Rud:"+i+" Rud[4]:"+racs.rud[i][4]+"  Rud[5]:"+racs.rud[i][5]);
         }
         System.out.println();*/
        for (int i = 1; i <= racs.szekcioszam; i++) {
            for (int j = 1; j <= 8; j++) {
                totalsuly += (racs.szelvenyrudhossz[i][j] * racs.rudsuly[i][j]) / 1000;
                if (i == szekciok.getSelectedIndex()) {
                    szekciosuly += (racs.szelvenyrudhossz[i][j] * racs.rudsuly[i][j]) / 1000;
                }
                //System.out.println("i:"+i+" j:"+j+"  nev:'"+racs.rudnevek[i][j]+"'  suly:"+racs.rudsuly[i][j]);
            }
        }
        for (int i = 1; i <= racs.rudindex; i++) {
            racs.rud[i][4] = 0;
        }
        for (int i = 1; i <= racs.csomopontindex; i++) {
            racs.csomopont[i][4] = 0;
        }
        teljes_suly.setText(String.format("%.0f", totalsuly));
        szekcio_suly.setText(String.format("%.0f", szekciosuly));
        racs.kozbeolvaso(szekciok.getSelectedIndex(), 1);
        szelvenysulyok_tablatolto();
        csomopontlista_tablatolto();
        rudlista_tablatolto();
        /*System.out.println("1:");
         for (int i = 1; i <= racs.rudindex; i++) {
         System.out.println("Rud:"+i+" Rud[4]:"+racs.rud[i][4]+"  Rud[5]:"+racs.rud[i][5]);
         }*/
        kozok.removeAllItems();
        // Az aktuális szelvénynév kijelölése

        racsrudnev1.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][1]);
        racsrudnev2.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][2]);
        racsrudnev3.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][3]);
        racsrudnev4.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][4]);
        racsrudnev5.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][5]);
        racsrudnev6.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][6]);
        racsrudnev7.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][7]);
        racsrudnev8.setSelectedItem(racs.rudnevek[szekciok.getSelectedIndex()][8]);
        //rajzolás
        racselemek_kijelzo();
        racskozok_torlo();
        rudnevek_kijelzo();
        // A racs köz-méreteinek beolvasása

        if (szekciok.getSelectedIndex() > 0) {
            for (int j = 1; j <= racs.szekcioszam; j++) {
                if (racs.adatok[j][0] == szekciok.getSelectedIndex()) {
                    szekciohossz.setText(String.valueOf(racs.adatok1[j][1]));
                }
            }
            // A közök legördülő feltöltése
            String[] data = new String[2];
            for (int j = 1; j <= racs.kozszam; j++) {
                if (racs.adatok1[j][0] == szekciok.getSelectedIndex()) {
                    szoveg = "Köz : " + String.valueOf(racs.adatok1[j][11]);
                    kozok.addItem(szoveg);
                    data[0] = String.valueOf(racs.adatok1[j][11]);
                    data[1] = String.valueOf(racs.adatok1[j][1]);
                    tableModel.addRow(data);
                }
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            racskozok.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            racskozok.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            // A tábla oszlopszélességei
            racskozok.setAutoResizeMode(racskozok.AUTO_RESIZE_OFF);
            racskozok.getColumnModel().getColumn(0).setPreferredWidth(70);
            racskozok.getColumnModel().getColumn(1).setPreferredWidth(150);
            racskozok.setModel(tableModel);
            racskozok.setShowGrid(true);
        }
        teljes_kepkitevo();
        szekcio_kepkitevo();
        racs.mintamasolo(1, 1);
    }//GEN-LAST:event_szekcio_kivalasztoActionPerformed

    private void ElemmodositoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ElemmodositoActionPerformed
        // TODO add your handling code here:

        mentes.setEnabled(true);
    }//GEN-LAST:event_ElemmodositoActionPerformed

    private void teljes_nezetMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teljes_nezetMouseDragged
        if (SwingUtilities.isLeftMouseButton(evt)) {
            // A forgatás
            if ((racs.mx1 == 0) || (racs.my1 == 0)) {
                racs.mx1 = evt.getX();
                racs.my1 = evt.getY();
            } else {
                if (evt.getX() > racs.mx1) {
                    racs.forgatas[0][2]++;
                    if (racs.forgatas[0][2] > 360) {
                        racs.forgatas[0][2] = 0;
                    }
                    racs.mx1 = evt.getX();
                }
                if (evt.getX() < racs.mx1) {
                    racs.forgatas[0][2]--;
                    if (racs.forgatas[0][2] < 0) {
                        racs.forgatas[0][2] = 360;
                    }
                    racs.mx1 = evt.getX();
                }
                if (evt.getY() > racs.my1) {
                    racs.forgatas[0][3]--;
                    if (racs.forgatas[0][3] < 0) {
                        racs.forgatas[0][3] = 360;
                    }
                    racs.my1 = evt.getY();
                }
                if (evt.getY() < racs.my1) {
                    racs.forgatas[0][3]++;
                    if (racs.forgatas[0][3] > 360) {
                        racs.forgatas[0][3] = 0;
                    }
                    racs.my1 = evt.getY();
                }
                teljes_kepkitevo();
            }
        }
        if (SwingUtilities.isRightMouseButton(evt)) {
            //mozgatás                
            if ((racs.tx1 == 0) || (racs.ty1 == 0)) {
                racs.tx1 = evt.getX();
                racs.ty1 = evt.getY();
            } else {
                if (evt.getX() > racs.tx1) {
                    racs.kepkozep[0][0]++;
                    racs.tx1 = evt.getX();
                }
                if (evt.getX() < racs.tx1) {
                    racs.kepkozep[0][0]--;
                    racs.tx1 = evt.getX();
                }
                if (evt.getY() > racs.ty1) {
                    racs.kepkozep[0][1]++;
                    racs.ty1 = evt.getY();
                }
                if (evt.getY() < racs.ty1) {
                    racs.kepkozep[0][1]--;
                    racs.ty1 = evt.getY();
                }
                teljes_kepkitevo();
            }
        }
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            // Alaphelyzet visszaállítás
            racs.kepkozep[0][0] = 0;
            racs.kepkozep[0][1] = 0;
            racs.forgatas[0][2] = 0;
            racs.forgatas[0][3] = 0;
            racs.kepnagyitas[0] = 1;
            teljes_kepkitevo();
        }
    }//GEN-LAST:event_teljes_nezetMouseDragged

    private void szekcio_nezetMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_szekcio_nezetMouseDragged
        if (SwingUtilities.isLeftMouseButton(evt)) {
            // A forgatás
            if ((racs.mx0 == 0) || (racs.my0 == 0)) {
                racs.mx0 = evt.getX();
                racs.my0 = evt.getY();
            } else {
                if (evt.getX() > racs.mx0) {
                    racs.forgatas[1][2]++;
                    if (racs.forgatas[1][2] > 360) {
                        racs.forgatas[1][2] = 0;
                    }
                    racs.mx0 = evt.getX();
                }
                if (evt.getX() < racs.mx0) {
                    racs.forgatas[1][2]--;
                    if (racs.forgatas[1][2] < 0) {
                        racs.forgatas[1][2] = 360;
                    }
                    racs.mx0 = evt.getX();
                }
                if (evt.getY() > racs.my0) {
                    racs.forgatas[1][3]--;
                    if (racs.forgatas[1][3] < 0) {
                        racs.forgatas[1][3] = 360;
                    }
                    racs.my0 = evt.getY();
                }
                if (evt.getY() < racs.my0) {
                    racs.forgatas[1][3]++;
                    if (racs.forgatas[1][3] > 360) {
                        racs.forgatas[1][3] = 0;
                    }
                    racs.my0 = evt.getY();
                }
                szekcio_kepkitevo();
            }
        }
        if (SwingUtilities.isRightMouseButton(evt)) {
            //mozgatás                    
            if ((racs.tx0 == 0) || (racs.ty0 == 0)) {
                racs.tx0 = evt.getX();
                racs.ty0 = evt.getY();
            } else {
                if (evt.getX() > racs.tx0) {
                    racs.kepkozep[1][0]++;
                    racs.tx0 = evt.getX();
                }
                if (evt.getX() < racs.tx0) {
                    racs.kepkozep[1][0]--;
                    racs.tx0 = evt.getX();
                }
                if (evt.getY() > racs.ty0) {
                    racs.kepkozep[1][1]++;
                    racs.ty0 = evt.getY();
                }
                if (evt.getY() < racs.ty0) {
                    racs.kepkozep[1][1]--;
                    racs.ty0 = evt.getY();
                }
                szekcio_kepkitevo();
            }
        }
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            // Alaphelyzet visszaállítás
            racs.kepkozep[1][0] = 0;
            racs.kepkozep[1][1] = 0;
            racs.forgatas[1][2] = 0;
            racs.forgatas[1][3] = 0;
            racs.kepnagyitas[1] = 1;
            szekcio_kepkitevo();
        }
    }//GEN-LAST:event_szekcio_nezetMouseDragged

    private void racstipus_valtoztato(int tipus, int ertek) {
        // Az aktuális köz rácstipusának megváltoztatása
        for (int i = 1; i <= racs.kozszam; i++) {
            if ((racs.adatok1[i][0] == szekciok.getSelectedIndex())
                    && ((racs.adatok1[i][11] == kozok.getSelectedIndex() + 1))) {
                racs.adatok1[i][11 + tipus] = ertek;
            }
        }
        racs.racselemek();
        teljes_kepkitevo();
        szekcio_kepkitevo();
        mentes.setEnabled(true);
    }

    private void ftipus1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus1StateChanged
        racstipus_valtoztato(1, ftipus1.getValue());
        mentes.setEnabled(true);
        ftipus1text.setText(String.valueOf(ftipus1.getValue()));
    }//GEN-LAST:event_ftipus1StateChanged

    private void ftipus2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus2StateChanged
        //racsrudnev2.setEnabled(true);
        //if (ftipus2.getValue() == 0) {racsrudnev2.setEnabled(false);}
        racstipus_valtoztato(2, ftipus2.getValue());
        ftipus2text.setText(String.valueOf(ftipus2.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus2StateChanged

    private void ftipus3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus3StateChanged
        //racsrudnev3.setEnabled(true);
        //if (ftipus3.getValue() == 0) { racsrudnev3.setEnabled(false); }
        racstipus_valtoztato(3, ftipus3.getValue());
        ftipus3text.setText(String.valueOf(ftipus3.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus3StateChanged

    private void ftipus4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus4StateChanged
        //racsrudnev4.setEnabled(true);
        //if (ftipus4.getValue() == 0) {racsrudnev4.setEnabled(false);}
        racstipus_valtoztato(4, ftipus4.getValue());
        ftipus4text.setText(String.valueOf(ftipus4.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus4StateChanged

    private void ftipus5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus5StateChanged
        //racsrudnev5.setEnabled(true);
        //if (ftipus5.getValue() == 0) {racsrudnev5.setEnabled(false);}
        racstipus_valtoztato(5, ftipus5.getValue());
        ftipus5text.setText(String.valueOf(ftipus5.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus5StateChanged

    private void ftipus6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus6StateChanged
        //racsrudnev6.setEnabled(true);
        //if (ftipus6.getValue() == 0) { racsrudnev6.setEnabled(false);}
        racstipus_valtoztato(6, ftipus6.getValue());
        ftipus6text.setText(String.valueOf(ftipus6.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus6StateChanged

    private void ftipus7StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus7StateChanged
        //racsrudnev7.setEnabled(true);
        //if (ftipus7.getValue() == 0) { racsrudnev7.setEnabled(false); }
        racstipus_valtoztato(7, ftipus7.getValue());
        ftipus7text.setText(String.valueOf(ftipus7.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus7StateChanged

    private void ftipus8StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus8StateChanged
        //racsrudnev8.setEnabled(true);
        //if (ftipus8.getValue() == 0) { racsrudnev8.setEnabled(false);}
        racstipus_valtoztato(8, ftipus8.getValue());
        ftipus8text.setText(String.valueOf(ftipus8.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus8StateChanged

    private void vtipus1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus1StateChanged
        racstipus_valtoztato(1, vtipus1.getValue());
        vtipus1text.setText(String.valueOf(vtipus1.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus1StateChanged

    private void vtipus2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus2StateChanged
        racstipus_valtoztato(2, vtipus2.getValue());
        vtipus2text.setText(String.valueOf(vtipus2.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus2StateChanged

    private void vtipus3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus3StateChanged
        racstipus_valtoztato(3, vtipus3.getValue());
        vtipus3text.setText(String.valueOf(vtipus3.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus3StateChanged

    private void vtipus4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus4StateChanged
        racstipus_valtoztato(4, vtipus4.getValue());
        vtipus4text.setText(String.valueOf(vtipus4.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus4StateChanged

    private void vtipus5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus5StateChanged
        racstipus_valtoztato(5, vtipus5.getValue());
        vtipus5text.setText(String.valueOf(vtipus5.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus5StateChanged

    private void vtipus6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vtipus6StateChanged
        racstipus_valtoztato(6, vtipus6.getValue());
        vtipus6text.setText(String.valueOf(vtipus6.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_vtipus6StateChanged

    private void vastagvonalakStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vastagvonalakStateChanged
        szekcio_kepkitevo();
        teljes_kepkitevo();
    }//GEN-LAST:event_vastagvonalakStateChanged

    private void kozkivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kozkivalasztoActionPerformed
        racs.kozbeolvaso(szekciok.getSelectedIndex(), kozok.getSelectedIndex() + 1);
        szekcio_kepkitevo();
        //racselemek_kijelzo();
    }//GEN-LAST:event_kozkivalasztoActionPerformed

    private void Szelveny_hozzarendeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Szelveny_hozzarendeloActionPerformed
        for (int j = 1; j <= racs.szekcioszam; j++) {
            if (j == szekciok.getSelectedIndex()) {
                if ((racsrudnev1.isEnabled()) && (racsrudnev1.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][1] = racsrudnev1.getSelectedItem().toString();
                }
                if ((racsrudnev2.isEnabled()) && (racsrudnev2.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][2] = racsrudnev2.getSelectedItem().toString();
                }
                if ((racsrudnev3.isEnabled()) && (racsrudnev3.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][3] = racsrudnev3.getSelectedItem().toString();
                }
                if ((racsrudnev4.isEnabled()) && (racsrudnev4.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][4] = racsrudnev4.getSelectedItem().toString();
                }
                if ((racsrudnev5.isEnabled()) && (racsrudnev5.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][5] = racsrudnev5.getSelectedItem().toString();
                }
                if ((racsrudnev6.isEnabled()) && (racsrudnev6.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][6] = racsrudnev6.getSelectedItem().toString();
                }
                if ((racsrudnev7.isEnabled()) && (racsrudnev7.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][7] = racsrudnev7.getSelectedItem().toString();
                }
                if ((racsrudnev8.isEnabled()) && (racsrudnev8.getSelectedIndex() > 0)) {
                    racs.rudnevek[j][8] = racsrudnev8.getSelectedItem().toString();
                }
            }
        }
        racs.racsrudvastagsag();
        teljes_kepkitevo();
        szekcio_kepkitevo();
        /*megjegyzes.setText("Beírtam.");
         try {
         Thread.sleep(5000L);
         } catch (InterruptedException ex) {
         Logger.getLogger(racstervezo.class.getName()).log(Level.SEVERE, null, ex);
         }
         megjegyzes.setText(""); */
        mentes.setEnabled(true);
    }//GEN-LAST:event_Szelveny_hozzarendeloActionPerformed

    private void racskoz_valtoztatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_racskoz_valtoztatoActionPerformed
        float magassag = 0, alsoxy = 0, alsoyz = 0, felsoxy = 0, felsoyz = 0, diffxy = 0, diffyz = 0;
        float kezdx = 0, kezdy = 0, kezdz = 0, eltolasxy = 0, eltolasyz = 0, eltxy = 0, eltyz = 0;
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        int j = tableModel.getRowCount();
        for (int i = 1; i <= racs.szekcioszam; i++) {
            if (racs.adatok[i][0] == szekciok.getSelectedIndex()) {
                magassag = racs.adatok[i][1];
                alsoxy = racs.adatok[i][2];
                alsoyz = racs.adatok[i][3];
                felsoxy = racs.adatok[i][4];
                felsoyz = racs.adatok[i][5];
                kezdx = racs.adatok[i][6];
                kezdy = racs.adatok[i][7];
                kezdz = racs.adatok[i][8];
                eltolasxy = racs.adatok[i][9];
                eltolasyz = racs.adatok[i][10];
            }
        }
        diffxy = (alsoxy - felsoxy) / magassag;
        diffyz = (alsoyz - felsoyz) / magassag;
        eltxy = eltolasxy / magassag;
        eltyz = eltolasyz / magassag;
        for (int i = 0; i < j; i++) {
            felsoxy = alsoxy - (Float.parseFloat(tableModel.getValueAt(i, 1).toString())) * diffxy;
            felsoyz = alsoyz - (Float.parseFloat(tableModel.getValueAt(i, 1).toString())) * diffyz;
            for (int k = 1; k <= racs.kozszam; k++) {
                if ((racs.adatok1[i][0] == szekciok.getSelectedIndex()) && ((racs.adatok1[i][11] == (i + 1)))) {
                    //itt kell folytatni
                    racs.adatok1[i][1] = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
                    racs.adatok1[i][2] = Integer.parseInt(String.valueOf(alsoxy));
                    racs.adatok1[i][3] = Integer.parseInt(String.valueOf(alsoyz));
                    racs.adatok1[i][4] = Integer.parseInt(String.valueOf(felsoxy));
                    racs.adatok1[i][5] = Integer.parseInt(String.valueOf(felsoyz));
                    racs.adatok1[i][6] = Integer.parseInt(String.valueOf(kezdx));
                    racs.adatok1[i][7] = Integer.parseInt(String.valueOf(kezdy));
                    racs.adatok1[i][8] = Integer.parseInt(String.valueOf(kezdz));
                    racs.adatok1[i][9] = Integer.parseInt(String.valueOf(eltolasxy));
                    racs.adatok1[i][10] = Integer.parseInt(String.valueOf(eltolasyz));
                }
            }
            alsoxy = felsoxy;
            alsoyz = felsoyz;
            eltolasxy += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * eltxy;
            eltolasyz += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * eltyz;
            kezdx += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * (diffxy / 2);
            //System.out.println(diffxy);
            kezdz += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * (diffyz / 2);
            kezdy += Float.parseFloat(tableModel.getValueAt(i, 1).toString());
        }
        // Adatbeolvasás?
        racs.adatbeolvaso();
        szekcio_keptorlo();
        teljes_kepkitevo();
        //szekcio_kepkitevo();
        mentes.setEnabled(true);
    }//GEN-LAST:event_racskoz_valtoztatoActionPerformed

    private void teljes_nezetMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_teljes_nezetMouseWheelMoved
        if (evt.getWheelRotation() < 0) {
            racs.kepnagyitas[0] /= 0.5;
        } else {
            racs.kepnagyitas[0] *= 0.5;
        }
        teljes_kepkitevo();
    }//GEN-LAST:event_teljes_nezetMouseWheelMoved

    private void szekcio_nezetMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_szekcio_nezetMouseWheelMoved
        if (evt.getWheelRotation() < 0) {
            racs.kepnagyitas[1] /= 0.5;
        } else {
            racs.kepnagyitas[1] *= 0.5;
        }
        szekcio_kepkitevo();
    }//GEN-LAST:event_szekcio_nezetMouseWheelMoved

    private void ftipus2textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus2textActionPerformed
        ftipus2.setValue(Integer.parseInt(ftipus2text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(2, Integer.parseInt(ftipus2text.getText()));
    }//GEN-LAST:event_ftipus2textActionPerformed

    private void ftipus1textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus1textActionPerformed
        ftipus1.setValue(Integer.parseInt(ftipus1text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(1, Integer.parseInt(ftipus1text.getText()));
    }//GEN-LAST:event_ftipus1textActionPerformed

    private void ftipus3textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus3textActionPerformed
        ftipus3.setValue(Integer.parseInt(ftipus3text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(3, Integer.parseInt(ftipus3text.getText()));
    }//GEN-LAST:event_ftipus3textActionPerformed

    private void ftipus4textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus4textActionPerformed
        ftipus4.setValue(Integer.parseInt(ftipus4text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(4, Integer.parseInt(ftipus4text.getText()));
    }//GEN-LAST:event_ftipus4textActionPerformed

    private void ftipus5textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus5textActionPerformed
        ftipus5.setValue(Integer.parseInt(ftipus5text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(5, Integer.parseInt(ftipus5text.getText()));
    }//GEN-LAST:event_ftipus5textActionPerformed

    private void ftipus6textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus6textActionPerformed
        ftipus6.setValue(Integer.parseInt(ftipus6text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(6, Integer.parseInt(ftipus6text.getText()));
    }//GEN-LAST:event_ftipus6textActionPerformed

    private void ftipus7textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus7textActionPerformed
        ftipus7.setValue(Integer.parseInt(ftipus7text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(7, Integer.parseInt(ftipus7text.getText()));
    }//GEN-LAST:event_ftipus7textActionPerformed

    private void ftipus8textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus8textActionPerformed
        ftipus8.setValue(Integer.parseInt(ftipus8text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(8, Integer.parseInt(ftipus8text.getText()));
    }//GEN-LAST:event_ftipus8textActionPerformed

    private void vtipus1textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus1textActionPerformed
        vtipus1.setValue(Integer.parseInt(vtipus1text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(1, Integer.parseInt(vtipus1text.getText()));
    }//GEN-LAST:event_vtipus1textActionPerformed

    private void vtipus2textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus2textActionPerformed
        vtipus2.setValue(Integer.parseInt(vtipus2text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(2, Integer.parseInt(vtipus2text.getText()));
    }//GEN-LAST:event_vtipus2textActionPerformed

    private void vtipus3textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus3textActionPerformed
        vtipus3.setValue(Integer.parseInt(vtipus3text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(3, Integer.parseInt(vtipus3text.getText()));
    }//GEN-LAST:event_vtipus3textActionPerformed

    private void vtipus4textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus4textActionPerformed
        vtipus4.setValue(Integer.parseInt(vtipus4text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(4, Integer.parseInt(vtipus4text.getText()));
    }//GEN-LAST:event_vtipus4textActionPerformed

    private void vtipus5textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus5textActionPerformed
        vtipus5.setValue(Integer.parseInt(vtipus5text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(5, Integer.parseInt(vtipus5text.getText()));
    }//GEN-LAST:event_vtipus5textActionPerformed

    private void vtipus6textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vtipus6textActionPerformed
        vtipus6.setValue(Integer.parseInt(vtipus6text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(6, Integer.parseInt(vtipus6text.getText()));
    }//GEN-LAST:event_vtipus6textActionPerformed

    private void mentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mentesActionPerformed
        // TODO add your handling code here:        
        for (int i = 1; i <= racs.rudindex; i++) {
            racs.rud[i][7] = (int) (racs.rudhossz(racs.rud[i][1], racs.rud[i][2]));
            for (int j = 1; j <= racs.kozszam; j++) {
                if ((racs.adatok1[j][0] == racs.rud[i][0]) && (racs.adatok1[j][11] == racs.rud[i][5])) {
                    racs.adatok1[j][racs.rud[i][6] + 19] += racs.rud[i][7];
                }
            }
            //System.out.println("Kezd:"+racs.rud[i][1]+" vég:"+racs.rud[i][2]+"  hossz:"+racs.rud[i][7]+" Szekcio:"+racs.rud[i][0]+" tipus:"+racs.rud[i][6]+" koz:"+racs.rud[i][5]);
        }
        racs.adatrogzito();
        mentes.setEnabled(false);
        csomopontszam.setText(String.valueOf(racs.csomopontindex));
        rudszam.setText(String.valueOf(racs.rudindex));
    }//GEN-LAST:event_mentesActionPerformed

    private void rudlista_megjeloloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rudlista_megjeloloActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        for (int i = 0; i < racs.rudindex; i++) {
            if (tableModel.getValueAt(i, 7).toString().equals("true")) {
                racs.rud[i][4] = 1;
            } else {
                racs.rud[i][4] = 0;
            }
        }
        szekcio_kepkitevo();
    }//GEN-LAST:event_rudlista_megjeloloActionPerformed

    private void csomopontlista_megjeloloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csomopontlista_megjeloloActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        for (int i = 0; i < racs.csomopontindex; i++) {
            if (tableModel.getValueAt(i, 5).toString().equals("true")) {
                racs.csomopont[i][4] = 1;
            } else {
                racs.csomopont[i][4] = 0;
            }
        }
        szekcio_kepkitevo();
    }//GEN-LAST:event_csomopontlista_megjeloloActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Elemmodosito;
    private javax.swing.JTextField Magassag;
    private javax.swing.JButton Szelveny_hozzarendelo;
    private javax.swing.JTextField alsoszelxy;
    private javax.swing.JTextField alsoszelyz;
    private javax.swing.JTable csomopontlista;
    private javax.swing.JButton csomopontlista_megjelolo;
    private javax.swing.JTextField csomopontszam;
    private javax.swing.JButton drotvaz_kivalaszto;
    private javax.swing.JComboBox drotvazak;
    private javax.swing.JButton elemhozzado;
    private javax.swing.JTextField elemszamok;
    private javax.swing.JTextField eltolasxy;
    private javax.swing.JTextField eltolasyz;
    private javax.swing.JTextField felsoszelxy;
    private javax.swing.JTextField felsoszelyz;
    private javax.swing.JSlider ftipus1;
    private javax.swing.JTextField ftipus1text;
    private javax.swing.JSlider ftipus2;
    private javax.swing.JTextField ftipus2text;
    private javax.swing.JSlider ftipus3;
    private javax.swing.JTextField ftipus3text;
    private javax.swing.JSlider ftipus4;
    private javax.swing.JTextField ftipus4text;
    private javax.swing.JSlider ftipus5;
    private javax.swing.JTextField ftipus5text;
    private javax.swing.JSlider ftipus6;
    private javax.swing.JTextField ftipus6text;
    private javax.swing.JSlider ftipus7;
    private javax.swing.JTextField ftipus7text;
    private javax.swing.JSlider ftipus8;
    private javax.swing.JTextField ftipus8text;
    private javax.swing.JRadioButton irany;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField kapcsx;
    private javax.swing.JTextField kapcsy;
    private javax.swing.JTextField kapcsz;
    private javax.swing.JRadioButton keresni;
    private javax.swing.JTextField konzolhossz;
    private javax.swing.JButton kozkivalaszto;
    private javax.swing.JComboBox kozok;
    private javax.swing.JButton mentes;
    private javax.swing.JButton racskoz_valtoztato;
    private javax.swing.JTable racskozok;
    private javax.swing.JComboBox racsrudnev1;
    private javax.swing.JComboBox racsrudnev2;
    private javax.swing.JComboBox racsrudnev3;
    private javax.swing.JComboBox racsrudnev4;
    private javax.swing.JComboBox racsrudnev5;
    private javax.swing.JComboBox racsrudnev6;
    private javax.swing.JComboBox racsrudnev7;
    private javax.swing.JComboBox racsrudnev8;
    private javax.swing.JTable rudlista;
    private javax.swing.JButton rudlista_megjelolo;
    private javax.swing.JTextField rudszam;
    private javax.swing.JButton szekcio_kivalaszto;
    private javax.swing.JLabel szekcio_nezet;
    private javax.swing.JTextField szekcio_suly;
    private javax.swing.JTable szekcioadatok;
    private javax.swing.JTextField szekciohossz;
    private javax.swing.JComboBox szekciok;
    private javax.swing.JTable szelvenysulyok;
    private javax.swing.JLabel teljes_nezet;
    private javax.swing.JTextField teljes_suly;
    private javax.swing.JButton ujelem;
    private javax.swing.JTextField ujelemnev;
    private javax.swing.JRadioButton vastagvonalak;
    private javax.swing.JSlider vtipus1;
    private javax.swing.JTextField vtipus1text;
    private javax.swing.JSlider vtipus2;
    private javax.swing.JTextField vtipus2text;
    private javax.swing.JSlider vtipus3;
    private javax.swing.JTextField vtipus3text;
    private javax.swing.JSlider vtipus4;
    private javax.swing.JTextField vtipus4text;
    private javax.swing.JSlider vtipus5;
    private javax.swing.JTextField vtipus5text;
    private javax.swing.JSlider vtipus6;
    private javax.swing.JTextField vtipus6text;
    // End of variables declaration//GEN-END:variables
}
