import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { last } from 'rxjs';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';
import { Person } from './person';
import { PersonHistory } from './person-history';

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  
  baseUrl = 'http://localhost:8080/api/persons';
  currentUserId = sessionStorage.getItem('userID');
  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();

  constructor(private httpClient: HttpClient) { }

  addPerson(person: Person){
    return this.httpClient.post<Person>(this.baseUrl+`/${this.currentUserId}`, person);
  }

  getAllPersons(){
    return this.httpClient.get<Person[]>(this.baseUrl);
  }

  getPersonsOfUser(){
    return this.httpClient.get<Person[]>(this.baseUrl+`/user/${this.currentUserId}`);
  }

  getPersonByDetails(firstName: string, lastName: string, phoneNumber: string){
    return this.httpClient.get<Person>(this.baseUrl+`/${firstName}/${lastName}/${phoneNumber}`);
  }

  getPersonById(id: number){
    return this.httpClient.get<Person>(this.baseUrl+`/${id}`);
  }

  getHistoryOfPersons(){
    return this.httpClient.get<PersonHistory[]>(this.baseUrl+"/history");
  }

  updatePerson(id: number | undefined, person: Person){
    this.objectDto.currentUserDto.objectId = id;
    this.objectDto.object = person;
    return this.httpClient.put<Person>(this.baseUrl,this.objectDto);
  }

  deletePerson(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Person>(this.baseUrl+"/delete",this.currentUserDto);
  }

  approvePerson(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Person>(this.baseUrl+"/approve",this.currentUserDto);
  }

  rejectPerson(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Person>(this.baseUrl+"/reject",this.currentUserDto);
  }
}
