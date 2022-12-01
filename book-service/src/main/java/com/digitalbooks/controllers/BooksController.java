package com.digitalbooks.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbooks.books.BooksService;
import com.digitalbooks.entities.Book;

@RestController
@RequestMapping("/api/v1/digitalbooks")
public class BooksController {
	
	@Autowired
	BooksService booksService;

	@PostMapping("/author/{author-id}/books")
	public ResponseEntity<?> createBook(@RequestBody Book book, @PathVariable("author-id") int id){
		book.setAuthorId(id);
		return ResponseEntity.ok(booksService.saveBook(book, id));
	}
	
}
