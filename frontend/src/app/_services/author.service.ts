import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

const API_URL = 'http://localhost:8080/api/v1/digitalbooks';
const AWS_API = 'https://bbm2n87aoc.execute-api.ap-northeast-1.amazonaws.com/UAT/';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json',
  'Access-Control-Allow-Origin':"*" })
};

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  
  
  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  user = this.tokenService.getUser();

  createBook(book: any) : Observable<any> {
    //return this.http.post(API_URL +"/author/"+this.user.id+"/books",book,httpOptions);
    return this.http.post(AWS_API +"/author/createbook/"+this.user.id,book,httpOptions);
  }

  updateBook(book: any) : Observable<any> {
    return this.http.put(AWS_API +"/author/"+this.user.id+"/updatebook/"+book.id,book,httpOptions);
  }

  getBooksCreatedByAuthor(id: any) : Observable<any> {
    return this.http.get(AWS_API + '/author/'+id+'/getAllBooks');
  }

  blockBook(bookId: any, block: any) {
    let queryParams = new HttpParams();
    queryParams = queryParams.append("block",block)
    return this.http.post(AWS_API +"/author/"+this.user.id+"/books/"+bookId, null,{params:queryParams});
  }
}
