package com.tunisys.TimeSheetPfe.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private int httpcode ;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime errorDate= LocalDateTime.now();

    private String message = "Something went wrong";

}
