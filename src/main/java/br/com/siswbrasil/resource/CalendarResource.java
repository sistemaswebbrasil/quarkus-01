package br.com.siswbrasil.resource;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.siswbrasil.dto.CalendarEventDTO;
import br.com.siswbrasil.model.CalendarEvent;
import br.com.siswbrasil.model.ExtendedProps;
import br.com.siswbrasil.model.Guest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/apps/calendar")
@Tag(name = "Calendar", description = "Operações relacionadas a eventos de calendário")
public class CalendarResource implements CalendarResourceDoc {

    private static final Logger logger = LoggerFactory.getLogger(CalendarResource.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @GET
    @Override
    public List<CalendarEvent> getAllEvents() {
        return CalendarEvent.listAll();
    }

    @GET
    @Path("/filter")
    @Override
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
    @Override
    public Response getEventById(@PathParam("id") Long id) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(event).build();
    }

    @POST

    @Transactional
    @Override
    public Response addEvent(@Valid CalendarEventDTO eventDTO) {
        logger.debug("Received eventDTO: {}", eventDTO);

        CalendarEvent event = new CalendarEvent();
        event.title = eventDTO.title;
        event.start = OffsetDateTime.parse(eventDTO.start, FORMATTER);
        event.endDate = OffsetDateTime.parse(eventDTO.end, FORMATTER);
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
    @Transactional
    @Override
    public Response updateEvent(@PathParam("id") Long id, CalendarEventDTO eventDTO) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.title = eventDTO.title;
        event.start = OffsetDateTime.parse(eventDTO.start, FORMATTER);
        event.endDate = OffsetDateTime.parse(eventDTO.end, FORMATTER);
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
    @Transactional
    @Override
    public Response deleteEvent(@PathParam("id") Long id) {
        CalendarEvent event = CalendarEvent.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        event.delete();
        return Response.noContent().build();
    }
}
