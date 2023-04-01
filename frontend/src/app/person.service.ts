import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';
import { Person } from './person';
import { PersonHistory } from './person-history';

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  baseUrl = 'http://localhost:8080/api/persons';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api/persons';
  currentUserId = sessionStorage.getItem('userID');
  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();
  role = sessionStorage.getItem('role');

  constructor(private httpClient: HttpClient) { }

  addPerson(person: Person){
    return this.httpClient.post<Person>(this.baseUrl+`/${this.currentUserId}`, person);
  }

  getAllPersons(){
    return this.role=="ADMIN_ROLE" ?  this.httpClient.get<Person[]>(this.baseUrl) : this.getPersonsOfUser();
  }

  getPersonsOfUser(){
    return this.httpClient.get<Person[]>(this.baseUrl+`/user/${this.currentUserId}`);
  }

  getPersonsByStatus(status: string){
    return this.httpClient.get<Person[]>(this.baseUrl+`/status/${status}`);
  }

  getPersonByDetails(firstName: string, lastName: string, phoneNumber: string){
    return this.httpClient.get<Person>(this.baseUrl+`/${firstName}/${lastName}/${phoneNumber}`);
  }

  getPersonById(id: number){
    return this.httpClient.get<Person>(this.baseUrl+`/${id}`);
  }

  getHistoryOfPersons(){
    return this.role=="ADMIN_ROLE" ? this.httpClient.get<PersonHistory[]>(this.baseUrl+"/history") : this.getPersonsHistoryOfUser();
  }

  getPersonsHistoryByPersonId(id: number | undefined){
    return this.httpClient.get<PersonHistory[]>(this.baseUrl+`/history/${id}`);
  }

  getPersonsHistoryOfUser(){
    return this.httpClient.get<PersonHistory[]>(this.baseUrl+`/history/user/${this.currentUserId}`);
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
