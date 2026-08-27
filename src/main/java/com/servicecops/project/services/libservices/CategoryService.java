package com.servicecops.project.services.libservices;

import com.servicecops.project.models.entities.BookInformation;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends CrudService<BookInformation>{
    protected CategoryService() {
        super(BookInformation.class);
    }
}
