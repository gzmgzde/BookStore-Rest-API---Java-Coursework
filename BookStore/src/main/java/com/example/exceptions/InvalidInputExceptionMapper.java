/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exceptions;

/**
 *
 * @author gizem
 */
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    private static final Logger logger =LoggerFactory.getLogger(InvalidInputExceptionMapper.class);
            
    @Override
    public Response toResponse (InvalidInputException e){
        logger.error("Invalid input. {}", e.getMessage(), e);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        
                
        
    }
    
}
