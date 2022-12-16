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
  isBooksAvailable=false;
  user: any = {
    id: null,
    username: null,
    email: null,
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
    this.loadBooks();

  }

  loadBooks(){
    this.books = [];
    this.user = this.tokenStorageService.getUser();
    this.userService.getSubscribedBooks(this.user.id).subscribe(
      data => {
        for(let b of data){
          this.book = b;
          if(this.book.publishedDate != null){
            let date = new Date(Date.parse(this.book.publishedDate));
            this.book.publishedDate = date.getDate()+'-'+(date.getMonth()+1)+'-'+date.getFullYear();
          } else {
            this.book.publishedDate = 'Not Available';
          }
          this.books.push(this.book);
        }
        if(this.books.length===0) this.isBooksAvailable = false;
        else this.isBooksAvailable = true;
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
    let subId:number;
    for(let sub of subs){
      if(bookId === sub.bookId) {
        subId = sub.id;
        this.cancelSubscription(subId);
      }
    }
    
  }

  cancelSubscription(subId:number){
    this.userService.cancelSubscription(subId, this.tokenStorageService.getUser().id).subscribe(
      data=>{
        console.log(data);
        let user = this.tokenStorageService.getUser();
        let subs = user.subscriptions;
        subs = subs.filter((sub: { id: number; }) => sub.id !== subId)
        user.subscriptions = subs;
        this.tokenStorageService.saveUser(user);
        //this.tokenService.reloadUser(this.tokenService.getUser().id);
        //window.location.reload()
        this.loadBooks();
      },
      error=>{
        console.error(error);
      }
    );
  }

  verifyIfLessThan24Hrs(bookId: any){
    return this.userService.verifyIfLessThan24Hrs(bookId);
  }

}
