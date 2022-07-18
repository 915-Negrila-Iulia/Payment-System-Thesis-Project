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
    return this.httpClient.post<any>(this.baseUrl+"/login", user);
  }

  register(user: User){
    return this.httpClient.post<User>(this.baseUrl+`/signup/${sessionStorage.getItem('userID')}`, user);
  }

  getAllUsers(){
    return this.httpClient.get<User[]>(this.baseUrl+"/users");
  }

  getCurrentUser(){
    return this.httpClient.get<User>(this.baseUrl+"/current-user");
  }

  getUserById(id: number | undefined){
    return this.httpClient.get<User>(this.baseUrl+"/users/"+`${id}`);
  }

  getHistoryOfUsers(){
    return this.httpClient.get<UserHistory[]>(this.baseUrl+"/users/history");
  }

  updateUser(id: number | undefined, user: User){
    return this.httpClient.put<User>(this.baseUrl+"/users/"+`${id}/${sessionStorage.getItem('userID')}`,user);
  }

  deleteUser(id: number | undefined){
    return this.httpClient.put<User>(this.baseUrl+"/users/delete/"+`${id}/${sessionStorage.getItem('userID')}`,null);
  }

  approveUser(id: number | undefined){
    return this.httpClient.put<User>(this.baseUrl+"/users/approve/"+`${id}/${sessionStorage.getItem('userID')}`,null);
  }

  rejectUser(id: number | undefined){
    return this.httpClient.put<User>(this.baseUrl+"/users/reject/"+`${id}/${sessionStorage.getItem('userID')}`,null);
  }
}
