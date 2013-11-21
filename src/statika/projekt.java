/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * projekt.java
 *
 * Created on 2012.12.27., 12:38:33
 */
package statika;

import java.sql.*;

/**
 *
 * @author SD-LEAP
 */
public class projekt extends javax.swing.JInternalFrame {

    static Connection co;
    static Statement st;
    static ResultSet rs;
    /** Creates new form projekt */
    String megnevezes = "";

    public projekt() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A projekt nevének a beolvasása
            rs = st.executeQuery("SELECT projekt FROM projectek where aktiv = '1'");
            while (rs.next()) {
                megnevezes = rs.getString(1);
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        //System.out.println("Az aktuális projekt: " + megnevezes);
        initComponents();
        projekt.setText(megnevezes);
        // A jComboBox1 feltöltése
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A projekt nevének a beolvasása
            rs = st.executeQuery("SELECT projekt FROM projectek ");
            while (rs.next()) {
                jComboBox1.addItem(rs.getString(1));
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        ujprojekt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        projekt = new javax.swing.JTextField();
        megrendelo = new javax.swing.JTextField();
        ujleiras = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Projekt-beállítás");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Új projekt létrehozása:");

        jButton1.setText("Létrehozás");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Meglévő projekt:");

        jButton2.setText("Kiválasztás");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("A beállított projekt:");

        projekt.setEditable(false);
        projekt.setText("Beállított projekt");
        projekt.setBorder(null);

        jLabel4.setText("Projektnév:");

        jLabel5.setText("Megrendelő:");

        jLabel6.setText("Projekt-leírás:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(4, 4, 4))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projekt, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 346, Short.MAX_VALUE))
                            .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, 456, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(megrendelo, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                            .addComponent(ujleiras, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ujprojekt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE))
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projekt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ujprojekt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(megrendelo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ujleiras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jButton2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String parancs;
        int UpdateQuery;
        if (ujprojekt.getText().length() > 0) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                // Az aktív projekt kikapcsolása
                parancs = "update projectek set aktiv = '0' where aktiv='1';";
                //rs = st.executeQuery(parancs);             
                UpdateQuery = st.executeUpdate(parancs);
                //System.out.println("SQL parancs: " + parancs);
                rs.close();
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                // Az új projekt lerögzítése
                parancs = "insert into projectek (projekt,megrendelo,leiras,aktiv) values ('";
                parancs = parancs + ujprojekt.getText() + "','";
                parancs = parancs + megrendelo.getText() + "','";
                parancs = parancs + ujleiras.getText() + "','1');";
                //System.out.println("SQL parancs: " + parancs);
                //rs = st.executeQuery(parancs);           
                UpdateQuery = st.executeUpdate(parancs);
                rs.close();
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
            projekt.setText(ujprojekt.getText());
            // A beírt adatok törlése
            ujprojekt.setText("");
            megrendelo.setText("");
            ujleiras.setText("");
            // A jComboBox1 újrafeltöltése            
            jComboBox1.removeAllItems();
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                // A projekt nevének a beolvasása
                rs = st.executeQuery("SELECT projekt FROM projectek ");
                while (rs.next()) {
                    jComboBox1.addItem(rs.getString(1));
                }
                rs.close();
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int UpdateQuery;
        String parancs;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // Az aktív projekt kikapcsolása
            parancs = "update projectek set aktiv = '0' where aktiv='1';";
            //rs = st.executeQuery(parancs);             
            UpdateQuery = st.executeUpdate(parancs);
            //System.out.println("SQL parancs: " + parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // Az aktív projekt kikapcsolása
            parancs = "update projectek set aktiv = '1' where projekt='";
            parancs = parancs + jComboBox1.getSelectedItem().toString() + "';";
            //rs = st.executeQuery(parancs);             
            UpdateQuery = st.executeUpdate(parancs);
            //System.out.println("SQL parancs: " + parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        projekt.setText(jComboBox1.getSelectedItem().toString());
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField megrendelo;
    private javax.swing.JTextField projekt;
    private javax.swing.JTextField ujleiras;
    private javax.swing.JTextField ujprojekt;
    // End of variables declaration//GEN-END:variables
}
