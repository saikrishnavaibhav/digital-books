package com.digitalbooks.books;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.digitalbooks.entities.Book;
import com.digitalbooks.repositories.BookRespository;
import com.digitalbooks.requests.BookRequest;
import com.digitalbooks.responses.MessageResponse;

@Service
public class BooksService {

	@Autowired
	BookRespository bookRespository;
	
	private static final Logger logger = LoggerFactory.getLogger(BooksService.class);
	
	public ResponseEntity<?> saveBook(BookRequest bookRequest, Long authorId) {
		Book book = generateBook(bookRequest);
		
		try {
			if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndTitle(authorId, book.getTitle()))) {
				logger.info("same title for author : {} exists", authorId);
				return ResponseEntity.badRequest().body("Book with same title exists!");
			}
			book.setPublishedDate(Timestamp.valueOf(LocalDateTime.now()));
			book = bookRespository.save(book);
			logger.info("saved book : {}",book);
		} catch (Exception exception) {
			logger.error(exception.getLocalizedMessage());
			return ResponseEntity.internalServerError().body("Error: "+exception.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Cacheable("book")
	public Book getBook(Long bookId) {
		
		Optional<Book> book = bookRespository.findById(bookId);
		if(book.isPresent())
			return book.get();
		return null;
	}
	
	public List<Book> getAllSubscribedBooks(List<Long> bookIds){
		
		List<Book> allSubscribedBooks = bookRespository.findAllById(bookIds);
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
				logger.info("book {} updated to active status: {}", bookId, !block);
				return true;
			} else 
				return false;
		} else
			return false;
		
	}

	public boolean updateBook(BookRequest bookRequest, Long bookId, Long authorId) {
		Book book = generateBook(bookRequest);
		
		if(Boolean.TRUE.equals(bookRespository.existsByAuthorIdAndId(authorId, bookId))) {
			Book existedBook = getBook(bookId);
			existedBook.setCategory(book.getCategory());
			existedBook.setContent(book.getContent());
			existedBook.setPrice(book.getPrice());
			existedBook.setPublisher(book.getPublisher());
			existedBook.setTitle(book.getTitle());
			logger.info("updating book: {}", bookId);
			bookRespository.save(existedBook);
			return true;
		}
		logger.info("invalid bookid: {} for author: {}", bookId, authorId);
		return false;
	}

	private Book generateBook(BookRequest bookRequest) {
		Book book = new Book();
		book.setActive(bookRequest.isActive());
		book.setAuthorId(bookRequest.getAuthorId());
		book.setAuthorName(bookRequest.getAuthorName());
		book.setCategory(bookRequest.getCategory());
		book.setContent(bookRequest.getContent());
		book.setLogo(bookRequest.getLogo());
		book.setPrice(bookRequest.getPrice());
		book.setPublishedDate(bookRequest.getPublishedDate());
		book.setPublisher(bookRequest.getPublisher());
		book.setTitle(bookRequest.getTitle());
		return book;
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
		logger.info("author : {}",author);
		logger.info("category : {}",category);
		logger.info("title : {}",title);
		List<Book> books = new ArrayList<>();
		if(ObjectUtils.isEmpty(category) && ObjectUtils.isEmpty(title) && !ObjectUtils.isEmpty(author))
			books =  bookRespository.findBooksByAuthorName(author);
		else if(ObjectUtils.isEmpty(category) && ObjectUtils.isEmpty(author) && !ObjectUtils.isEmpty(title))
			books = bookRespository.findBooksByBookTitle(title);
		else if(ObjectUtils.isEmpty(title) && ObjectUtils.isEmpty(author) && !ObjectUtils.isEmpty(category))
			books =  bookRespository.findBooksByBookCategory(category);
		else if(!ObjectUtils.isEmpty(title) && !ObjectUtils.isEmpty(author) && !ObjectUtils.isEmpty(category))
			books = bookRespository.findBooksByCategoryOrTitleOrAuthor(category, title, author);
		
		return books;
	}

	public List<Book> getAuthorBooks(Long authorId) {
		List<Book> books = bookRespository.findAllByAuthorId(authorId);
		logger.info("books of author: {}",books);
		return books;
	}
}
