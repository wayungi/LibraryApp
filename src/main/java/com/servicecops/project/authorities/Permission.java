package com.servicecops.project.authorities;

import lombok.Getter;

@Getter
public enum Permission {

    BOOK_CREATE("Allows creating new books."),
    BOOK_READ("Allows viewing books."),
    BOOK_UPDATE("Allows updating book information."),
    BOOK_DELETE("Allows deleting books."),

    MEMBER_CREATE("Allows registering new library members."),
    MEMBER_READ("Allows viewing member information."),
    MEMBER_UPDATE("Allows updating member information."),
    MEMBER_DELETE("Allows deleting library members."),

    BORROW_BOOK("Allows borrowing books."),
    VIEW_BORROWED_BOOK("Allows viewing borrowed books."),
    RETURN_BOOK("Allows returning borrowed books."),
    VIEW_OVERDUE_LOANS("Allows viewing overdue loans.");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

}