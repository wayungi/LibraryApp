package com.servicecops.project.authorities;

import lombok.Getter;

import java.util.Set;

@Getter
public enum Role {

    ADMIN(
            "System administrator",
            Set.of(
                    Permission.BOOK_CREATE,
                    Permission.BOOK_READ,
                    Permission.BOOK_UPDATE,
                    Permission.BOOK_DELETE,

                    Permission.MEMBER_CREATE,
                    Permission.MEMBER_READ,
                    Permission.MEMBER_UPDATE,
                    Permission.MEMBER_DELETE,

                    Permission.BORROW_BOOK,
                    Permission.VIEW_BORROWED_BOOK,
                    Permission.RETURN_BOOK,
                    Permission.VIEW_OVERDUE_LOANS
            )
    ),

    LIBRARIAN(
            "Manages books, members and borrowing operations",
            Set.of(
                    Permission.BOOK_CREATE,
                    Permission.BOOK_READ,
                    Permission.BOOK_UPDATE,

                    Permission.MEMBER_CREATE,
                    Permission.MEMBER_READ,
                    Permission.MEMBER_UPDATE,

                    Permission.BORROW_BOOK,
                    Permission.VIEW_BORROWED_BOOK,
                    Permission.RETURN_BOOK,
                    Permission.VIEW_OVERDUE_LOANS
            )
    ),

    LIBRARY_ASSISTANT(
            "Assists with basic library operations",
            Set.of(
                    Permission.BOOK_READ,
                    Permission.MEMBER_READ,

                    Permission.BORROW_BOOK,
                    Permission.VIEW_BORROWED_BOOK,
                    Permission.RETURN_BOOK
            )
    ),

    MEMBER(
            "Library member",
            Set.of(
                    Permission.BOOK_READ,
                    Permission.VIEW_BORROWED_BOOK
            )
    );

    private final String description;
    private final Set<Permission> permissions;

    Role(String description, Set<Permission> permissions) {
        this.description = description;
        this.permissions = permissions;
    }

}