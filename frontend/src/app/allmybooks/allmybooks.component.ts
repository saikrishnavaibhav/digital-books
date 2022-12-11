import { Component, OnInit } from '@angular/core';
import { BookstorageService } from '../_services/bookstorage.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-allmybooks',
  templateUrl: './allmybooks.component.html',
  styleUrls: ['./allmybooks.component.css']
})
export class AllmybooksComponent implements OnInit {

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

    this.userService.getBooksCreatedByAuthor(this.user.id).subscribe(
      data  => {
        for(let b of data){
          this.book = b;
          this.books.push(this.book);
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
