package com.digitalbooks.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.digitalbooks.entities.Book;
import com.digitalbooks.repositories.BookRespository;
import com.digitalbooks.responses.MessageResponse;

@Service
public class BooksService {

	@Autowired
	BookRespository bookRespository;
	
	public MessageResponse saveBook(Book book, int authorId) {
		System.out.println(book);
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
	
}
