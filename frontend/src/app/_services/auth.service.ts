import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8080/api/v1/digitalbooks/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(userName: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'sign-in', {
      userName,
      password
    }, httpOptions);
  }

  register(userName: string, emailId: string, phoneNumber: String, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'sign-up', {
      userName,
      emailId,
      phoneNumber,
      password
    }, httpOptions);
  }
}
