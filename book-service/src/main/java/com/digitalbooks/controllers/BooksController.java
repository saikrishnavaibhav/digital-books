package com.digitalbooks.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.books.BooksService;
import com.digitalbooks.entities.Book;
import com.digitalbooks.repositories.BookRespository;
import com.digitalbooks.responses.BookResponse;
import com.digitalbooks.responses.MessageResponse;

@RestController
@RequestMapping("/api/v1/digitalbooks")
public class BooksController {
	
	@Autowired
	BooksService booksService;
	
	@Autowired
	BookRespository bookRespository;
	
	static final String INVALID_BOOKID= "Invalid book id";
	
	/*
	 * Author can create a book
	 */
	@PostMapping("/author/{author-id}/createBook")
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") Long id) {
		if(id == null)
			return ResponseEntity.badRequest().body("Invalid author Id");
		book.setAuthorId(id);
		MessageResponse response = booksService.saveBook(book, id);
		if("Book with same title exists!".equals(response.getMessage()))
			return ResponseEntity.badRequest().body("Book with same title exists!");
		return ResponseEntity.ok(response);
	}
	
	/*
	 * get a subscribed book 
	 */
	@GetMapping("/book/{book-id}/getSubscribedBook")
	public ResponseEntity<?> getSubscribedBook(@PathVariable("book-id") Long bookId){
		if(bookId == null)
			return ResponseEntity.badRequest().body(INVALID_BOOKID);
		Book book = booksService.getBook(bookId);
		if(book == null || !book.getActive())
			return ResponseEntity.badRequest().body(new MessageResponse("Book not found!"));
		BookResponse bookResponse = new BookResponse();
		bookResponse.setId(book.getId());
		bookResponse.setActive(book.getActive());
		bookResponse.setAuthorId(book.getAuthorId());
		bookResponse.setAuthorName(book.getAuthorName());
		bookResponse.setCategory(book.getCategory());
		bookResponse.setContent(book.getContent());
		
//		String logo = ServletUriComponentsBuilder
//		          .fromCurrentContextPath()
//		          .path("/logos/")
//		          .path(""+book.getId())
//		          .toUriString();
//		bookResponse.setLogo(logo);
		bookResponse.setPrice(book.getPrice());
		bookResponse.setPublishedDate(book.getPublishedDate());
		bookResponse.setPublisher(book.getPublisher());
		bookResponse.setTitle(book.getTitle());
		return ResponseEntity.ok(bookResponse);
	}
	
	/*
	 * get all subscribed books of user 
	 */
	@PostMapping("/book/getSubscribedBooks")
	public ResponseEntity<?> getAllSubscribedBooks(@RequestBody List<Long> bookIds){
		if(bookIds.isEmpty())
			return ResponseEntity.badRequest().body("Invalid books");
		List<Book> book = booksService.getAllSubscribedBooks(bookIds);
		if(book.isEmpty())
			return ResponseEntity.badRequest().body(new MessageResponse("User not subscribed to any book"));
		List<BookResponse> bookResponses = getBookResponses(book);
		return ResponseEntity.ok(bookResponses);
	}
	

	/*
	 * Author can block/unblock his book
	 */
	@GetMapping("/author/{authorId}/blockBook/{bookId}")
	public MessageResponse getSubscribedBook(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @RequestParam("block") boolean block) {
		if(authorId == null)
			return new MessageResponse("Invalid author id");
		if(bookId == null)
			return new MessageResponse(INVALID_BOOKID);
		if (booksService.blockBook(authorId, bookId, block)) return new MessageResponse("Book updated successfully");
		return new MessageResponse("Book updation failed");
	}
	
	
	/*
	 * Author can update his book
	 */
	@PostMapping("/author/{author-id}/updateBook/{book-id}")
	public MessageResponse updateBook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if(authorId == null)
			return new MessageResponse("Invalid author id");
		if(bookId == null)
			return new MessageResponse(INVALID_BOOKID);
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
		
		if(bookId == null)
			return new MessageResponse(INVALID_BOOKID);
		return booksService.readBook(bookId);
	}
	
	
	/*
	 * verify if book exists
	 */
	@GetMapping("/book/{book-id}/checkBook")
	public String checkBookExistance(@PathVariable("book-id") Long bookId) {
		
		if(bookId == null)
			return "Invalid BookId";
		return bookRespository.existsById(bookId) ? "BookFound" : INVALID_BOOKID;
	}
	
	/*
	 * Anyone can search books
	 */
	@GetMapping("/book/searchBooks")
	public List<BookResponse> searchBooks(@RequestParam("category") String category, @RequestParam("title") String title,
			@RequestParam("author") String author) {
		
		List<BookResponse> booksList = new ArrayList<>();
		if(ObjectUtils.isEmpty(category) && ObjectUtils.isEmpty(title) && ObjectUtils.isEmpty(author))
			return booksList;
		
		List<Book> books = booksService.searchBooks(category, title, author);
		return getBookResponses(books);
	}
	
	
	/*
	 * Anyone can search books
	 */
	@GetMapping("/author/{author-id}/getAuthorBooks")
	public List<BookResponse> getAllAuthorBooks(@PathVariable("author-id") Long authorId) {
		
		List<BookResponse> booksList = new ArrayList<>();
		if(ObjectUtils.isEmpty(authorId))
			return booksList;
		
		List<Book> books = booksService.getAuthorBooks(authorId);
		return getAuthorBooks(books);
	}

	private List<BookResponse> getAuthorBooks(List<Book> book) {
		return book.stream().map(book1 -> {
			BookResponse bookResponse = new BookResponse();
				bookResponse.setId(book1.getId());
				bookResponse.setAuthorName(book1.getAuthorName());
				bookResponse.setCategory(book1.getCategory());
				bookResponse.setContent(book1.getContent());
				bookResponse.setActive(book1.getActive());
				
				//set logo here
				
				bookResponse.setPrice(book1.getPrice());
				bookResponse.setPublishedDate(book1.getPublishedDate());
				bookResponse.setPublisher(book1.getPublisher());
				bookResponse.setTitle(book1.getTitle());
				return bookResponse;
		}).collect(Collectors.toList());
		
	}
	
	private List<BookResponse> getBookResponses(List<Book> book) {
		return book.stream().filter(Book::getActive).map(book1 -> {
			BookResponse bookResponse = new BookResponse();
			bookResponse.setId(book1.getId());
				bookResponse.setAuthorName(book1.getAuthorName());
				bookResponse.setCategory(book1.getCategory());
				bookResponse.setContent(book1.getContent());
				
				//set logo here
				
				bookResponse.setPrice(book1.getPrice());
				bookResponse.setPublishedDate(book1.getPublishedDate());
				bookResponse.setPublisher(book1.getPublisher());
				bookResponse.setTitle(book1.getTitle());
				return bookResponse;
		}).collect(Collectors.toList());
		
	}
}
