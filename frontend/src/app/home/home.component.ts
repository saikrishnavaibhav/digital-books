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

  isSearchSuccess: any;
  isSearchFailed = false;
  errorMessage = "";
  isUserLoggedIn = this.tokenService.getUser() !== null;
  showSubscribe:any;

  searchForm : any = {
    category:null,
    title:null,
    author:null
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
    const {category, title, author} = this.searchForm;
    this.userService.search(category, title, author).subscribe(
      data => {
        this.books = []
        console.log(data);
        this.isSearchSuccess = true;
        this.isSearchFailed = false;
        for ( let b of data){
          this.book = b;
          this.books.push(this.book);
        }
      },
      error => {
        console.error(error);
        this.isSearchSuccess = false;
        this.isSearchFailed = true;
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

  showSubscription(bookId: any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        return false;
      }
    }
    return true;
  }

  showUnSubscribe(bookId:any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    for(let sub of subs){
      if(bookId === sub.bookId && this.userService.verifyIfLessThan24Hrs(bookId)) {
        return true;
      }
    }
    return false;
  }

  onSubscribe(bookId:any){
    this.userService.subscribeAbook(bookId, this.tokenService.getUser().id).subscribe(
      data=> {
        console.log(data);
        let user = this.tokenService.getUser();
        let subs = user.subscriptions;
        subs.push({
          userId:user.id,
          bookId:bookId,
          id:data.id,
          subscriptionTime:data.subscriptionTime,
          active:true
        })
        user.subscriptions = subs;
        this.tokenService.saveUser(user);
      },
      error => {
        console.error(error);
      }
    );
  }

  onUnSubscribe(bookId: any){
    let subs : any[] = this.tokenService.getUser().subscriptions;
    let subId:number;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        subId = sub.id;
        this.cancelSubscription(subId);
      }
    }
    
  }

  cancelSubscription(subId:number){
    this.userService.cancelSubscription(subId, this.tokenService.getUser().id).subscribe(
      data=>{
        console.log(data);
        let user = this.tokenService.getUser();
        let subs = user.subscriptions;
        subs = subs.filter((sub: { id: number; }) => sub.id !== subId)
        user.subscriptions = subs;
        this.tokenService.saveUser(user);
        //this.tokenService.reloadUser(this.tokenService.getUser().id);
      },
      error=>{
        console.error(error);
      }
    );
  }

}
