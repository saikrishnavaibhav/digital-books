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
    this.user = this.tokenStorageService.getUser();
    this.authorService.getBooksCreatedByAuthor(this.user.id).subscribe(
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

  onBlock(bookId : any) : void {
    console.log("blocking : " + bookId);
    this.blockBook(bookId,"yes");
  }

  onUnblock(bookId : any) : void {
    console.log("unblocking : "+bookId);
    this.blockBook(bookId,"no");
  }

  blockBook(bookId:any, block:any){
    this.authorService.blockBook(bookId, block).subscribe(
      data=>{
        console.log(data);
        window.location.reload();
      },
      error=>{
        console.error(error);
      }
    );
  }

  onUpdate(book : any) : void {
    this.bookService.setBook(book);
  }
}
