package com.digitalbooks.books;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	
	public MessageResponse saveBook(Book book, Long authorId) {
		
		try {
			if(bookRespository.existsByAuthorIdAndTitle(authorId, book.getTitle())) {
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
		
		List<Book> booksList = new ArrayList<>();
		List<Book> allSubscribedBooks  = bookRespository.findAllById(bookIds);
		booksList = allSubscribedBooks.stream().filter(Book::getActive).collect(Collectors.toList());
			
		return booksList;
		
	}

	public boolean blockBook(Long authorId, Long bookId, boolean block) {
		if(bookRespository.existsByAuthorIdAndId(authorId, bookId)) {
			Book book = getBook(bookId);
			System.out.println();
			System.out.println("initial book: " + book);
			System.out.println();
			if(book.getActive() != block)
				return false;
			
			book.setActive(!block);
			System.out.println();
			System.out.println("mid book: " + book);
			System.out.println();
			
			Book book1 = bookRespository.save(book);
			System.out.println("final book: " + book1);
			if(book1.getActive() != block) {
				return true;
			} else 
				return false;
		} else
			return false;
		
	}

	public boolean updateBook(Book book, Long bookId, Long authorId) {
		if(bookRespository.existsByAuthorIdAndId(authorId, bookId)) {
			Book existedBook = getBook(bookId);
			existedBook.setCategory(book.getCategory());
			existedBook.setContent(book.getContent());
			existedBook.setPrice(book.getPrice());
			existedBook.setPublishedDate(book.getPublishedDate());
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

	public List<Book> searchBooks(String category, String title, String author, Long price, String publisher) {
		List<Book> books = bookRespository.findBooksByCategoryAndTitleAndAuthorAndPriceAndPublisher(category, title, author, price, publisher);
		System.out.println(books);
		return books;
	}
}
