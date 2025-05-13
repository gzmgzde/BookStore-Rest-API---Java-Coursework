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
public class OutOfStockExceptionMapper implements ExceptionMapper<OutOfStockException> {

    private static final Logger logger =LoggerFactory.getLogger(OutOfStockExceptionMapper.class);
            
    @Override
    public Response toResponse (OutOfStockException e){
        logger.error("Stock not found. {}", e.getMessage(), e);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        
                
        
    }
    
}
