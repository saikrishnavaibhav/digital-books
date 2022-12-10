import { Component, OnInit } from '@angular/core';
import { BookstorageService } from '../_services/bookstorage.service';
import { Book } from '../_services/bookstorage.service';

@Component({
  selector: 'app-bookdetails',
  templateUrl: './bookdetails.component.html',
  styleUrls: ['./bookdetails.component.css']
})
export class BookdetailsComponent implements OnInit{

  book :any;

  ngOnInit(): void {
    this.book = this.bookService.getBook();
    console.log("book => ");
    console.log(this.book);
  }

  constructor(private bookService: BookstorageService) { }

}
