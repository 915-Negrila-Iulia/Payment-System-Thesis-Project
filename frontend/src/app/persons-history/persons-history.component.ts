import { Component, OnInit } from '@angular/core';
import { PersonHistory } from '../person-history';
import { PersonService } from '../person.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-persons-history',
  templateUrl: './persons-history.component.html',
  styleUrls: ['./persons-history.component.scss']
})
export class PersonsHistoryComponent implements OnInit {

  personsHistory: PersonHistory[] = [];
  users: User[] = [];

  constructor(private personService: PersonService, private userService: UserService) { }

  ngOnInit(): void {
    this.getPersonsHistory();
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
      })
  }

  getPersonsHistory(){
    this.personService.getHistoryOfPersons().subscribe(data => {
      this.personsHistory = data;
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

}
