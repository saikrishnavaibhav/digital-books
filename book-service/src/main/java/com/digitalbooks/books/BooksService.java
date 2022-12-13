package com.digitalbooks.books;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.digitalbooks.entities.Book;
import com.digitalbooks.repositories.BookRespository;
import com.digitalbooks.responses.MessageResponse;

@Service
public class BooksService {

	@Autowired
	BookRespository bookRespository;
	
	private static final Logger logger = LoggerFactory.getLogger(BooksService.class);
	
	public MessageResponse saveBook(Book book, Long authorId) {
		
		try {
			if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndTitle(authorId, book.getTitle()))) {
				return new MessageResponse("Book with same title exists!");
			}
			bookRespository.save(book);
		} catch (Exception exception) {
			return new MessageResponse("Error: "+exception.getMessage());
		}
		
		return new MessageResponse("Book added successfully!");
	}
	
	public List<Book> getBooks(){
		List<Book> books = bookRespository.findAll();
		logger.info("retrieved books : {}", books);
		return books;
	}
	
	@Cacheable("book")
	public Book getBook(Long bookId) {
		
		Optional<Book> book = bookRespository.findById(bookId);
		if(book.isPresent())
			return book.get();
		return null;
	}
	
	public List<Book> getAllSubscribedBooks(List<Long> bookIds){
		
		List<Book> allSubscribedBooks  = bookRespository.findAllById(bookIds);
		return allSubscribedBooks.stream().filter(Book::getActive).collect(Collectors.toList());
		
	}

	public boolean blockBook(Long authorId, Long bookId, boolean block) {
		if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndId(authorId, bookId))) {
			Book book = getBook(bookId);
			if(book.getActive() != block)
				return false;
			book.setActive(!block);

			Book book1 = bookRespository.save(book);
			if(book1.getActive() != block) {
				logger.info("book updated to active status: {}", block);
				return true;
			} else 
				return false;
		} else
			return false;
		
	}

	public boolean updateBook(Book book, Long bookId, Long authorId) {
		if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndId(authorId, bookId))) {
			Book existedBook = getBook(bookId);
			existedBook.setCategory(book.getCategory());
			existedBook.setContent(book.getContent());
			existedBook.setPrice(book.getPrice());
			existedBook.setPublisher(book.getPublisher());
			existedBook.setTitle(book.getTitle());
			existedBook.setLogo(book.getLogo());
			
			bookRespository.save(existedBook);
			return true;
		}
		return false;
	}

	public MessageResponse readBook(Long bookId) {
		if(bookRespository.existsById(bookId)) {
			Book book = getBook(bookId);
			if(book.getActive()) {
				return new MessageResponse(book.getContent());
			}
		}
		return new MessageResponse("Invalid Request");
	}

	public List<Book> searchBooks(String category, String title, String author) {
		List<Book> books = bookRespository.findBooksByCategoryOrTitleOrAuthor(category, title, author);
		logger.info("books from search: {}",books);
		return books;
	}

	public List<Book> getAuthorBooks(Long authorId) {
		List<Book> books = bookRespository.findAllByAuthorId(authorId);
		logger.info("books of author: {}",books);
		return books;
	}
}
