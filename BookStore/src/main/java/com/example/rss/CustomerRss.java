/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.rss;

/**
 *
 * @author gizem
 */
import com.example.model.Customers;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.exceptions.*;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerRss {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRss.class);

    private static List<Customers> customers = new ArrayList<>();

    static {
        customers.add(new Customers("Alice Johnson", "alice.johnson@example.com", "aB6$kM1dGp^9", 20000));
        customers.add(new Customers("Bob Smith", "bobsmith99@example.com", "hF!0ZsJ9wptQ", 20001));
        customers.add(new Customers("Clara Zhao", "clara.zhao@example.com", "rER~XVuwtJT", 20002));
        customers.add(new Customers("David Patel", "david.patel@example.com", "T2qjA9k8mZLf", 20003));
        customers.add(new Customers("Emma García", "emma.garcia@example.com", "3&jxX8Vo@1nT", 20004));
        customers.add(new Customers("Farah Khan", "farah.khan@example.com", "bT3$gHkz7Iw2", 20005));
        customers.add(new Customers("George Ivanov", "george.ivanov@example.com", "!9mEr4L5yQxS", 20006));
        customers.add(new Customers("Hannah Kim", "hannah.kim@example.com", "H2#YzNc8vUz7\n", 20007));
        customers.add(new Customers("Isaac Müller", "isaac.muller@example.com", "k@YV0l4iRq8S", 20008));
        customers.add(new Customers("Julia Rossi", "julia.rossi@example.com", "J*Wo6Nw4fLg@", 20009));
    }

    // Get all customers
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customers> getAllCustomers() {
        logger.info("GET request to retrieve all customers. ");

        return customers;
    }

    // Get customer by ID
    @GET
    @Path("/{custId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customers findCustomerById(@PathParam("custId") int custId) {
        logger.info("GET request to retrieve customers by id " + custId);

        //filter customers by their id, find the first matching value and print. If book not found, print error message. 
        return customers.stream()
                .filter(entry -> entry.getCustId() == custId)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found.  " + custId));

    }

    // Add new customer
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCustomer(Customers customer) {
        logger.info("POST request to add authors ");

        //generate new customer id and set as id
        int newCustId = nextCustomerId();
        customer.setCustId(newCustId);

        //check if name  is empty, if yes, print error message. 
        if (customer.getCustName() == null || customer.getCustName().trim().isEmpty()) {
            throw new InvalidInputException("Customer name is missing.");
        } //check if password is empty, if yes, print error message. 
        else if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("Password is missing.");
        } //check if price is less/equal than zero, if yes, print error message. 
        else if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("Email is missing.");
        }

        //add customer
        customers.add(customer);

        logger.info("Added new customer " + newCustId);
        
        //print 201-Created code with details of the added customer
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(customer) // Include the  customer details
            .build();
    }

    // Update customer details
    @PUT
    @Path("/{custId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("custId") int custId, Customers updatedCust) {
        logger.info("PUT request to update customer by id " + custId);

        //check to find the customer by its id, if not found, print errr message
        Customers existingCust = customers.stream()
                .filter(customer -> customer.getCustId() == custId)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + custId + " not found."));

        // set the customer id for the updated customer
        updatedCust.setCustId(custId);

        //check if name  is empty, if yes, print error message. 
        if (updatedCust.getCustName() == null || updatedCust.getCustName().trim().isEmpty()) {
            throw new InvalidInputException("Customer name is missing.");
        } //check if password is empty, if yes, print error message. 
        else if (updatedCust.getPassword() == null || updatedCust.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("Password is missing.");
        } //check if price is less/equal than zero, if yes, print error message. 
        else if (updatedCust.getEmail() == null || updatedCust.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("Email is missing.");
        }
        
        // for loop to get customer
        for (int i = 0; i < customers.size(); i++) {
            Customers customer = customers.get(i);

            //if customer id is same as updatedCust id, update details for that customer
            if (customer.getCustId() == updatedCust.getCustId()) {
                customers.set(i, updatedCust);  

                logger.info("Customer information updated successfully for customer ID: " + custId);
            }
        }
        //print 201-Created code with details of the updated customer
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(updatedCust) // Include the  customer details
            .build();
    }

    // Delete customer
    @DELETE
    @Path("/{custId}")
    public void deleteCustomer(@PathParam("custId") int custId) {
        logger.info("DELETE request to delete customer " + custId);

        //create boolean value and try to find customer by its id, if true, delete
        boolean removed = customers.removeIf(customer -> customer.getCustId() == custId);
        if (!removed) {
             //if boolean is not true/customer not found, print error message
            throw new CustomerNotFoundException("Customer " + custId + " not found. ");

        } else {
            logger.info("Deleted customer " + custId);
        }
    }

    // Generate a new customer ID
    private int nextCustomerId() {
        //set maxCustomerId to smallest integer. Then, look at all customer ids and store its value. 
        int maxCustId = Integer.MIN_VALUE;
        for (Customers customer : customers) {
            int custId = customer.getCustId();
            if (custId > maxCustId) {
                maxCustId = custId;
            }
        }
         // end of the loop, add +1 to the last found custId to generate the new customer id
        return maxCustId + 1;
    }
}
