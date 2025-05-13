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
import com.example.model.Order;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Path("/customers/{custId}/orders")
public class OrderRss {

    private static final Logger logger = LoggerFactory.getLogger(OrderRss.class);

    //use hashmap, as customers might have more than one items inside order. 
    private static Map<Integer, List<Order>> orders = new HashMap<>();

    static {

        //access CustomerRss and BookRss to retrieve customers & books
        CustomerRss customerRss = new CustomerRss();
        List<Customers> customers = customerRss.getAllCustomers();
        BookRss booksRss = new BookRss();
        List<Books> books = booksRss.getAllBooks();

        orders.putIfAbsent(20000, new ArrayList<>());
        orders.get(20000).add(new Order(10, 20000, Arrays.asList(new Cart(10000005, booksRss.findBookById(10000005).getTitle(), 3))));

        orders.putIfAbsent(20006, new ArrayList<>());
        orders.get(20006).add(new Order(11, 20006, Arrays.asList(new Cart(10000009, booksRss.findBookById(10000009).getTitle(), 1))));

    }

    //create a new order by customer cart
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Order newOrder(@PathParam("custId") int custId) {
        logger.info("POST request to create new order for customer " + custId);

        //retrieve cart by customer id and all books. 
        List<Cart> cartList = CartRss.carts.get(custId);
        List<Books> bookList = BookRss.getAllBooksStatic();

        //generate new order no and set to the order. 
        int newOrderNo = nextOrderId();

        //check if cart is empty
        if (cartList == null || cartList.isEmpty()) {
            throw new CartNotFoundException("There is no cart for customer " + custId);
        }

        //create the new order with orderNo, custId and cart items
        Order newOrder = new Order(newOrderNo, custId, new ArrayList<>(cartList));

        //check all items in the cart
        for (Cart cartItem : cartList) {

            //get the book id and quantity from cart
            int bookIdFromCart = cartItem.getBookId();
            int quantityOrdered = cartItem.getQuantity();

            //check all books 
            for (Books book : bookList) {
                //if book id matches with the book id in the cart
                if (book.getBookId() == bookIdFromCart) {
                    int currentStock = book.getStock();
                    //check if stock is enough for the order and update stock with the quantity customer ordered and set as new stock. 
                    if (currentStock >= quantityOrdered) {
                        book.setStock(currentStock - quantityOrdered);

                        //save the order 
                        orders.putIfAbsent(custId, new ArrayList<>());
                        orders.get(custId).add(newOrder);

                    } else {
                        // if there is not enough stock, print error message. 
                        throw new OutOfStockException("There is not enough stock for this order.");
                    }
                }

            }
        }
        //clear customer's cart
        cartList.clear();

        logger.info("Created new order for customer " + custId + ", Order No: " + newOrderNo);
        return newOrder;

    }

    //get orders by customer id
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> getOrdersByCustomer(@PathParam("custId") int custId) {
        logger.info("GET request to retrieve orders by customer " + custId);

        //filter orders by custoemr id. get the first matching value and print. if not found, print error message. 
        List<Order> order = orders.entrySet().stream()
                .filter(entry -> entry.getKey() == custId) 
                .map(Map.Entry::getValue) 
                .findFirst() 
                .orElseThrow(() -> new OrderNotFoundException("Order not found for customer " + custId));

        logger.info("Order for customer " + custId);
        return order;

    }

    //get orders by order no , by customer id
    @GET
    @Path("/{orderNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Order getOrderById(@PathParam("custId") int custId, @PathParam("orderNo") int orderNo) {
        logger.info("GET request to retrieve order by id " + orderNo);

        List<Order> customerOrders = orders.get(custId);

        //filter orders by order no. get the first matching value and print. if not found, print error message. 
        return customerOrders.stream()
                .filter(order -> order.getOrderNo() == orderNo)
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderNo + " not found for customer: " + custId));

    }

    //generate new order no
    private int nextOrderId() {
        //set maxOrderNo to smallest integer. Then, look at all order no and store its value. 
        int maxOrderNo = Integer.MIN_VALUE;
        for (List<Order> customerOrders : orders.values()) {
            for (Order order : customerOrders) {
                int orderNo = order.getOrderNo();
                if (orderNo > maxOrderNo) {
                    maxOrderNo = orderNo;

                }

            }
        }
        //add +1 to the maxOrderNo to generate new order no.
        return maxOrderNo + 1;
    }

}
