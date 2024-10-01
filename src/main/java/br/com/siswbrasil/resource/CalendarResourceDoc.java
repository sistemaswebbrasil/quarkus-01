package br.com.siswbrasil.resource;

import br.com.siswbrasil.dto.CalendarEventDTO;
import br.com.siswbrasil.model.CalendarEvent;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Calendar", description = "Operações relacionadas a eventos de calendário 2")
public interface CalendarResourceDoc {
    @GET
     
    @Operation(summary = "Obter todos os eventos", description = "Retorna uma lista de todos os eventos de calendário")
    @APIResponse(
            responseCode = "200",
            description = "Lista de eventos de calendário",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    List<CalendarEvent> getAllEvents();

    @GET
    @Path("/filter")
    
    @Operation(summary = "Filtrar eventos por data", description = "Retorna uma lista de eventos de calendário filtrados pela data inicial e final no formato yyyy-MM-dd")
    @APIResponse(
            responseCode = "200",
            description = "Lista de eventos de calendário filtrados",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Parâmetros de data inválidos"
    )
    Response getEventsByDateRange(@QueryParam("startDate") String startDateStr, @QueryParam("endDate") String endDateStr);

    @GET
    @Path("/{id}")
    
    @Operation(summary = "Obter evento por ID", description = "Retorna um evento de calendário pelo ID")
    @APIResponse(
            responseCode = "200",
            description = "Evento encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Evento não encontrado"
    )
    Response getEventById(@PathParam("id") Long id);

    @POST
    
    
    @Operation(summary = "Adicionar um novo evento", description = "Adiciona um novo evento ao calendário")
    @APIResponse(
            responseCode = "201",
            description = "Evento criado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Transactional
    Response addEvent(
            @RequestBody(
                    description = "Dados do evento a ser criado",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CalendarEventDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Evento",
                                    value = "{ \"title\": \"Reunião de planejamento\", \"start\": \"2024-10-01T07:00:00.000Z\", \"end\": \"2024-10-01T08:00:00.000Z\", \"allDay\": false, \"url\": \"http://example.com/event\", \"extendedProps\": { \"guests\": [\"Jane Foster\", \"Sandy Vega\"], \"location\": \"Sala de Reuniões\", \"description\": \"Planejar as próximas etapas do projeto\", \"calendar\": \"Business\" } }"
                            )
                    )
            ) @Valid CalendarEventDTO eventDTO);

    @PUT
    @Path("/{id}")
    
    
    @Operation(summary = "Atualizar evento", description = "Atualiza um evento existente pelo ID")
    @APIResponse(
            responseCode = "200",
            description = "Evento atualizado com sucesso",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Evento não encontrado"
    )
    @Transactional
    Response updateEvent(@PathParam("id") Long id, CalendarEventDTO eventDTO);

    @DELETE
    @Path("/{id}")
    
    @Operation(summary = "Excluir evento", description = "Exclui um evento existente pelo ID")
    @APIResponse(
            responseCode = "204",
            description = "Evento excluído com sucesso"
    )
    @APIResponse(
            responseCode = "404",
            description = "Evento não encontrado"
    )
    @Transactional
    Response deleteEvent(@PathParam("id") Long id);
}
