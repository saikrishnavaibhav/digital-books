package com.digitalbooks.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digitalbooks.entities.Book;

@Repository
public interface BookRespository extends JpaRepository<Book, Long>{

	Boolean existsByAuthorIdAndTitle(Long authorId, String title);
	
	Boolean existsByAuthorIdAndId(Long authorId, Long id );
	
	List<Book> findAllByAuthorId(Long authorId);
	
	@Query("select books from Book books where books.category = :category or books.title= :title or books.authorName=:author")
	List<Book> findBooksByCategoryOrTitleOrAuthor(String category, String title, String author);
}
