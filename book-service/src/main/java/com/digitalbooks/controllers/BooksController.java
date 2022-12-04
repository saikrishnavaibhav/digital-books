package com.digitalbooks.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") Long id){
		book.setAuthorId(id);
		return ResponseEntity.ok(booksService.saveBook(book, id));
	}
	
	/*
	 * get a subscribed book 
	 */
	@GetMapping("/book/{book-id}/getSubscribedBook")
	public ResponseEntity<?> getSubscribedBook(@PathVariable("book-id") Long bookId){
		
		Book book = booksService.getBook(bookId);
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
	
	/*
	 * Author can block/unblock his book
	 */
	@GetMapping("/author/{authorId}/blockBook/{bookId}")
	public MessageResponse getSubscribedBook(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @RequestParam("block") boolean block) {
		if (booksService.blockBook(authorId, bookId, block)) return new MessageResponse("Book updated successfully");
		return new MessageResponse("Book updation failed");
	}
	
	
	/*
	 * Author can update his book
	 */
	@PostMapping("/author/{author-id}/updateBook/{book-id}")
	public MessageResponse updateBook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if(booksService.updateBook(book, bookId, authorId)) {
			return new MessageResponse("Book updated Successfully");
		}
		return new MessageResponse("Book updation failed");
	}

	
	/*
	 * Reader can read his subscribed book
	 */
	@GetMapping("/book/{book-id}/readBook")
	public MessageResponse readBook(@PathVariable("book-id") Long bookId) {
		return booksService.readBook(bookId);
	}
	
	
	/*
	 * Anyone can search books
	 */
	@GetMapping("/book/searchBooks")
	public MessageResponse readBook(@RequestParam("category") String category, @RequestParam("title") String title,
			@RequestParam("author") String author, @RequestParam("price") int price,  @RequestParam("publisher") String publisher) {
		return booksService.searchBooks(category, title, author, price, publisher);
	}
	
}
