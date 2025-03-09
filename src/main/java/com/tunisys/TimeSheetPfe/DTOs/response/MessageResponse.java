package com.tunisys.TimeSheetPfe.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;

    public static MessageResponse build(String message){
        return new MessageResponse(message) ;
    }

    public static ResponseEntity<MessageResponse> badRequest(String message){
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }

    public static ResponseEntity<MessageResponse> ok(String message){
        return ResponseEntity.ok().body(new MessageResponse(message));
    }
}
