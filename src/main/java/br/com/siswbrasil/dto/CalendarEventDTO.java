package br.com.siswbrasil.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import br.com.siswbrasil.validation.ValidDateFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "DTO para representar um evento de calendário")
public class CalendarEventDTO {

    @NotBlank(message = "Title is required")
    @Schema(description = "Título do evento", example = "Reunião de planejamento")
    public String title;

    @Schema(description = "Data e hora de início do evento", example = "2024-10-01T07:00:00.000Z")
    @NotBlank(message = "Start date is required")
    @ValidDateFormat
    public String start;

    @Schema(description = "Data e hora de término do evento", example = "2024-10-01T08:00:00.000Z")
    @NotBlank(message = "End date is required")
    @ValidDateFormat
    public String end;

    @Schema(description = "Indica se o evento dura o dia todo", example = "false")
    public boolean allDay;

    @Schema(description = "URL associada ao evento", example = "http://example.com/event")
    public String url;

    @Valid
    @Schema(description = "Propriedades estendidas do evento")
    public ExtendedPropsDTO extendedProps;

    @Schema(description = "DTO para representar as propriedades estendidas de um evento de calendário")    
    public static class ExtendedPropsDTO {

        @Schema(description = "Lista de convidados do evento", type = SchemaType.ARRAY, implementation = String.class, example = "[\"Jane Foster\", \"Sandy Vega\"]")
        public List<String> guests;
        
        @Schema(description = "Localização do evento", example = "Sala de Reuniões")
        public String location;

        @NotBlank
        @Schema(description = "Descrição do evento", example = "Planejar as próximas etapas do projeto")
        public String description;

        @NotBlank
        @Schema(description = "Tipo de calendário", example = "Business")
        public String calendar;
    }
}