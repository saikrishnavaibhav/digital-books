import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/v1/digitalbooks';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {
  
  constructor(private http: HttpClient) { }

  search(category: any, title: any, author: any, price: any, publisher: any) : Observable<any> {
    let queryParams = new HttpParams();
    queryParams = queryParams.append("category",category)
                              .append("title",title)
                              .append("author",author)
                              .append("price",price)
                              .append("publisher",publisher);

    return this.http.get(API_URL + '/search', {params:queryParams});
  }

  getSubscribedBooks(id: any) : Observable<any> { 
    return this.http.get(API_URL + '/readers/'+id+'/books');
  }

  getSubscribedBook(id: any) : Observable<any> { 
    return this.http.get(API_URL + '/readers/'+id+'/books');
  }
}
