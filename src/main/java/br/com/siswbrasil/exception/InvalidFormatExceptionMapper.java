package br.com.siswbrasil.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {
        String fieldName = exception.getPath().stream()
                                    .map(ref -> ref.getFieldName())
                                    .reduce((first, second) -> second)
                                    .orElse("unknown field");

        ErrorResponse errorResponse = new ErrorResponse(
            String.format("Formato inválido para o campo '%s'. O formato correto é yyyy-MM-dd'T'HH:mm:ss.SSSX", fieldName)
        );

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(errorResponse)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}