package com.servicecops.project.repositories;

import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface GenericRepository<T> extends JetRepository<T, Long> {
}
