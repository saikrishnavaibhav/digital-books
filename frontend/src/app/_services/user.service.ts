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

  subscribeAbook(bookid:any, userId:any): Observable<any> { 
    return this.http.post(API_URL +"/"+bookid+'/subscribe', {
      bookId: bookid,
      userId: userId
    });
  }
  
  cancelSubscription(subId:any, userId:any): Observable<any>  {
    return this.http.post(API_URL +"/readers/"+userId+"/books/"+subId+"/cancel-subscription",null);
    
  }

  loadUser(id: any): Observable<any>  {
    
    return this.http.get(API_URL +"/readers/"+id);
  }
}
