package com.unity3d.api;

import com.unity3d.service.ProjectService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by shashwatajmani on 5/27/17.
 * Request Project API to fetch the details of the project corresponding to the parameters
 * passed in the URL.
 */

@Path("/requestProject")
@Produces(MediaType.APPLICATION_JSON)
public class RequestProject {

    @GET
    public String getData(@QueryParam("projectid") Integer projectId,
                          @QueryParam("country") String country,
                          @QueryParam("number") Integer number,
                          @QueryParam("keyword") String keyword){

        Date date = new Date();
        System.out.println(date.toString() + " /requestProject API Request " +
                "with projectId = "+ projectId + ", country = " + country + ", number = " + number +
                ", keyword = " + keyword );

        String result = "";
        ProjectService projectService = new ProjectService();
        result = projectService.retrieve(projectId, country, number, keyword);
        date = new Date();
        System.out.println(date.toString() + " Response after retrieve : \n" + result);
        return result;
    }
}
