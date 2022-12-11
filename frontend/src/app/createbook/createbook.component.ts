import { Component } from '@angular/core';
import { AuthorService } from '../_services/author.service';

@Component({
  selector: 'app-createbook',
  templateUrl: './createbook.component.html',
  styleUrls: ['./createbook.component.css']
})
export class CreatebookComponent {
  
  isSuccessful = false;
  errorMessage = "";
  book : any = {
    logo: null,
    title: null,
    publisher: null,
    category: null,
    content: null,
    price: null
  }

  constructor(private authorService: AuthorService){}

  onCreate(){
    const{logo, title, publisher,category,content,price} = this.book;
    this.authorService.createBook(this.book).subscribe(data=> {
      console.log(data.message);
      this.isSuccessful = true;
      // setTimeout(() => {
      //   window.location.reload();
      // }, 1000);
    },
    error=> {
      console.error(error);
      this.errorMessage = error.error;
      this.isSuccessful = false;
    })
  }



}
