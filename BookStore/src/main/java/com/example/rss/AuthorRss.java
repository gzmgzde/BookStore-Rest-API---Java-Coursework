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
import com.example.model.Authors;
import com.example.model.Books;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/authors")
public class AuthorRss {

    private static final Logger logger = LoggerFactory.getLogger(AuthorRss.class);

    private static List<Authors> authors = new ArrayList<>();

    //access BookRss to retrieve books
    BookRss booksRss = new BookRss();
    List<Books> books = booksRss.getAllBooks();

    static {

        authors.add(new Authors("Jane Austen", "Renowned English novelist known for romantic fiction like Pride and Prejudice, exploring love and class in 19th-century England.", 1000));
        authors.add(new Authors("Herman Melville", "American author best known for Moby-Dick, exploring themes of obsession and humanity's struggle with nature.", 1001));
        authors.add(new Authors("Leo Tolstoy", "Russian novelist of War and Peace and Anna Karenina, known for deep philosophical and moral exploration.", 1002));
        authors.add(new Authors("Charlotte Brontë", "English novelist best known for Jane Eyre, focusing on themes of gender, class, and personal strength.", 1003));
        authors.add(new Authors("Oscar Wilde", "Irish writer of The Picture of Dorian Gray and witty plays, known for his sharp humor and critique of Victorian society.", 1004));
        authors.add(new Authors("George Orwell", "British author of dystopian novels like 1984 and Animal Farm, known for political satire and criticism of totalitarianism.", 1005));
        authors.add(new Authors("Harper Lee", "American novelist widely known for To Kill a Mockingbird, which addresses racial injustice in the Deep South.", 1006));
        authors.add(new Authors("F. Scott Fitzgerald", "American author of The Great Gatsby, capturing the Jazz Age and the American Dream’s disillusionment.", 1007));
        authors.add(new Authors("Fyodor Dostoevsky", "Russian novelist known for Crime and Punishment and The Brothers Karamazov, exploring psychology and existentialism.", 1008));
        authors.add(new Authors("Mary Shelley", "English writer and pioneer of science fiction, best known for Frankenstein.", 1009));
        authors.add(new Authors("Emily Brontë", "English author of Wuthering Heights, a tale of passionate, doomed love set on the Yorkshire moors.", 1010));
        authors.add(new Authors("Aldous Huxley", "British writer and philosopher best known for Brave New World, a dystopian novel critiquing loss of individuality.", 1011));
        authors.add(new Authors("Victor Hugo", "French poet and novelist, author of Les Misérables and The Hunchback of Notre-Dame, known for his social conscience.", 1012));
        authors.add(new Authors("Bram Stoker", "Irish author best known for Dracula, which defined the modern vampire genre.", 1013));

    }

    // Get all authors
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Authors> getAllAuthors() {
        logger.info("GET request to retrieve all authors. ");

        return authors;
    }

    // Get author by their ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Authors findAuthorById(@PathParam("id") int id) {
        logger.info("GET request to retrieve authors by id " + id);

        //filter authors by their id. get the first matching value and print. if not found, print error message. 
        return authors.stream()
                .filter(entry -> entry.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AuthorNotFoundException("Author " + id + " not found. "));

    }

    // Add new author
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAuthor(Authors author) {
        logger.info("POST request to add authors ");

        //generates new author id and set as id
        int newAuthorId = nextAuthorId();
        author.setId(newAuthorId);

        //check if biography is empty, if yes, print error message. 
        if (author.getBiography() == null || author.getBiography().trim().isEmpty()) {
            throw new InvalidInputException("Author biography is missing.");
        } //check if author name is empty, if yes, print error message. 
        else if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new InvalidInputException("Author name is missing.");
        }

        //add book
        authors.add(author);

        logger.info("Added new author  " + newAuthorId);
        
        //print 201-Created code with details of the added author
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(author) // Include the  author details
            .build();
    }

    // Update author details
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAuthor(@PathParam("id") int id, Authors updatedAuthor) {
        logger.info("PUT request to update authors by id " + id);

        //check if author exists and find by its id, if not, print error messagge
        Authors existingAuthor = authors.stream()
                .filter(author -> author.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AuthorNotFoundException("Author " + id + " not found to update."));

        //check if author name is empty, if yes, print error message. 
        if (updatedAuthor.getName() == null || updatedAuthor.getName().trim().isEmpty()) {
            throw new InvalidInputException(" Author name is missing.");
        } //check if biography is empty, if yes, print error message. 
        else if (updatedAuthor.getBiography() == null || updatedAuthor.getBiography().trim().isEmpty()) {
            throw new InvalidInputException(" Author biography is missing.");
        } //check if author id is less than 1000, if yes, print error message. 
        else if (updatedAuthor.getId() < 1000) {
            throw new InvalidInputException(" Author id cannot be less than 1000.");
        }

        // set updated author as its id
        updatedAuthor.setId(id);

        // for loop to get author
        for (int i = 0; i < authors.size(); i++) {
            Authors author = authors.get(i);

            //if authhor id is same as updatedAuthor id, update details for that author
            if (author.getId() == updatedAuthor.getId()) {
                authors.set(i, updatedAuthor);

                logger.info("Updated information for author " + id);
                
            }
        }
        //print 201-Created code with details of the updated author
        return Response.status(Response.Status.CREATED) // 201 Created
            .entity(updatedAuthor) // Include the updated author details
            .build();
    }

    // Delete author
    @DELETE
    @Path("/{id}")
    public void deleteAuthor(@PathParam("id") int id) {
        logger.info("DELETE request to delete author " + id);

        //create a boolean value and try to find the author by its id, if true, delete
        boolean removed = authors.removeIf(author -> author.getId() == id);

        if (!removed) {
            //if boolean is not true/author not found, print error message
            throw new AuthorNotFoundException("Author " + id + " not found. ");

        } else {
            logger.info("Deleted author " + id);
        }
    }

    // Get all books by author ID
    @GET
    @Path("/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Books> getBookByAuthorID(@PathParam("id") int id) {
        logger.info("GET request to retrieve all books by author id " + id);

        //check if author exists and find by its id
        Authors author = findAuthorById(id);

        //if author does not exist/null, print error message
        if (author == null) {
            throw new AuthorNotFoundException("Author " + id + " not found. ");
        }

        //get the name of the author
        String authorName = author.getName();

        //check all the books and check if its matching with the author (in the books, i have used/stored author names, therefore, i am checking author names)
        //find the all matching values and print. 
        //i couldn't use the stream() method, because it was printing first value only(findFirst), also if i remove that, i couldnt use orElseThrow. so i needed to change to for loop. 
        List<Books> booksByAuthor = new ArrayList<>();
        for (Books book : BookRss.getAllBooksStatic()) {
            if (book.getAuthor() != null && book.getAuthor().equalsIgnoreCase(authorName)) {
                booksByAuthor.add(book);
            }
        }
        //if list is empty/no books found, print error message. 
        if (booksByAuthor.isEmpty()) {
            throw new BookNotFoundException("No books found for author " + id);
        }

        logger.info("Book found for author: " + id);
        return booksByAuthor;
    }

    // Generate new author ID
    private int nextAuthorId() {
        //set maxAuthorId to smallest integer. Then, look at all author ids and store its value. 
        int maxAuthorId = Integer.MIN_VALUE;
        for (Authors author : authors) {
            int id = author.getId();
            if (id > maxAuthorId) {
                maxAuthorId = id;
            }
        }
        //add +1 to the maxAuthorId to generate new author id. 
        return maxAuthorId + 1;
    }
}
