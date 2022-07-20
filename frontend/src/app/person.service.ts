import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Person } from './person';
import { PersonHistory } from './person-history';

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  
  baseUrl = 'http://localhost:8080/api/persons';
  currentUserId = sessionStorage.getItem('userID');

  constructor(private httpClient: HttpClient) { }

  addPerson(person: Person){
    return this.httpClient.post<Person>(this.baseUrl+`/${this.currentUserId}`, person);
  }

  getAllPersons(){
    return this.httpClient.get<Person[]>(this.baseUrl);
  }

  getPersonById(id: number){
    return this.httpClient.get<Person>(this.baseUrl+`/${id}`);
  }

  getHistoryOfPersons(){
    return this.httpClient.get<PersonHistory[]>(this.baseUrl+"/history");
  }

  updatePerson(id: number | undefined, person: Person){
    return this.httpClient.put<Person>(this.baseUrl+`/${id}/${this.currentUserId}`,person);
  }

  deletePerson(id: number | undefined){
    return this.httpClient.put<Person>(this.baseUrl+"/delete/"+`${id}/${this.currentUserId}`,null);
  }

  approvePerson(id: number | undefined){
    return this.httpClient.put<Person>(this.baseUrl+"/approve/"+`${id}/${this.currentUserId}`,null);
  }

  rejectPerson(id: number | undefined){
    return this.httpClient.put<Person>(this.baseUrl+"/reject/"+`${id}/${this.currentUserId}`,null);
  }
}
