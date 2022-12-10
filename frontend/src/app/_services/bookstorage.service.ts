import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BookstorageService {

  book : Book = {
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

  constructor() { }

  setBook(book:any){
    this.book = book;
  }

  getBook(){
    return this.book;
  }
}

export class Book {
  id= null;
  logo= null;
  title= null;
  authorId= null;
  authorName= null;
  publisher= null;
  category= null;
  content= null;
  price= null;
  publishedDate= null;
  active= null
}
