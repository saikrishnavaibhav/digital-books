import { Component, OnInit } from '@angular/core';
import { BookstorageService } from '../_services/bookstorage.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-board-user',
  templateUrl: './board-user.component.html',
  styleUrls: ['./board-user.component.css']
})

export class BoardUserComponent implements OnInit {
  
  content?: string;
  user: any = {
    id: null,
    userName: null,
    emailId: null,
    phoneNumber: null,
    roles :null,
    subscriptions:null
  };

  public books:any[] = []

  public book : any = {
    id: null,
    logo: null,
    title: null,
    authorId: null,
    authorName: null,
    publisher: null,
    category: null,
    content: null,
    price: null,
    publishedDate: null,
    active: null
  }

  constructor(private userService: UserService, private tokenStorageService: TokenStorageService, private bookService: BookstorageService) { }

  ngOnInit(): void {
    this.user = this.tokenStorageService.getUser();
    this.userService.getSubscribedBooks(this.user.id).subscribe(
      data => {
        
        console.log("books -> " + this.books);
        for(let b of data){
          this.book = b;
          this.books.push(this.book);
        }
        console.log("after adding books -> "+this.books);
        for (let b of this.books){
          console.log("book => "+b);
        }
      },
      error => {
        console.error(error);
      }
    );

  }

  onClick(book : any) : void {
    this.bookService.setBook(book);
  }

}
