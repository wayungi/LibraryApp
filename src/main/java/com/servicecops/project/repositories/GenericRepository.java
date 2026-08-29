package com.servicecops.project.repositories;

import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T> extends JetRepository<T, Long> {
}
