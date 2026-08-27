package com.servicecops.project.services.libservices;

import com.servicecops.project.models.entities.Librarian;
import org.springframework.stereotype.Service;

@Service
public class LibrarianService extends CrudService<Librarian>{
    protected LibrarianService() {
        super(Librarian.class);
    }
}
