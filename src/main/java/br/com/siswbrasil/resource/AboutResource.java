package br.com.siswbrasil.resource;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/about")
public class AboutResource {

    @Inject
    Template about;

    @ConfigProperty(name = "project.version")
    String projectVersion;

    @ConfigProperty(name = "project.name")
    String projectName;    

    @Inject
    @ConfigProperty(name = "quarkus.profile",defaultValue = "default")
    String quarkusProfile;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return about.data("quarkusProfile", quarkusProfile)
                    .data("projectName", projectName)
                    .data("projectVersion", projectVersion);
    }
}
