/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import static api.AgTools.ClosestMarkets;
import static api.AgTools.MarketCarts;
import static api.AgTools.MinList;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Giannis
 */
@Path("api")
public class GenericResource {

    @Context
    private UriInfo context;

    /*
     @GET
     @Produces("application/json")
     public String getXml() {
     return "AGINARA ROCKS";
     }
     */
    @POST
    @Path("/marketCarts")
    @Produces("application/json")
    public Response marketCarts(@FormParam("latitude") double latitude, @FormParam("longtitude") double longtitude, @FormParam("products") String products) {

        try {
            String[] pre = URLDecoder.decode(products, "UTF-8").split(",");
            ArrayList<String> prod = new ArrayList<String>();//(Arrays.asList(products));
            for (String p : pre) {
                prod.add(p);
            }

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionURL = "jdbc:mysql://83.212.110.120/Bizeli?useUnicode=yes&characterEncoding=UTF-8&user=penguin&password=~Agkinara`";
            Connection connection = DriverManager.getConnection(connectionURL);

            HashMap<String, Double> markets = ClosestMarkets(latitude, longtitude, 4.0, 10, connection);
            //if there where no markets close
            if (markets.isEmpty() || markets.equals(null)) {
                return Response.status(200).entity("noMarkets").header("Access-Control-Allow-Origin", "GET,POST").build();
            }
            String marketCarts = MarketCarts(prod, markets, connection);
            //if there were no demanded product found in any of the markets 
            if (marketCarts.length() == 0 || marketCarts.equals(null)) {
                connection.close();
                return Response.status(200).entity("noProducts").header("Access-Control-Allow-Origin", "GET,POST").build();
            }
            connection.close();

            return Response.status(200).entity(marketCarts).header("Access-Control-Allow-Origin", "GET,POST").build();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(200).entity("").build();
    }

    
    @POST
    @Path("/minList")
    @Produces("application/json")
    public Response minList(@FormParam("latitude") double latitude, @FormParam("longtitude") double longtitude, @FormParam("products") String products) {

        try {

            String[] pre = URLDecoder.decode(products, "UTF-8").split(",");
            ArrayList<String> prod = new ArrayList<String>();//(Arrays.asList(products));
            for (String p : pre) {
                prod.add(p);
            }

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionURL = "jdbc:mysql://83.212.110.120/Bizeli?useUnicode=yes&characterEncoding=UTF-8&user=penguin&password=~Agkinara`";
            Connection connection = DriverManager.getConnection(connectionURL);

            HashMap<String, Double> markets = ClosestMarkets(latitude, longtitude, 4.0, 10, connection);
            //if there where no markets close
            if (markets.isEmpty() || markets.equals(null)) {
                return Response.status(200).entity("noMarkets").header("Access-Control-Allow-Origin", "GET,POST").build();
            }
            String marketCarts = MarketCarts(prod, markets, connection);
            //if there were no demanded product found in any of the markets 

            String minList = MinList(prod, markets, connection);

            if (minList.length() == 0 || minList.equals(null)) {
                connection.close();
                return Response.status(200).entity("noProducts").header("Access-Control-Allow-Origin", "GET,POST").build();
            }
            connection.close();
            return Response.status(200).entity(minList).header("Access-Control-Allow-Origin", "GET,POST").build();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(200).entity("").build();
    }
}
