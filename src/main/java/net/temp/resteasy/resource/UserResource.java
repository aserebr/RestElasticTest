package net.temp.resteasy.resource;


import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.temp.elastic.clients.IndexingOperationsManager;


/**
 * 
 * 
 *
 */

@Path("elastic")
public class UserResource {


    private IndexingOperationsManager main = new IndexingOperationsManager();

    @Path("/{index}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDocumentById(@PathParam("index") String index,String jsonString) {

    	System.out.println("!!!!!!!!!!index="+index);

    	ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        String idValue=null;
		try {
			jsonNode = objectMapper.readTree(jsonString);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (jsonNode!=null) {
			idValue = jsonNode.get("id").asText();
		}
         if (idValue==null) {
        	 return Response.status(Response.Status.NOT_FOUND).build();
		}


    	System.out.println("!!!!!!!!!!id="+idValue);
    	String result;

    	try {
    		
    		result=main.getDocumentById(index, idValue);
    	} catch (IOException e) {

    		e.printStackTrace();
    		return Response.notModified().build();
    	}
    	return Response.ok(result ).status(Response.Status.CREATED).build();
    }
    
    @Path("/{index}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createIndex(@PathParam("index") String index) {


    	System.out.println("!!!!!!!!!!index="+index);
    	try {
    		main.createIndexUsingClient(index);
    	} catch (IOException e) {

    		e.printStackTrace();
    		return Response.notModified().build();
    	}

    	return Response.ok().status(Response.Status.CREATED).build();

    }
    
    
    @PUT
    @Path("/{index}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response indexDocument(@PathParam("index") String index , String jsonString) {
    	String id;
    	try {
    		System.out.println("!!!!!!!!!!index="+index);
    		System.out.println(jsonString);
    		id=main.indexDocumentWithJSON(index, jsonString);
    	} catch (IOException e) {

    		e.printStackTrace();
    		return Response.notModified().build();
    	}
    	return Response.ok("Indexed with id ="+id ).status(Response.Status.CREATED).build();
        
    }
    
    

}