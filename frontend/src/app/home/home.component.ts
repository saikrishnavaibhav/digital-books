import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  isSearchFailed = false;
  errorMessage = "";

  searchForm : any = {
    category:null,
    title:null,
    author:null,
    price:null,
    publisher:null
  };
  
  constructor(private userService: UserService) { }

  onSearch(){
    const {category, title, author,price,publisher} = this.searchForm;
    this.userService.search(category, title, author,price,publisher).subscribe(
      data => {
        console.log(data);
      },
      error => {
        console.error(error);
        this.isSearchFailed = true;
        
        if(error instanceof HttpErrorResponse){
          console.error(error.error.message);
          this.errorMessage = error.error.message
        }
        
        
      }
    );
  }
}
