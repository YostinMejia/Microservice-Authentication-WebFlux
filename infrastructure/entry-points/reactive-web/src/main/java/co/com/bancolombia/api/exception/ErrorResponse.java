package co.com.bancolombia.api.exception;

import java.time.Instant;
import java.util.Map;


public record ErrorResponse(
        String error,
        String message,
        int status,
        String path,
        Instant timestamp,
        Map<String, Object> details
){
}
