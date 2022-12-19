package com.digitalbooks.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.books.BooksService;
import com.digitalbooks.entities.Book;
import com.digitalbooks.repositories.BookRespository;
import com.digitalbooks.requests.BookRequest;
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
	
	static final String INVALID_REQUEST= "Invalid Request";
	
	private static final Logger logger = LoggerFactory.getLogger(BooksController.class);
	
	/*
	 * Author can create a book
	 */
	@PostMapping("/author/{author-id}/createBook")
	public ResponseEntity<?> createBook(@RequestBody BookRequest bookRequest, @PathVariable("author-id") Long id) {
		if(id == null)
			return ResponseEntity.badRequest().body("Invalid author Id");
		bookRequest.setAuthorId(id);
		return booksService.saveBook(bookRequest, id);
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
		return ResponseEntity.ok(book);
	}
	
	/*
	 * get all subscribed books of user 
	 */
	@PostMapping("/book/getSubscribedBooks")
	@Cacheable("subscribedbooks")
	public ResponseEntity<?> getAllSubscribedBooks(@RequestBody List<Long> bookIds){
		if(bookIds.isEmpty())
			return ResponseEntity.badRequest().body("Invalid books");
		List<Book> book = booksService.getAllSubscribedBooks(bookIds);
	
		return ResponseEntity.ok(book);
	}
	

	/*
	 * Author can block/unblock his book
	 */
	@GetMapping("/author/{authorId}/blockBook/{bookId}")
	public ResponseEntity<?> blockBook(@PathVariable("authorId") Long authorId, @PathVariable("bookId") Long bookId, @RequestParam("block") boolean block) {
		if(authorId == null || bookId == null)
			return ResponseEntity.badRequest().body(new MessageResponse(INVALID_REQUEST));
		if (booksService.blockBook(authorId, bookId, block)) return ResponseEntity.ok().build();
		return ResponseEntity.internalServerError().body(new MessageResponse("Book updation failed"));
	}
	
	
	/*
	 * Author can update his book
	 */
	@PutMapping("/author/{author-id}/updateBook/{book-id}")
	public ResponseEntity<?> updateBook(@RequestBody BookRequest bookRequest, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if(authorId == null)
			return ResponseEntity.badRequest().body("Invalid author id");
		if(bookId == null)
			return ResponseEntity.badRequest().body(INVALID_BOOKID);
		if(booksService.updateBook(bookRequest, bookId, authorId)) {
			return ResponseEntity.ok().build();
		}
		logger.info("updating book: {}",bookId);
		return ResponseEntity.badRequest().body("Book updation failed");
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
	@Cacheable("bookexists")
	public String checkBookExistance(@PathVariable("book-id") Long bookId) {
		
		if(bookId == null)
			return "Invalid BookId";
		return bookRespository.existsById(bookId) ? "BookFound" : INVALID_BOOKID;
	}
	
	/*
	 * Anyone can search books
	 */
	@GetMapping("/book/searchBooks")
	@Cacheable("searchedbooks")
	public List<BookResponse> searchBooks(@Nullable @RequestParam("category") String category, @Nullable @RequestParam("title") String title,
			@Nullable @RequestParam("author") String author) {
		
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
	public List<Book> getAllAuthorBooks(@PathVariable("author-id") Long authorId) {
		
		List<Book> booksList = new ArrayList<>();
		if(Objects.isNull(authorId) || authorId == 0)
			return booksList;
		logger.info("retrieving books of author: {}", authorId);
		booksList = booksService.getAuthorBooks(authorId);
		return booksList;
	}
	
	private List<BookResponse> getBookResponses(List<Book> book) {
		return book.stream().filter(Book::getActive).map(book1 -> {
			BookResponse bookResponse = new BookResponse();
			bookResponse.setId(book1.getId());
				bookResponse.setAuthorName(book1.getAuthorName());
				bookResponse.setCategory(book1.getCategory());
				bookResponse.setLogo(book1.getLogo());
				bookResponse.setPrice(book1.getPrice());
				bookResponse.setTitle(book1.getTitle());
				logger.info(bookResponse.toString());
				return bookResponse;
		}).collect(Collectors.toList());
		
	}
}
