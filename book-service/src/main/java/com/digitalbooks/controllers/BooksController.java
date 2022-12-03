package com.digitalbooks.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.books.BooksService;
import com.digitalbooks.entities.Book;
import com.digitalbooks.responses.MessageResponse;

@RestController
@RequestMapping("/api/v1/digitalbooks")
public class BooksController {
	
	@Autowired
	BooksService booksService;

	@PostMapping("/author/{author-id}/createBook")
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") int id){
		book.setAuthorId(id);
		return ResponseEntity.ok(booksService.saveBook(book, id));
	}
	
	@GetMapping("search?category&title&author&price&publisher")
	public ResponseEntity<?> getBooks(HttpServletRequest request){
		
		String uri = request.getRequestURI();
		
		String uriSearch = uri.split("?")[1];
		String[] searchSplit = uriSearch.split("&");
		
		
		List<Book> books = new ArrayList<>();
		books = booksService.getBooks();
		return ResponseEntity.ok(books);
	}
	
	/*
	 * get a subscribed book 
	 */
	@GetMapping("/book/{book-id}/getSubscribedBook")
	public ResponseEntity<?> getSubscribedBook(@PathVariable("book-id") Long bookId){
		
		Book book = booksService.getSubscribedBook(bookId);
		if(book == null || !book.getActive())
			return ResponseEntity.badRequest().body(new MessageResponse("Book not found!"));
		return ResponseEntity.ok(book);
	}
	
	/*
	 * get all subscribed books of user 
	 */
	@PostMapping("/book/getSubscribedBooks")
	public ResponseEntity<?> getAllSubscribedBooks(@RequestBody List<Long> bookIds){
		
		List<Book> book = booksService.getAllSubscribedBooks(bookIds);
		if(book.isEmpty())
			return ResponseEntity.badRequest().body(new MessageResponse("User not subscribed to any book"));
		return ResponseEntity.ok(book);
	}
}
