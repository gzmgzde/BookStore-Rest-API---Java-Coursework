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
public class CartNotFoundExceptionMapper implements ExceptionMapper<CartNotFoundException> {

    private static final Logger logger =LoggerFactory.getLogger(CartNotFoundExceptionMapper.class);
            
    @Override
    public Response toResponse (CartNotFoundException e){
        logger.error("Cart not found. {}", e.getMessage(), e);
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        
                
        
    }
    
}
