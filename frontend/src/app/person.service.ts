import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Person } from './person';
import { PersonHistory } from './person-history';

@Injectable({
  providedIn: 'root'
})
export class PersonService {

  
  baseUrl = 'http://localhost:8080/api/persons';

  constructor(private httpClient: HttpClient) { }

  addPerson(person: Person){
    return this.httpClient.post<Person>(this.baseUrl, person);
  }

  getAllPersons(){
    return this.httpClient.get<Person[]>(this.baseUrl);
  }

  getHistoryOfPersons(){
    return this.httpClient.get<PersonHistory[]>(this.baseUrl+"/history");
  }

  updatePerson(id: number | undefined, person: Person){
    return this.httpClient.put<Person>(this.baseUrl+`/${id}`,person);
  }

  approvePerson(id: number | undefined){
    return this.httpClient.put<Person>(this.baseUrl+"/approve/"+`${id}`,null);
  }
}
