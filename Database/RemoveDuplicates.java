/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giorgos
 */
public class RemoveDuplicates {

    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //url dikias mou vashs
            //String connectionURL = "jdbc:mysql://127.0.0.1:3306/hackathon?useUnicode=yes&characterEncoding=UTF-8&user=root&password=password";
            //url aginaras 
            String connectionURL = "jdbc:mysql://83.212.110.120/Bizeli?useUnicode=yes&characterEncoding=UTF-8&user=penguin&password="+args[0];

            Connection connection = DriverManager.getConnection(connectionURL);
            Statement query = connection.createStatement();
            Statement del = connection.createStatement();
            Statement ins = connection.createStatement();
            ResultSet rs = null;
            //vres ta proionta pou emfanizontai parapanw apo duo f1 fora sto idio magazi
            rs = query.executeQuery("SELECT avg( price ) as price,pdate,market,name,region,id,barcode FROM `prices` GROUP BY market, id HAVING count( * ) >1");
            while (rs.next()) {
                //kane delete oles tis pleiades me diplotupa proionda
                String mar = rs.getString("market");
                int id = rs.getInt("id");
                del.execute("DELETE FROM prices where market like '%" + mar + "%' and id=" + id);
                //pare tis times gia to neo insert
                String date = rs.getString("pdate");
                String name = rs.getString("name");
                String reg = rs.getString("region");
                String bar = rs.getString("barcode");
                String price = rs.getString("price");
                System.out.println(name + " " + reg + " " + bar + " " + price + " " + mar + " " + id);
                //insert ton sundiasmo proion-market me to meso price
                ins.execute("INSERT INTO `prices`(`Pdate`, `Region`, `Market`, `ID`, `Barcode`, `Name`, `Price`) VALUES ('" + date + "','" + reg + "','" + mar + "'," + id + ",'" + bar + "','" + name + "'," + price + ")");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
