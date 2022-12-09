import { Component, OnInit } from '@angular/core';
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

  constructor(private userService: UserService, private tokenStorageService: TokenStorageService) { }

  ngOnInit(): void {
    
    this.user = this.tokenStorageService.getUser();
    this.userService.getSubscribedBooks(this.user.id).subscribe(
      data => {
        let books = [];
        for(let b of data){
          this.book = b;
          books.push(this.book);
        }
        console.log(books);
      },
      error => {
        console.error(error);
      }
    )
  }
}
