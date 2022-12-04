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
	
	@Query("select books from Book books where books.category = :category and books.title= :title and books.authorName=:author and books.price< :price and books.publisher= :publisher")
	List<Book> findBooksByCategoryAndTitleAndAuthorAndPriceAndPublisher(String category, String title, String author, Long price, String publisher);
}
