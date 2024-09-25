package br.com.siswbrasil.resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import br.com.siswbrasil.dto.CalendarEventDTO;
import br.com.siswbrasil.model.CalendarEvent;
import br.com.siswbrasil.model.ExtendedProps;
import br.com.siswbrasil.model.Guest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/calendar")
@Tag(name = "Calendar", description = "Operações relacionadas a eventos de calendário")
public class CalendarResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    @Operation(summary = "Obter todos os eventos", description = "Retorna uma lista de todos os eventos de calendário")
    @APIResponse(
        responseCode = "200",
        description = "Lista de eventos de calendário",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CalendarEvent.class))
    )
    public List<CalendarEvent> getAllEvents() {
        return CalendarEvent.listAll();
    }

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response getEventsByDateRange(@QueryParam("startDate") String startDateStr, @QueryParam("endDate") String endDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            List<CalendarEvent> events = CalendarEvent.<CalendarEvent>streamAll()
                .filter(event -> {
                    LocalDate eventDate = event.start.toLocalDate();
                    return (eventDate.isEqual(startDate) || eventDate.isAfter(startDate)) &&
                           (eventDate.isEqual(endDate) || eventDate.isBefore(endDate));
                })
                .collect(Collectors.toList());

            return Response.ok(events).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Parâmetros de data inválidos").build();
        }
    }    

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response getEventById(@PathParam("id") Long id) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(event).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response addEvent(
        @org.eclipse.microprofile.openapi.annotations.parameters.RequestBody(
            description = "Dados do evento a ser criado",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = CalendarEventDTO.class),
                examples = @ExampleObject(
                    name = "Exemplo de Evento",
                    value = "{ \"title\": \"Reunião de planejamento\", \"start\": \"2024-10-01 07:00\", \"end\": \"2024-10-01 08:00\", \"allDay\": false, \"url\": \"http://example.com/event\", \"extendedProps\": { \"guests\": [\"Jane Foster\", \"Sandy Vega\"], \"location\": \"Sala de Reuniões\", \"description\": \"Planejar as próximas etapas do projeto\", \"calendar\": \"Business\" } }"
                )
            )
        ) CalendarEventDTO eventDTO) {
        CalendarEvent event = new CalendarEvent();
        event.title = eventDTO.title;
        event.start = LocalDateTime.parse(eventDTO.start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        event.endDate = eventDTO.end != null ? LocalDateTime.parse(eventDTO.end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
        event.allDay = eventDTO.allDay;
        event.url = eventDTO.url;

        if (eventDTO.extendedProps != null && eventDTO.extendedProps.guests != null) {
            // Converte a lista de nomes em uma lista de objetos Guest
            List<Guest> guests = eventDTO.extendedProps.guests.stream().map(name -> {
                Guest guest = new Guest();
                guest.name = name;
                return guest;
            }).collect(Collectors.toList());

            // Persiste os objetos Guest
            guests.forEach(guest -> guest.persist());

            // Cria e persiste o ExtendedProps
            ExtendedProps extendedProps = new ExtendedProps();
            extendedProps.location = eventDTO.extendedProps.location;
            extendedProps.description = eventDTO.extendedProps.description;
            extendedProps.calendar = eventDTO.extendedProps.calendar;
            extendedProps.guestList = guests;
            extendedProps.persist();

            // Atualiza o evento com o ExtendedProps
            event.extendedProps = extendedProps;
        }

        // Persiste o evento
        event.persist();

        return Response.status(Response.Status.CREATED).entity(event).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response updateEvent(@PathParam("id") Long id, CalendarEventDTO eventDTO) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.title = eventDTO.title;
        event.start = LocalDateTime.parse(eventDTO.start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        event.endDate = eventDTO.end != null ? LocalDateTime.parse(eventDTO.end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
        event.allDay = eventDTO.allDay;
        event.url = eventDTO.url;

        if (eventDTO.extendedProps != null && eventDTO.extendedProps.guests != null) {
            // Converte a lista de nomes em uma lista de objetos Guest
            List<Guest> guests = eventDTO.extendedProps.guests.stream().map(name -> {
                Guest guest = new Guest();
                guest.name = name;
                return guest;
            }).collect(Collectors.toList());

            // Persiste os objetos Guest
            guests.forEach(guest -> guest.persist());

            // Cria e persiste o ExtendedProps
            ExtendedProps extendedProps = new ExtendedProps();
            extendedProps.location = eventDTO.extendedProps.location;
            extendedProps.description = eventDTO.extendedProps.description;
            extendedProps.calendar = eventDTO.extendedProps.calendar;
            extendedProps.guestList = guests;
            extendedProps.persist();

            // Atualiza o evento com o ExtendedProps
            event.extendedProps = extendedProps;
        }

        // Atualiza o evento
        event.persist();

        return Response.ok(event).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response deleteEvent(@PathParam("id") Long id) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.delete();
        return Response.noContent().build();
    }
}