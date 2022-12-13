import { Component, OnInit } from '@angular/core';
import { HomeComponent } from '../home/home.component';
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

  onCancelSubscription(bookId : any) : void {
    let subs : any[] = this.tokenStorageService.getUser().subscriptions;
    let subId = null;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        subId = sub.id;
      }
    }
    this.userService.cancelSubscription(subId, this.tokenStorageService.getUser().id).subscribe(
      data=>{
        console.log(data);
        this.tokenStorageService.reloadUser(this.tokenStorageService.getUser().id);
        window.location.reload();
      },
      error=>{
        console.error(error);
      }
    )
  }

  verifyIfLessThan24Hrs(bookId: any){
    var currentTimestamp = Date.now();
    var twentyFourHours = 24 * 60 * 60 * 1000;
   
    this.user = this.tokenStorageService.getUser();
    let subs =  this.user.subscriptions;
    for(let sub of subs){
      let subscriptionTimeStamp = Date.parse(sub.subscriptionTime);
      
      if(bookId === sub.bookId){
        if((currentTimestamp - subscriptionTimeStamp) > twentyFourHours){
          return false;
        }

      }
    }
    return true;
  }

}
