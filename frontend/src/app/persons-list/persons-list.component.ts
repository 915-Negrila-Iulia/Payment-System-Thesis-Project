import { Component, OnInit } from '@angular/core';
import { Person } from '../person';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-persons-list',
  templateUrl: './persons-list.component.html',
  styleUrls: ['./persons-list.component.scss']
})
export class PersonsListComponent implements OnInit {

  persons: Person[] = [];
  person: Person = new Person();

  constructor(private personService: PersonService) { }

  ngOnInit(): void {
    this.getPersons();
  }

  getPersons(){
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
    })
  }

  updatePerson(id: any, firstName: any, lastName: any, address: any, dateOfBirth: any, phoneNumber: any, userID: any, status: any){
    this.person.id = id;
    this.person.firstName = firstName.value;
    this.person.lastName = lastName.value;
    this.person.address = address.value;
    this.person.dateOfBirth = dateOfBirth.value;
    this.person.phoneNumber = phoneNumber.value;
    this.person.userID = userID;
    this.person.status = status;
    this.personService.updatePerson(id,this.person).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

  getPersonById(id: any){
    return this.persons.filter( person => person.id === id)[0];
  }

  approvePerson(id: any){
    this.personService.approvePerson(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

}
