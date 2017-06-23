package com.unity3d.api;

import com.unity3d.service.ProjectService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.Date;

/**
 * Created by shashwatajmani on 5/27/17.
 * Create Project API to create the Projects.txt file with the input JSON data.
 */

@Path("/createProject")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public class CreateProject {

    @POST
    public String postData(String jsonData){

        Date date = new Date();
        System.out.println(date.toString() + " /createProject API Request \n" + "data : " +
                    jsonData );
        String statusMessage = "";
        ProjectService projectService = new ProjectService();
        statusMessage = projectService.create(jsonData);

        date = new Date();
        System.out.println(date.toString() + " Response : " + statusMessage );
        return statusMessage;
    }
}
