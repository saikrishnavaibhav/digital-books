import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  isSearchSuccess = false;
  errorMessage = "";
  isUserLoggedIn = this.tokenService.getUser() !== null;

  searchForm : any = {
    category:null,
    title:null,
    author:null,
    price:null,
    publisher:null
  };
  
  books : any[] = []
  
  book : any = {
    logo: null,
    title: null,
    publisher: null,
    category: null,
    content: null,
    price: null
  }

  constructor(private userService: UserService, private tokenService: TokenStorageService) { }

  onSearch(){
    const {category, title, author,price,publisher} = this.searchForm;
    this.userService.search(category, title, author,price,publisher).subscribe(
      data => {
        console.log(data);
        this.isSearchSuccess = true;
        for ( let b of data){
          this.book = b;
          this.books.push(this.book);
        }
      },
      error => {
        console.error(error);
        this.isSearchSuccess = false;
        if(error instanceof HttpErrorResponse){
          console.error(error.error.message);
          this.errorMessage = error.error.message
        }
        
      }
    );

  }

  onClick(book:any){
    if(!this.isUserLoggedIn){
      console.log("please login/signup");
    }
  }

  oncancelSearch(){
    this.isSearchSuccess = false;
  }

  subscribed(bookId: any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        return true;
      }
    }
    return false;
  }

  onSubscribe(bookId:any){
    this.userService.subscribeAbook(bookId, this.tokenService.getUser().id).subscribe(
      data=> {
        console.log(data);
        this.tokenService.reloadUser(this.tokenService.getUser().id);
      },
      error => {
        console.error(error);
      }
    );
  }

  public onUnSubscribe(bookId: any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    let subId = null;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        subId = sub.id;
      }
    }
    this.userService.cancelSubscription(subId, this.tokenService.getUser().id).subscribe(
      data=>{
        console.log(data);
        this.tokenService.reloadUser(this.tokenService.getUser().id);
      },
      error=>{
        console.error(error);
      }
    )
  }

}
