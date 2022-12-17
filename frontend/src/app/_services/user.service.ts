import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

const API_URL = 'http://localhost:8080/api/v1/digitalbooks';
const AWS_API = 'https://bbm2n87aoc.execute-api.ap-northeast-1.amazonaws.com/UAT/';


  const headers= new HttpHeaders({ 'Content-Type': 'application/json',
  'Access-Control-Allow-Origin':"*" });


@Injectable({
  providedIn: 'root'
})
export class UserService {
    
  constructor(private http: HttpClient, private tokenStorageService: TokenStorageService) { }

  search(category: any, title: any, author: any) : Observable<any> {
    let queryParams = new HttpParams();
    queryParams = queryParams.append("category",category)
                              .append("title",title)
                              .append("author",author);

    return this.http.get(AWS_API + '/search', {headers, params:queryParams});
  }

  getSubscribedBooks(id: any) : Observable<any> { 
    return this.http.get(AWS_API + '/readers/'+id+'/books');
  }

  getSubscribedBook(id: any) : Observable<any> { 
    return this.http.get(AWS_API + '/readers/'+id+'/books');
  }

  subscribeAbook(bookid:any, userId:any): Observable<any> { 
    return this.http.post(AWS_API +"/"+bookid+'/subscribe', {
      bookId: bookid,
      userId: userId
    });
  }
  
  cancelSubscription(subId:number, userId:any): Observable<any>  {
    return this.http.post(AWS_API +"/readers/"+userId+"/books/"+subId+"/cancel-subscription",null);
    
  }

  // loadUser(id: any): Observable<any>  {
  //   return this.http.get(API_URL +"/readers/"+id);
  // }

  verifyIfLessThan24Hrs(bookId: any) : boolean{
    var currentTimestamp = Date.now();
    var twentyFourHours = 24 * 60 * 60 * 1000;
   
    let user = this.tokenStorageService.getUser();
    let subs =  user.subscriptions;
    for(let sub of subs){
      let subscriptionTimeStamp = Date.parse(sub.subscriptionTime);
      
      if(bookId === sub.bookId){
        if((currentTimestamp - subscriptionTimeStamp) > twentyFourHours){
          return false;
        }

      }
    }
    return true;
  }

}
