import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:8080/api/v1/digitalbooks/';
const AWS_API = 'https://bbm2n87aoc.execute-api.ap-northeast-1.amazonaws.com/UAT/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json',
  'Access-Control-Allow-Origin':"*" })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  // login(userName: string, password: string): Observable<any> {
  //   return this.http.post(AUTH_API + 'sign-in', {
  //     userName,
  //     password
  //   }, httpOptions);
  // }

  // register(userName: string, emailId: string, phoneNumber: String, password: string, role: any): Observable<any> {
  //   return this.http.post(AUTH_API + 'sign-up', {
  //     userName,
  //     emailId,
  //     phoneNumber,
  //     password,
  //     role
  //   }, httpOptions);
  // }

  login(userName: string, password: string): Observable<any> {
    return this.http.post(AWS_API , {
      userName,
      password
    }, httpOptions);
  }

  register(userName: string, emailId: string, phoneNumber: String, password: string, role: any): Observable<any> {
    return this.http.post(AWS_API + 'sign-up', {
      userName,
      emailId,
      phoneNumber,
      password,
      role
    }, httpOptions);
  }
}
