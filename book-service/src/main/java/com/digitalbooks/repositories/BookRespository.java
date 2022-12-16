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
	
	@Query("select books from Book books where books.category LIKE %:category% or books.title LIKE %:title% or books.authorName LIKE %:author%")
	List<Book> findBooksByCategoryOrTitleOrAuthor(String category, String title, String author);
	
	@Query("select books from Book books where books.authorName LIKE %:author%")
	List<Book> findBooksByAuthorName(String author);
	
	@Query("select books from Book books where books.title LIKE %:title%")
	List<Book> findBooksByBookTitle(String title);
	
	@Query("select books from Book books where books.category LIKE %:category%")
	List<Book> findBooksByBookCategory(String category);
}
