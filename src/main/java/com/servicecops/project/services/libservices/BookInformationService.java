package com.servicecops.project.services.libservices;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.entities.Book;
import com.servicecops.project.models.entities.BookInformation;
import com.servicecops.project.repositories.BookInformationRepo;
import com.servicecops.project.repositories.BookRepository;
import com.servicecops.project.utils.OperationReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookInformationService extends CrudService<BookInformation>{
    @Autowired
    private BookInformationRepo bookInformationRepo;
    @Autowired
    private BookRepository bookRepo;

    protected BookInformationService() {
        super(BookInformation.class);
    }

    // parent save creates new category and new books

    // add single book.
    public OperationReturnObject saveOne(JSONObject request) {
        requires(List.of("category", "body"), request);
        String category = request.getString("category");
        String bookEntity = request.getString("body");

        BookInformation bookInformation = bookInformationRepo.findByCategory(category)
                .orElse(null);

        if (bookInformation == null) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "No book information found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        Book newBook = JSON.parseObject(bookEntity, Book.class);

        bookInformation.getBooks().add(newBook);

        BookInformation saved = bookInformationRepo.save(bookInformation);

        operationReturnObject.setReturnObject(saved);
        return operationReturnObject;
    }

    // add multiple books to existing category.
    public OperationReturnObject saveMultiple(JSONObject request) {
        requires(List.of("category", "body"), request);
        String category = request.getString("category");
        String bookEntity = request.getString("body");

        BookInformation bookInformation = bookInformationRepo.findByCategory(category)
                .orElse(null);

        if (bookInformation == null) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "No book information found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        List<Book> newBooks = JSON.parseArray(bookEntity, Book.class);

        if (newBooks == null || newBooks.isEmpty()) {
            operationReturnObject.setReturnCode(400);
            operationReturnObject.setReturnMessage("No books provided");
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        bookInformation.getBooks().addAll(newBooks);
        BookInformation saved = bookInformationRepo.save(bookInformation);
        operationReturnObject.setReturnObject(saved);
        return operationReturnObject;
    }

    // delete single book in a category.
    public OperationReturnObject deleteOne(JSONObject request) {

        requires(List.of("category", "isbn"), request);

        String category = request.getString("category");
        String isbn = request.getString("isbn");

        BookInformation bookInformation = bookInformationRepo.findByCategory(category)
                .orElse(null);

        if (bookInformation == null) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "No book information found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        boolean removed = bookInformation.getBooks()
                .removeIf(book -> isbn.equals(book.getIsbn()));

        if (!removed) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "No book with ISBN " + isbn +
                            " found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        BookInformation saved = bookInformationRepo.save(bookInformation);

        operationReturnObject.setReturnCode(200);
        operationReturnObject.setReturnMessage("Book removed successfully");
        operationReturnObject.setReturnObject(saved);

        return operationReturnObject;
    }

    // delete multiple books in the same category.

    public OperationReturnObject deleteMultiple(JSONObject request) {

        requires(List.of("category", "isbn"), request);

        String category = request.getString("category");

        List<String> isbns = JSON.parseArray(
                request.getString("isbn"),
                String.class
        );

        if (isbns == null || isbns.isEmpty()) {
            operationReturnObject.setReturnCode(400);
            operationReturnObject.setReturnMessage("No ISBN numbers provided");
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        BookInformation bookInformation = bookInformationRepo.findByCategory(category)
                .orElse(null);

        if (bookInformation == null) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "No book information found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        Set<String> isbnSet = new HashSet<>(isbns);

        boolean removed = bookInformation.getBooks()
                .removeIf(book -> isbnSet.contains(book.getIsbn()));

        if (!removed) {
            operationReturnObject.setReturnCode(404);
            operationReturnObject.setReturnMessage(
                    "None of the provided books were found in category " + category
            );
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }

        BookInformation saved = bookInformationRepo.save(bookInformation);

        operationReturnObject.setReturnCode(200);
        operationReturnObject.setReturnMessage("Books removed successfully");
        operationReturnObject.setReturnObject(saved);

        return operationReturnObject;
    }

    public OperationReturnObject upsert(JSONObject request) {

        requires("body", request);
        JSONObject body = request.getJSONObject("body");

        requires("category", body);
        String category = body.getString("category");

        BookInformation incomingBookInformation =
                body.toJavaObject(BookInformation.class);

        BookInformation bookInformation = null;

        // find using category.
        bookInformation = bookInformationRepo.findByCategory(category)
                .orElse(null);

        if (bookInformation != null) {

            // update fields
            bookInformation.setCategory(category);
            bookInformation.setPublisher(body.getString("publisher"));
            bookInformation.setPublicationYear(body.getInteger("publicationYear"));
            bookInformation.setAuthorsName(body.getString("authorsName"));

            Set<Book> dbBooks = bookInformation.getBooks();
            Set<Book> newBooks = incomingBookInformation.getBooks();

            for (Book newBook : newBooks) {

                boolean found = false;

                for (Book dbBook : dbBooks) {

                    if (newBook.getIsbn().equals(dbBook.getIsbn())) {

                        dbBook.setIsbn(newBook.getIsbn());
                        dbBook.setStatus(newBook.getStatus());
                        found = true;
                        break;
                    }
                }
                // add book if it does not exist
                if (!found) {
                    dbBooks.add(newBook);
                }
            }
            // assign updated bookSet to bookInformation and save to db.
            bookInformation.setBooks(dbBooks);
       operationReturnObject.setReturnObject(bookInformationRepo.save(bookInformation));

        } else {
            operationReturnObject.setReturnObject( bookInformationRepo.save(incomingBookInformation));
        }
        operationReturnObject.setReturnCode(0);
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch(action){
            case "saveOne"-> saveOne(request);
            case "saveMultiple"-> saveMultiple(request);
            case "deleteOne"-> deleteOne(request);
            case "deleteMultiple"-> deleteMultiple(request);
            case "upsert"-> upsert(request);
            default ->   super.switchActions(action, request);
        };
    }
    }




