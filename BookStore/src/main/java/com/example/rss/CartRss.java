/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.rss;

/**
 *
 * @author gizem
 */
import com.example.exceptions.*;
import com.example.model.Books;
import com.example.model.Cart;
import com.example.model.Customers;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import javax.ws.rs.core.Response;

@Path("/customers/{custId}/cart")
public class CartRss {

    private static final Logger logger = LoggerFactory.getLogger(CartRss.class);

    //use hashmap, as customers might have more than one items in the cart. 
    static Map<Integer, List<Cart>> carts = new HashMap<>();

    static {
        //access BookRss to retrieve books
        BookRss booksRss = new BookRss();
        List<Books> books = booksRss.getAllBooks();

        //access CustomerRss to retrieve customers
        CustomerRss customerRss = new CustomerRss();
        List<Customers> customers = customerRss.getAllCustomers();

        carts.putIfAbsent(customerRss.findCustomerById(20000).getCustId(), new ArrayList<>());
        carts.putIfAbsent(customerRss.findCustomerById(20003).getCustId(), new ArrayList<>());
        carts.get(customerRss.findCustomerById(20000).getCustId()).add(new Cart(10000000, booksRss.findBookById(10000000).getTitle(), 2));
        carts.get(customerRss.findCustomerById(20003).getCustId()).add(new Cart(10000001, booksRss.findBookById(10000001).getTitle(), 1));
        carts.get(customerRss.findCustomerById(20003).getCustId()).add(new Cart(10000004, booksRss.findBookById(10000004).getTitle(), 3));
    }

    // Get cart items by customer id
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cart> getItemsByCustomerId(@PathParam("custId") int custId) {
        logger.info("GET request to retrieve cart by customer " + custId);

        //filter carts by customer id, find the first matching value and print. If cart not found, print error message. 
        List<Cart> cartList = carts.entrySet().stream()
                .filter(entry -> entry.getKey() == custId)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customer " + custId));

        //if cart is empty, print error message,
        if (cartList.isEmpty()) {
            throw new CartNotFoundException("Cart not found for customer " + custId);
        }

        return cartList;

    }

    // Add item to cart
    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    public Cart addItem(@PathParam("custId") int custId, Cart cartItem) {
        logger.info("POST request to add item to cart for customer " + custId);

        // access BooksRss to get book details
        BookRss booksRss = new BookRss();

        // check if the book exists
        Books book = booksRss.findBookById(cartItem.getBookId());

        //if not, print error message. 
        if (book == null) {
            throw new BookNotFoundException("Book " + cartItem.getBookId() + " is not in the system. Cannot add to cart.");
        }

        //check if book id is less than 10000000, if yes, print error message. 
        if (book.getBookId() < 10000000) {
            throw new InvalidInputException(" Book id cannot be less than 10000000.");
        }

        // assign cart to customer
        carts.putIfAbsent(custId, new ArrayList<>());
        List<Cart> cartList = carts.get(custId);

        //check the how many books customer added to cart, checking with bookId and find the total. 
        int currentQuantity = cartList.stream()
                .filter(item -> item.getBookId() == cartItem.getBookId())
                .mapToInt(Cart::getQuantity)
                .sum();

        //sum how many books customer has in cart and how many they want to add, set a new value 
        int requestedQuantity = currentQuantity + cartItem.getQuantity();

        //if stock is less than requested amount, print error message. 
        if (book.getStock() < requestedQuantity) {
            throw new OutOfStockException("Book " + cartItem.getBookId() + " is out of stock or insufficient stock available.");
        }

        // check if the item already exists in the cart
        for (Cart existingItem : cartList) {
            if (existingItem.getBookId() == cartItem.getBookId()) {

                // if it already exists, update the quantity of the existing item
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                logger.info("Updated quantity for book {} in cart for customer {}", cartItem.getBookId(), custId);
                return existingItem;
            }
        }

        // check the quantity if its less than 1, if yes, print error message. 
        cartItem.setBookName(book.getTitle());
        if (cartItem.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity cannot be less than 1. ");
        } //check if book title is empty, if yes, print error message. 
        else if (cartItem.getBookName() == null || cartItem.getBookName().trim().isEmpty()) {
            throw new InvalidInputException("Title is missing.");
        }

        //add the new items in cart. 
        cartList.add(cartItem);

        logger.info("Added new item to cart for customer " + custId);
        return cartItem;
    }

    // Update cart item with book id
    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCart(@PathParam("custId") int custId, @PathParam("bookId") int bookId, Cart updatedCart) {
        logger.info("PUT request to update cart for customer " + custId);

        //check if cart exists by customerid
        List<Cart> cartList = carts.get(custId);

        //if there is nothing on the cart, print error messaage. 
        if (cartList == null || cartList.isEmpty()) {
            throw new CartNotFoundException("Cart not found for customer " + custId);
        }

        // check if the updatedCart is null OR quantity is less than zero 
        if (updatedCart == null || updatedCart.getQuantity() <= 0) {
            throw new CartNotFoundException("Updated cart is empty for customer " + custId);
        }

        // check if the cart item exists , if yes, print the first cart item. if not, print error message. 
        Cart existingItem = cartList.stream()
                .filter(item -> item.getBookId() == bookId)
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Book ID " + bookId + " not found in cart for customer ID: " + custId));

        // Update the quantity, keep title & id same
        existingItem.setQuantity(updatedCart.getQuantity());

        logger.info("Cart item updated for customer: " + custId + ", book ID: " + bookId);
        
        //print 201-Created code with details of the  new
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(existingItem) // Include the updated cart item details
            .build();

    }

    // Delete item from cart
    @DELETE
    @Path("/items/{bookId}")
    public void deleteItem(@PathParam("custId") int custId, @PathParam("bookId") int bookId) {
        logger.info("DELETE request to delete cart by customer " + custId);

        //retrieve cart
        List<Cart> cartList = carts.get(custId);

        //check if cart exists or is empty
        if (cartList == null || cartList.isEmpty()) {
            throw new CartNotFoundException("Cart not found for customer " + custId);
        }

        //create a boolean value and try to find the item by its id, if true, delete        
        boolean removed = cartList.removeIf(cartItem -> cartItem.getBookId() == bookId);

        if (!removed) {
            //if boolean is not true/book not found, print error message
            throw new CartNotFoundException("Book " + bookId + " not found in cart for customer " + custId);
        } else {

            logger.info("Deleted item from cart \nbook: " + bookId + "  \ncustomer " + custId);
        }
    }

}
