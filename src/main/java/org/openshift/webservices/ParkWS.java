package org.openshift.webservices;


/**
 * Created with IntelliJ IDEA.
 * User: spousty
 * Date: 11/5/13
 * Time: 8:00 AM
 * To change this template use File | Settings | File Templates.
 */

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import org.openshift.data.ParkpointsEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
The EJB container handles @PersistenceContext injection ... ParkWS is not an EJB  ...
you might want to make it an stateless session bean by adding @Stateless to it
 */

@Stateless
@Path("parks")
public class ParkWS {

    //get the entity manager from the container. No need to automanage
    //This also includes the closing of the EM
    //No need to name the unit because we only have one persistence unit in persistence.xml
    @PersistenceContext(name = "parks")
    EntityManager em;

    @GET
    @Produces("application/json")
    public List getAllParks(){
        List allParksList = new ArrayList();
        Query query =   em.createQuery("select p from ParkpointsEntity p");
        ArrayList templist = (ArrayList) query.getResultList();
        if (templist != null && templist.size() > 0){
            allParksList = processQueryResults(templist);
        }
        return allParksList;
    }

    @GET
    @Produces("application/json")
    @Path("within")
    public List findParksWithin(@QueryParam("lat1") float lat1, @QueryParam("lon1") float lon1, @QueryParam("lat2") float lat2, @QueryParam("lon2") float lon2){
        ArrayList<Map> allParksList = new ArrayList<Map>();

        //since our data in PostGIS has a srid of 4326, we need to use a geometryfactory to get a geom with that model
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        //time to make the box filter
        //the lat2 and lon2 are the mins
        StringBuffer sb = new StringBuffer("POLYGON((");
        sb.append(lon2 + " ");
        sb.append(lat2 + ",");
        sb.append(lon1 + " ");
        sb.append(lat2 + ",");
        sb.append(lon1 + " ");
        sb.append(lat1 + ",");
        sb.append(lon2 + " ");
        sb.append(lat1 + ",");
        sb.append(lon2 + " ");
        sb.append(lat2 + "))");
        WKTReader wktReader = new WKTReader(geometryFactory);
        Geometry boxFilter = null;
        System.out.println(sb.toString());
        try {
            boxFilter = wktReader.read(sb.toString());
        } catch (Exception e){
            System.out.println("Threw exception making the filter: " + e.getClass() + " :: " + e.getMessage());
        }

        Query qe = em.createQuery("select p from ParkpointsEntity p where intersects(p.theGeom, :filter) = true", ParkpointsEntity.class);
        qe.setParameter("filter", boxFilter);
        allParksList = processQueryResults((ArrayList) qe.getResultList());

        return allParksList;
    }


    private ArrayList processQueryResults(ArrayList inList){
        ArrayList forResults = new ArrayList(inList.size());
        for(int i = 0; i < inList.size(); i++){
            HashMap park = new HashMap();
            ParkpointsEntity ppe = (ParkpointsEntity) inList.get(i);
            park.put("name", ppe.getName())   ;
            park.put("id", ppe.getParkid());
            double[] positions = {ppe.getTheGeom().getX(),ppe.getTheGeom().getY()};
            park.put("position", positions) ;



            forResults.add(park);

        }

        return forResults;
    }
}