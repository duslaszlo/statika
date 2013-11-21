/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egyeb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 *
 * @author programt515
 */
// A másolás stringet ad vissza!
public class sormasolas {

    public static void main(String[] args) throws IOException {

        BufferedReader inputStream1 = null;
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        String filenev, filenev1;
        Integer csp ;
        try {
            inputStream1 = new BufferedReader(new FileReader("d:/1/obj/files.txt"));
            while ((filenev = inputStream1.readLine()) != null) {
                System.out.println(filenev);
                try {
                    filenev1 = "d:/1/obj/" + filenev + ".obj";
                    inputStream = new BufferedReader(new FileReader(filenev1));
                    filenev1 = "d:/1/obj/" + filenev + ".sql";
                    outputStream = new PrintWriter(new FileWriter(filenev1));
                    csp=0;
                    String l, l1, l2, kimenet, x, y, z;
                    while ((l = inputStream.readLine()) != null) {
                        if (!l.isEmpty()) {
                            l1 = "";
                            kimenet = "";
                            //System.out.println(l);
                            // Az elem vizsgálata                                                
                            if (l.substring(0, 1).equals("v")) {
                                csp++;
                                StringTokenizer st = new StringTokenizer(l, " ");
                                x = st.nextElement().toString();    // Az első betú bevitele...
                                x = st.nextElement().toString();
                                y = st.nextElement().toString();
                                z = st.nextElement().toString();
                                kimenet = "insert into csomopont (azonosito,csomopont,x,y,z) values ('" + filenev + "','"+csp+"','"
                                        + x + "','" + y + "','" + z + "');" + System.getProperty("line.separator");
                            }
                            if ((l.substring(0, 1).equals("f")) || (l.substring(0, 1).equals("l"))) {
                                StringTokenizer st = new StringTokenizer(l, " ");
                                l1 = st.nextElement().toString();  // Az első betú bevitele...
                                l1 = st.nextElement().toString();
                                while (st.hasMoreTokens()) {
                                    l2 = st.nextElement().toString();
                                    kimenet = kimenet + "insert into rud (azonosito,kezdocsp,vegecsp) values ('" + filenev
                                            + "','" + l1 + "','" + l2 + "');" + System.getProperty("line.separator");
                                    l1 = l2;
                                }
                            }
                            outputStream.print(kimenet);
                        }
                    }
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        } finally {
            if (inputStream1 != null) {
                inputStream1.close();
            }
        }
    }
}
