import { Component, OnInit } from '@angular/core';
import { AuthorService } from '../_services/author.service';
import { BookstorageService } from '../_services/bookstorage.service';

@Component({
  selector: 'app-updatebook',
  templateUrl: './updatebook.component.html',
  styleUrls: ['./updatebook.component.css']
})
export class UpdatebookComponent implements OnInit{

  
  isSuccessful = false;
  errorMessage = "";
  book : any = {
    logo: null,
    title: null,
    publisher: null,
    category: null,
    content: null,
    price: null
  }

  constructor(private authorService: AuthorService, private bookService: BookstorageService){}
  
  ngOnInit(): void {
    this.book = this.bookService.getBook();
  }

  onUpdate(){
    this.authorService.updateBook(this.book).subscribe(
      data=>{
        this.isSuccessful = true;
        this.errorMessage = data.message;
      },
      error => {
        console.error(error);
        this.errorMessage = "Book updation failed!"
      }
    );
  }


}
