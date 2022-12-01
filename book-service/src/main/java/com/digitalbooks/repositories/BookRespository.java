package com.digitalbooks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalbooks.entities.Book;

@Repository
public interface BookRespository extends JpaRepository<Book, Long>{

	Boolean existsByAuthorIdAndTitle(int authorId, String title);
	
}
