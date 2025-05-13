/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.rss;

/**
 *
 * @author gizem
 */
import com.example.model.Authors;
import com.example.model.Books;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.exceptions.*;
import javax.ws.rs.core.Response;

@Path("/books")
public class BookRss {

    private static final Logger logger = LoggerFactory.getLogger(BookRss.class);

    private static List<Books> books = new ArrayList<>();

    static {
        //access AuthorRss to retrieve authors
        AuthorRss authorRss = new AuthorRss();
        List<Authors> author = authorRss.getAllAuthors();

        // add books to list
        books.add(new Books("Pride and Prejudice", authorRss.findAuthorById(1000).getName(), 1813, "9780141199078", 9.99, 1, 10000000));
        books.add(new Books("Moby-Dick", authorRss.findAuthorById(1001).getName(), 1851, "9781503280786", 11.50, 18, 10000001));
        books.add(new Books("War and Peace", authorRss.findAuthorById(1002).getName(), 1869, "9780199232765", 14.99, 12, 10000002));
        books.add(new Books("Jane Eyre", authorRss.findAuthorById(1003).getName(), 1847, "9780141441146", 8.49, 30, 10000003));
        books.add(new Books("The Picture of Dorian Gray", authorRss.findAuthorById(1004).getName(), 1890, "9780141439570", 7.95, 22, 10000004));
        books.add(new Books("1984", authorRss.findAuthorById(1005).getName(), 1949, "9780451524935", 10.99, 0, 10000005));
        books.add(new Books("To Kill a Mockingbird", authorRss.findAuthorById(1006).getName(), 1960, "9780061120084", 7.19, 15, 10000006));
        books.add(new Books("The Great Gatsby", authorRss.findAuthorById(1007).getName(), 1925, "9780743273565", 10.29, 0, 10000007));
        books.add(new Books("Crime and Punishment", authorRss.findAuthorById(1008).getName(), 1866, "9780486415871", 12.49, 8, 10000008));
        books.add(new Books("Frankenstein", authorRss.findAuthorById(1009).getName(), 1818, "9780486282114", 6.95, 5, 10000009));
        books.add(new Books("Wuthering Heights", authorRss.findAuthorById(1010).getName(), 1847, "9780141439556", 8.95, 0, 10000010));
        books.add(new Books("Brave New World", authorRss.findAuthorById(1011).getName(), 1932, "9780060850524", 9.99, 13, 10000011));
        books.add(new Books("Les Misérables", authorRss.findAuthorById(1012).getName(), 1862, "9780451419439", 13.50, 10, 10000012));
        books.add(new Books("The Brothers Karamazov", authorRss.findAuthorById(1008).getName(), 1880, "9780374528379", 14.95, 16, 10000013));
        books.add(new Books("Dracula", authorRss.findAuthorById(1013).getName(), 1897, "9780486411095", 7.50, 20, 10000014));

    }

    // Get all books
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Books> getAllBooks() {
        logger.info("GET request to retrieve all books. ");
        return books;
    }

    //get all books - static (in order to access from AuthorRss)
    public static List<Books> getAllBooksStatic() {
        return books;
    }

    // Get book by its ID
    @GET
    @Path("/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Books findBookById(@PathParam("bookId") int bookId) {
        logger.info("GET request to retrieve book by id " + bookId);

        //filter books by their id, find the first matching value and print. If book not found, print error message. 
        return books.stream()
                .filter(entry -> entry.getBookId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book " + bookId + " not found. "));

    }

    // Add new book
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBook(Books book) {
        logger.info("POST request to add book. ");

        //generate new book id and set as id
        int newBookId = nextBookId();
        book.setBookId(newBookId);

        //check if author is empty, if yes, print error message. 
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidInputException("Author is missing.");
        } //check if isbn is empty, if yes, print error message. 
        else if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new InvalidInputException("ISBN is missing.");
        } //check if price is less/equal than zero, if yes, print error message. 
        else if (book.getPrice() <= 0) {
            throw new InvalidInputException("Price cannot be less/equal than 0.");
        } //check if publication year is less/equal than zero, if yes, print error message. 
        else if (book.getPublicationYear() < 0 || book.getPublicationYear() > 2025) {
            throw new InvalidInputException("Publication year cannot be less than 0 OR in future");
        } //check if title is empty, if yes, print error message. 
        else if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title is missing.");
        } //check if stock is less than 0, if yes, print error message. 
        else if (book.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be less than 0.");
        }

        //add book
        books.add(book);

        logger.info("Added new Book  " + newBookId);
        //print 201-Created code with details of the added book
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(book) // Include the created book details
            .build();
    }

    // Update book details
    @PUT
    @Path("/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("bookId") int bookId, Books updatedBook) {
        logger.info("PUT request to update book by id " + bookId);

        //check to find the book by its id, if not found, print errr message
        Books existingBook = books.stream()
                .filter(book -> book.getBookId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book " + bookId + " not found to update."));

        //check if author is empty, if yes, print error message. 
        if (updatedBook.getAuthor() == null || updatedBook.getAuthor().trim().isEmpty()) {
            throw new InvalidInputException("Author is missing.");
        } //check if isbn is empty, if yes, print error message. 
        else if (updatedBook.getIsbn() == null || updatedBook.getIsbn().trim().isEmpty()) {
            throw new InvalidInputException("ISBN is missing.");
        } //check if price is less/equal than zero, if yes, print error message. 
        else if (updatedBook.getPrice() <= 0) {
            throw new InvalidInputException("Price cannot be less/equal than 0.");
        } //check if publication year is less/equal than zero, if yes, print error message. 
        else if (updatedBook.getPublicationYear() < 0 || updatedBook.getPublicationYear() > 2025) {
            throw new InvalidInputException("Publication year cannot be less than 0 OR in future");
        } //check if title is empty, if yes, print error message. 
        else if (updatedBook.getTitle() == null || updatedBook.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Title is missing.");
        } //check if stock is less than 0, if yes, print error message. 
        else if (updatedBook.getStock() < 0) {
            throw new InvalidInputException("Stock cannot be less than 0.");
        } //check if book id is less than 10000000, if yes, print error message. 
        else if (updatedBook.getBookId() < 10000000) {
            throw new InvalidInputException(" Book id cannot be less than 10000000.");
        }

        // set the book id for the updated book
        updatedBook.setBookId(bookId);

        //for loop to get book
        for (int i = 0; i < books.size(); i++) {
            Books book = books.get(i);

            //if book id is same as updatedBook id, update details for that book
            if (book.getBookId() == updatedBook.getBookId()) {
                books.set(i, updatedBook);

                logger.info("Updated information for book  " + bookId);
                
            }
        }
        //print 201-Created code with details of the added book
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(updatedBook) // Include the updated book details
            .build();

    }

    // Delete book
    @DELETE
    @Path("/{bookId}")
    public void deleteBook(@PathParam("bookId") int bookId) {
        logger.info("DELETE request to delete book " + bookId);

        //create a boolean value and try to find the book by its id, if true, delete
        boolean removed = books.removeIf(book -> book.getBookId() == bookId);
        if (!removed) {
            //if boolean is not true/book not found, print error message
            throw new BookNotFoundException("Book " + bookId + " not found to delete. ");

        } else {
            logger.info("Deleted book " + bookId);
            
        }

    }

    // Generate new book ID
    private int nextBookId() {
        //set maxBookId to smallest integer. Then, look at all book ids and store its value. 
        int maxBookId = Integer.MIN_VALUE;
        for (Books book : books) {
            int bookId = book.getBookId();
            if (bookId > maxBookId) {
                maxBookId = bookId;
            }
        }
        // end of the loop, add +1 to the last found bookId to generate the new book id
        return maxBookId + 1;
    }
}
