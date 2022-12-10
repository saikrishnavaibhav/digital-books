import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

const API_URL = 'http://localhost:8080/api/v1/digitalbooks';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthorService {
  
  constructor(private http: HttpClient, private tokenService: TokenStorageService) { }

  createBook(book: any) : Observable<any> {
    let user = this.tokenService.getUser();
    console.log(user);
    console.log(user.id);
    return this.http.post(API_URL +"/author/"+user.id+"/books",book,httpOptions);
  }

}
