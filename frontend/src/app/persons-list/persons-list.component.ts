import { Component, OnInit } from '@angular/core';
import { tap } from 'rxjs';
import { Person } from '../person';
import { PersonService } from '../person.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-persons-list',
  templateUrl: './persons-list.component.html',
  styleUrls: ['./persons-list.component.scss']
})
export class PersonsListComponent implements OnInit {

  persons: Person[] = [];
  person: Person = new Person();
  users: User[] = [];
  objectType = 'PERSON';

  constructor(private personService: PersonService, private userService: UserService) { }

  ngOnInit(): void {
    this.getPersons();
    this.userService.getAllUsers().subscribe(data => {
    this.users = data;
    })
  }

  getUsername(id: number | undefined){
    if(this.users){
      let user = this.users.filter(user => user.id === id)[0];
      if(user){
        return user.username;
      }
      return '';
    }
    else{
      return '';
    }
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
      //console.log(data);
    },
    error => console.log(error)
    );
  }

  deletePerson(id: any){
    this.personService.deletePerson(id).subscribe(data => {
     console.log(data)
    },
    error => console.log(error))
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

  rejectPerson(id: number | undefined){
    this.personService.rejectPerson(id).subscribe(data => {
      console.log(data)
     },
     error => console.log(error))
  }

}
