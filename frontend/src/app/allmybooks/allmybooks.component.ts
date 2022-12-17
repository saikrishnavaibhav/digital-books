import { Component, OnInit } from '@angular/core';
import { AuthorService } from '../_services/author.service';
import { BookstorageService } from '../_services/bookstorage.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-allmybooks',
  templateUrl: './allmybooks.component.html',
  styleUrls: ['./allmybooks.component.css']
})
export class AllmybooksComponent implements OnInit {

  isBooksAvailable = false;
  showSuccess=false;
  successMessage="";
  showErrorMessage=false;
  errorMessage="";

  user: any = {
    id: null,
    username: null,
    emailid: null,
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

  constructor(private authorService: AuthorService, private tokenStorageService: TokenStorageService, private bookService: BookstorageService) { }

  ngOnInit(): void {
   this.loadBooks();

  }
  loadBooks() {
    this.user = this.tokenStorageService.getUser();
    this.authorService.getBooksCreatedByAuthor(this.user.id).subscribe(
      data  => {
        let booksOfAuthor:any[] = data;
        if(booksOfAuthor.length === 0) this.isBooksAvailable = false;
        else this.isBooksAvailable = true;
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
      },
      error => {
        console.error(error);
      }
    );
  }

  onClick(book : any) : void {
    this.bookService.setBook(book);
  }

  onBlock(bookId : any) : void {
    console.log("blocking : " + bookId);
    this.blockBook(bookId,"yes","blocked");
  }

  onUnblock(bookId : any) : void {
    console.log("unblocking : "+bookId);
    this.blockBook(bookId,"no","unblocked");
  }

  blockBook(bookId:any, block:any, message:String){
    this.authorService.blockBook(bookId, block).subscribe(
      data=>{
        console.log("book updated");
        this.modifyBooksDataOfAuthor(bookId, block);
        this.successMessage="Book "+message+" successfully!";
        this.showSuccess=true;
        setTimeout(() => {
          this.showSuccess=false;
          this.successMessage="";
        }, 2500);
      },
      error=>{
        console.error(error);
        this.errorMessage="Failed to "+message.substring(0,message.length-2)+" book!";
        this.showErrorMessage=true;
        setTimeout(() => {
          this.showErrorMessage=false;
          this.errorMessage="";
        }, 2500);
      }
    );
  }
  
  modifyBooksDataOfAuthor(bookId: any, block: any) {
    let books = this.books;
    let active = true;
    if(block === 'yes')
      active = false;
    else
      active = true;
    books.map(b=>{
      if(b.id === bookId){
        b.active = active;
      }
    })

  }

  onUpdate(book : any) : void {
    this.bookService.setBook(book);
  }
}
