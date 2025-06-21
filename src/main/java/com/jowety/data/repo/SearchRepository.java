package com.jowety.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.jowety.data.query.Search;

@NoRepositoryBean
public interface SearchRepository<T, ID> extends JpaRepository<T, ID>{

	public List<T> search(Search<T> search);
}
