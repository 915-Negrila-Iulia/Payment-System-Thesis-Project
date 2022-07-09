import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from './user';
import { UserHistory } from './user-history';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  baseUrl = 'http://localhost:8080/api';

  constructor(private httpClient: HttpClient) { }

  authenticate(user: User){
    return this.httpClient.post<User>(this.baseUrl+"/login", user);
  }

  register(user: User){
    return this.httpClient.post<User>(this.baseUrl+"/register", user);
  }

  getAllUsers(){
    return this.httpClient.get<User[]>(this.baseUrl+"/users");
  }

  getHistoryOfUsers(){
    return this.httpClient.get<UserHistory[]>(this.baseUrl+"/users/history");
  }

  updateUser(id: number | undefined, user: User){
    return this.httpClient.put<User>(this.baseUrl+"/users/"+`${id}`,user);
  }

  approveUser(id: number | undefined){
    return this.httpClient.put<User>(this.baseUrl+"/users/approve/"+`${id}`,null);
  }
}
