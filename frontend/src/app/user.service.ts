import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';
import { User } from './user';
import { UserHistory } from './user-history';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  baseUrl = 'http://localhost:8080/api';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api';
  currentUserId = Number(sessionStorage.getItem('userID'));

  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();

  constructor(private httpClient: HttpClient) { }

  authenticate(user: User){
    return this.httpClient.post<any>(this.baseUrl+"/login", user);
  }

  register(user: User){
    return this.httpClient.post<User>(this.baseUrl+`/signup/${this.currentUserId}`, user);
  }

  getAllUsers(){
    return this.httpClient.get<User[]>(this.baseUrl+"/users");
  }

  getUserById(id: number | undefined){
    return this.httpClient.get<User>(this.baseUrl+"/users/"+`${id}`);
  }

  getHistoryOfUsers(){
    return this.httpClient.get<UserHistory[]>(this.baseUrl+"/users/history");
  }

  updateUser(id: number, user: User){
    this.objectDto.currentUserDto.objectId = id;
    this.objectDto.object = user;
    return this.httpClient.put<User>(this.baseUrl+"/users",this.objectDto);
  }

  deleteUser(id: number){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<User>(this.baseUrl+"/users/delete",this.currentUserDto);
  }

  approveUser(id: number){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<User>(this.baseUrl+"/users/approve",this.currentUserDto);
  }

  rejectUser(id: number){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<User>(this.baseUrl+"/users/reject",this.currentUserDto);
  }
}
