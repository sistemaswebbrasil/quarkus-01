package br.com.siswbrasil.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class HomeResource {

    @GET
    public Response redirectToAbout() {
        return Response.status(Response.Status.FOUND)
                       .location(java.net.URI.create("/about"))
                       .build();
    }
}