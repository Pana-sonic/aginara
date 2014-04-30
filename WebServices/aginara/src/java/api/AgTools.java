/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.ProcessBuilder.Redirect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giorgos
 */
public class AgTools {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, SQLException {
        try {
            //String products="595:ΨΑΡΙ ΝΩΠΟ ΦΑΓΚΡΙ:2,542:ΠΙΠΕΡΙΑ ΧΟΝΔΡΗ:2,541:ΠΙΠΕΡΙΑ ΜΑΚΡΙΑ ΦΛΩΡΙΝΗΣ:3";
            String products = "COCA COLA  PET ΦΙΑΛΗ 1.5Lt:1,ΜΠΙΣΚΟΤΑ ΠΤΙ ΜΠΕΡ ΠΑΠΑΔΟΠΟΥΛΟΥ 225gr:2,ΑΡΑΒΟΣΙΤΕΛΑΙΟ ΜΙΝΕΡΒΑ:3,ΜΠΥΡΑ AMSTEL ΜΕΤΑΛΛΙΚΟ ΚΟΥΤΙ 330ml:2,ΜΠΥΡΑ  HEINEKEN ΚΟΥΤΙ 500ml:2";

            double latitude = 37.9381;
            double longtitude = 23.6394;
            String[] pre = products.split(",");
            // System.out.println(pre.length);
            ArrayList<String> prod = new ArrayList<String>();//(Arrays.asList(products));
            for (String p : pre) {
                //System.out.println(p);
                prod.add(p);
            }
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionURL = "jdbc:mysql://83.212.110.120/Bizeli?useUnicode=yes&characterEncoding=UTF-8&user=penguin&password=~Agkinara`";
            Connection connection = DriverManager.getConnection(connectionURL);
            HashMap<String, Double> markets = ClosestMarkets(latitude, longtitude, 4.0, 10, connection);
            System.out.println(markets.keySet().toString());//+markets.get(1));
            String marketCarts = MarketCarts(prod, markets, connection);
            //System.out.println(marketCarts);//+markets.get(1));

            if (marketCarts.isEmpty()) {
                System.out.println("NoMarkets");
            }
            String minList = MinList(prod, markets, connection);
            String jsons = marketCarts.concat("---" + minList);
            System.out.println(jsons);
            /*
             Class.forName("com.mysql.jdbc.Driver").newInstance();
             String connectionURL = "jdbc:mysql://83.212.110.120/Bizeli?useUnicode=yes&characterEncoding=UTF-8&user=penguin&password=~Agkinara`";
             Connection connection = DriverManager.getConnection(connectionURL);
             // if (connect()) {
             ArrayList<String> markets = new ArrayList<String>();
             markets.add("ΑΒ ΑΘΗΝΩΝ (Πατησίων 240):5.0");
             markets.add("ΛΑΪΚΗ ΑΓΟΡΑ Ν. ΦΙΛΑΔΕΛΦΕΙΑΣ:7.0");
             markets.add("ΔΗΜΟΤΙΚΗ ΑΓΟΡΑ  ΒΑΡΒΑΚΕΙΟΣ:8.0");
             //hashmap product IDs and the amount of them the costumer wants to buy
             ArrayList<String> products = new ArrayList<String>();
             products.add("1344:ΨΑΡΙ ΝΩΠΟ ΦΑΓΚΡΙ ΕΙΣΑΓΩΓΗΣ:2");
             products.add("587:ΨΑΡΙ ΝΩΠΟ ΓΟΠΕΣ ΕΙΣΑΓΩΓΗΣ:1");
             products.add("1635:ΜΟΣΧΑΡΙ ΝΩΠΟ ΜΠΡΙΖΟΛΑ ΚΟΝΤΡΑ ΕΓΧΩΡΙΑ:2");
             products.add("956:ΧΟΙΡΙΝΟ ΝΩΠΟ ΜΠΡΙΖΟΛΑ ΛΑΙΜΟΥ ΜΕ ΟΣΤΑ ΕΓΧΩΡΙΑ:3");
             products.add("526:ΛΑΧΑΝΙΚΟ ΚΡΕΜΜΥΔΙ ΦΡΕΣΚΟ:3");
             //Veropoulos formionos 37.9702 23.7518 
             //my market antigonhs 38.0028 23.7518
             //carefour athinwn  aitwlias 37.9876  23.7624
             //AB athinwn
             HashMap<String, Double> closest = ClosestMarkets(38.0088, 23.7351, 10.0,10, connection);

             for (String s : closest.keySet()) {
             System.out.println(closest.get(s) + " has distance " + s);
             }

             Gson gson = new Gson();
             String json;
             //take the market carts
             json = MarketCarts(products, closest, connection);
             //create a type for Gson to recognise ArrayList<ArrayList<String>>
             java.lang.reflect.Type type = new TypeToken<ArrayList<ArrayList<String>>>() {
             }.getType();
             //parse json string
             ArrayList<ArrayList<String>> fin = gson.fromJson(json, type);
             //it works (y)
             for (ArrayList<String> p : fin) {
             System.out.println(p.get(0));//market cart stats
             System.out.println(p.get(1));//first product
             }

             json = MinList(products, closest, connection);
             ArrayList<String> fin2 = gson.fromJson(json, ArrayList.class);
             for (String p : fin2) {
             System.out.println(p);
             }
             // }*/
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AgTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String MarketCarts(ArrayList<String> productsList, HashMap<String, Double> markets, Connection connection) {
        try {
            Statement select = connection.createStatement();
            ResultSet rs = null;
            //each market has a cart with product Id and their correspoinding price
            HashMap<String, HashMap<String, Float>> MarketAndProducts = new HashMap<String, HashMap<String, Float>>();
            // HashMap<String, ArrayList<String>> MarketAndProducts = new HashMap<String, ArrayList<String>>();
            //HashMap for products and their amount
            HashMap<String, Integer> products = new HashMap<String, Integer>();
            //string of the products requested
            String ProductStr = "'";
            for (String p : productsList) {
                String[] pr = p.split(":");
                //id:product and the respectice amount
                //products.put(pr[0] + ":" + pr[1], Integer.parseInt(pr[2]));
                //System.out.println(pr[0] + pr[0].length());
                products.put(pr[0], Integer.parseInt(pr[1]));
                // System.out.println(pr[0]);
//str of names to query
                ProductStr = ProductStr.concat(pr[0] + "','");
            }
            //string of markets requested
            String MarketStr = "'";
            //HM for markets and their distance from costumer
            // HashMap<String, Double> markets = new HashMap<String, Double>();
            HashMap<String, String> marketLatLon = new HashMap<String, String>();
            HashMap<String, Double> marketsDist = new HashMap<String, Double>();
            for (String entry : markets.keySet()) {
                //market:distance
                //String[] pr = b.split(":");
                String[] e = entry.split("---");
                marketLatLon.put(e[0], e[1]);
                String ent = e[0];
                MarketStr = MarketStr.concat(ent + "','");
                marketsDist.put(ent, markets.get(entry));
                //hashmap of products and prices
                //  markets.put(ent.getKey(),ent.getValue() );
                //keep one hashmap with product-price for every market, representing the cart
                HashMap<String, Float> temp = new HashMap<String, Float>();
                MarketAndProducts.put(ent, temp);
            }

            if (ProductStr.length() > 0 && MarketStr.length() > 0) {
                //remoce the last , and the last ,'
                ProductStr = ProductStr.substring(0, ProductStr.length() - 2);
                MarketStr = MarketStr.substring(0, MarketStr.length() - 2);
                //query for the products
                String query = "SELECT name,price,market FROM prices WHERE name in (" + ProductStr + ") and market in (" + MarketStr + ")";
                //CONCAT(id,':',name) as 

                rs = select.executeQuery(query);
                while (rs.next()) {
                    String mar = rs.getString("market").trim();
                    //take the list until now for this market
                    HashMap<String, Float> temp = MarketAndProducts.get(mar);
                    //add the new product to the already existing cart, with its price* the amount the costumer wants
                    String name = rs.getString("name").trim();
                    Float price = (float) rs.getDouble("price");
                    // System.out.println(name + " " + price * 1);
                    //product with the final price
                    temp.put(name, price * products.get(name));
                    //put the renewed cart in the respective market 
                    MarketAndProducts.put(mar, temp);
                }
                if (MarketAndProducts.isEmpty()) {
                    return "";
                }
                /*
                 String ret=" "+name.length();
                 for(String p:products.keySet()){
                 ret=ret+" "+p.length();
                 if(p.equalsIgnoreCase(name)){
                 return name+Integer.toString(products.get(name));
                 }
                 }*/

                //find the expected price of all the products requested
                HashMap<String, Float> averages = new HashMap<String, Float>();
                query = "SELECT avg(price) as avg, name FROM prices WHERE name in (" + ProductStr + ") GROUP BY name";////////

                rs = select.executeQuery(query);
                //the average cost of this basket generally
                float expAvg = 0;
                System.out.println(ProductStr);
                while (rs.next()) {
                    Float avg = rs.getFloat("avg");
                    String name = rs.getString("name").trim();
                    //expected price is the average price of the product * # of these products the costumer wants to buy 

                    //System.out.println(name + " " + avg);
                    averages.put(name, avg * products.get(name));
                    expAvg += avg * products.get(name);
                }
                //the final arraylist returned
                ArrayList<ArrayList<String>> fin = new ArrayList<ArrayList<String>>();
                HashMap<String, Double> feat = new HashMap<String, Double>();
                HashMap<String, ArrayList<String>> ca = new HashMap<String, ArrayList<String>>();
                //find the final features of each market's cart
                for (String m : MarketAndProducts.keySet()) {

                    HashMap<String, Float> temp = MarketAndProducts.get(m);
                    //for cases where the market has no suitable product
                    if (temp.isEmpty()) {
                        continue;
                    }
                    //actual sum of market list's products
                    float actSum = 0;
                    for (float d : temp.values()) {
                        //System.out.println(d);
                        actSum += d;
                    }

                    //expected sum (with missing products)
                    float expSum = actSum;
                    for (String s : averages.keySet()) {
                        //if a product is not in the market's cart
                        if (!temp.keySet().contains(s)) {
                            // System.out.println(s);
                            //add its average price to the expected price of the cart
                            expSum += averages.get(s);
                        }
                    }

                    ///occupancy percentage of cart for that market
                    double occ = temp.size() * 100 / products.size();
                    //distance of the costumer to each market
                    double dist = marketsDist.get(m);
                    boolean expensive = false;
                    if (expSum > expAvg) {
                        expensive = true;
                    }// + occ + ":" 
                    String features = m + ":" + actSum + ":" + expSum + ":" + dist + ":" + expensive + ":" + marketLatLon.get(m);

                    feat.put(features, occ);
                    //the list of the market
                    ArrayList<String> cart = new ArrayList<String>();
                    //append the features to the first position of the market's cart
                    cart.add(features + ":" + occ);
                    //append to the rest of the posistions the products of the cart and their respective prices
                    for (Map.Entry o : temp.entrySet()) {
                        cart.add(o.getKey() + ":" + o.getValue());
                    }
                    ca.put(features, cart);
                    //fin.add(cart);
                }

                //sort by occupancy
                LinkedHashMap<String, Double> sorted = sortHashMapByValues(feat);
                for (Map.Entry<String, Double> plom : sorted.entrySet()) {
                    String maStats = plom.getKey();
                    fin.add(ca.get(maStats));
                }
                Gson gson = new Gson();
                String json = gson.toJson(fin);
                return json;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AgTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String MinList(ArrayList<String> productsList, HashMap<String, Double> markets, Connection connection) {
        try {
            Statement select = connection.createStatement();
            ResultSet rs = null;
            //string of products requested
            String ProductStr = "'";
            ArrayList<String> kla = new ArrayList<String>();
            for (String p : productsList) {
                String[] pr = p.split(":");
                ProductStr = ProductStr.concat(pr[0] + "','");
               // System.out.println("mesa "+pr[0]);
                kla.add(pr[0]);
            }
            //string of markets requested
            String MarketStr = "'";
            HashMap<String, String> marketLatLon = new HashMap<String, String>();
            HashMap<String, Double> marketsDist = new HashMap<String, Double>();
            for (String entry : markets.keySet()) {
                //String[] pr = b.split(":");
                //market:distance
                //String[] pr = b.split(":");
                String[] e = entry.split("---");
                marketLatLon.put(e[0], e[1]);
                marketsDist.put(e[0], markets.get(entry));
                MarketStr = MarketStr.concat(e[0] + "','");
            }
            if (ProductStr.length() > 0 && MarketStr.length() > 0) {
                //remove the last , and the last ,'
                ProductStr = ProductStr.substring(0, ProductStr.length() - 2);
                MarketStr = MarketStr.substring(0, MarketStr.length() - 2);
                /*
                 String query = "SELECT min( price ) as min , market,CONCAT(id,':',name) as name FROM `prices` WHERE id in (" + ProductList + ") and market in (" + MarketList + ") GROUP BY id"; 
                 while(rs.next()){
                 String product = rs.getString("market") + ":" +  + ":" + rs.getString("name") + ":" + rs.getString("min");
                 fin.add(product);
                 }
                 */

                String query = "SELECT name,price,market FROM prices WHERE name in (" + ProductStr + ") and market in (" + MarketStr + ")";
               //CONCAT(id,':',name) as 
                // System.out.println(query);

                //HM for the product and the respective price
                HashMap<String, Double> prodPrice = new HashMap<String, Double>();
                //HM for the product and the market that currently has it cheaper
                HashMap<String, String> prodMarket = new HashMap<String, String>();
                ArrayList<String> fin = new ArrayList<String>();
                rs = select.executeQuery(query);
                while (rs.next()) {
                    String name = rs.getString("name").trim();
                    // System.out.println(name);
                    //if the product has occured again in the result set
                    if (prodPrice.containsKey(name)) {
                        Double price = rs.getDouble("price");
                        // and if its current price is cheaper than the previous 
                        if (price < prodPrice.get(name)) {
                            //keep that price
                            prodPrice.put(name, price);
                            String mar = rs.getString("market").trim();
                            //and keep that market as the best option for that product
                            prodMarket.put(name, mar);
                        }
                    } else {
                        //if it is the first time this product occurs in the result set
                        Double price = rs.getDouble("price");
                        String mar = rs.getString("market").trim();
                        //keep this price as the min
                        prodPrice.put(name, price);
                        //keep this market as min's price market for this product
                        prodMarket.put(name, mar);
                    }
                }
                for (Map.Entry a : prodMarket.entrySet()) {
                    //take the id:name of the product
                    String name = a.getKey().toString();
                    //make the string that corresponds to market of min price:id:product:min(price of product)
                    String product = a.getValue() + ":" + name + ":" + marketLatLon.get(a.getValue()) + ":" + prodPrice.get(name) + ":" + marketsDist.get(a.getValue());
                    // System.out.println(product);
                    fin.add(product);
                }
                for (String s : kla) {
                    if (!prodMarket.containsKey(s)) {
                        fin.add("-1:" +s+":-1"+":-1"+":-1"+":-1");
                    }
                }
                Gson gson = new Gson();
                String json = gson.toJson(fin);
                // System.out.println(json);
                return json;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(AgTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static HashMap<String, Double> ClosestMarkets(double lat, double lon, double withinDist, int numberOfMarkets, Connection connection) {
        try {
            final int R = 6371; // Radious of the earth
            Statement select = connection.createStatement();
            ResultSet rs = null;

            String query = "SELECT name,lat,lon from markets";

            //apo dw http://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
            rs = select.executeQuery(query);

            TreeMap<Double, String> marketDist = new TreeMap<Double, String>();
            while (rs.next()) {
                //Haversine Formula
                Double dblat = rs.getDouble("lat");
                Double dblon = rs.getDouble("lon");
                String name = rs.getString("name");
                Double latDistance = toRad(dblat - lat);
                Double lonDistance = toRad(dblon - lon);
                Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                        + Math.cos(toRad(dblat)) * Math.cos(toRad(dblon))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                Double dist = R * c;
                //System.out.println(name+ " distance from target : "+dist );
                name = name + "---" + dblat + ":" + dblon;//////no time to make a class Super Market
                marketDist.put(dist, name);
            }

            //sort the markets by distance and take those in a radius of withinDist
            SortedMap<Double, String> temp = new TreeMap<Double, String>();
            temp = marketDist.headMap(withinDist);
            HashMap<String, Double> result = new HashMap<String, Double>();
            int i = 0;
            //keep the first ten
            for (Map.Entry<Double, String> entry : temp.entrySet()) {
                i++;
                // System.out.println(entry.getKey());
                result.put(entry.getValue(), entry.getKey());
                if (i == numberOfMarkets) {
                    break;
                }
            }
            return result;
            // Gson gson = new Gson();
            // String json = gson.toJson(marketDist);
            // System.out.println(json);
        } catch (SQLException ex) {
            Logger.getLogger(AgTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    /*   
     public static ArrayList<String> ClosestMarkets(double Lat, double Lon, Connection connection) {
     try {
     Statement select = connection.createStatement();
     ResultSet rs = null;
            
     String query = "SELECT name, 6371 * 2*ATAN2(SQRT(POW(sin( radians((" + Lat + "-lat)/2) ),2) + cos( radians( " + Lat + ") )*cos( radians(lat) )*POW(sin( radians((" + Lon + "-lon)/2) ),2)),"
     + "SQRT(POW(sin( radians((" + Lat + "-lat)/2) ),2) + cos( radians( " + Lat + ") )*cos( radians(lat) )*POW(sin( radians((" + Lon + "-lon)/2) ),2)))"
     + " AS distance FROM markets HAVING distance<1000000 ORDER BY distance desc LIMIT 0,10";

     //apo dw http://bigdatanerd.wordpress.com/2011/11/03/java-implementation-of-haversine-formula-for-distance-calculation-between-two-points/
     rs = select.executeQuery(query);
     ArrayList<String> marketDist = new ArrayList<String>();
     while (rs.next()) {
     System.out.println(rs.getString("name") + ":" + rs.getFloat("distance"));
     marketDist.add(rs.getString("name") + ":" + rs.getFloat("distance"));
     }
     // Gson gson = new Gson();
     // String json = gson.toJson(marketDist);
     // System.out.println(json);
     return marketDist;
     } catch (SQLException ex) {
     Logger.getLogger(AgTools.class.getName()).log(Level.SEVERE, null, ex);
     }
     return null;
     }
     */
    public static LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        //sort the value set sepparately
        Collections.sort(mapValues, Collections.reverseOrder());
        //Collections.sort(mapKeys);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap();
        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();
            //find the key that corresponds to the sorted value
            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();
                //tally that key to this value in the sorted hashmap
                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Double) val);
                    break;
                }
            }
        }
        return sortedMap;
    }

}
