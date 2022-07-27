import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
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
  displayedColumns: string[] = ['#', 'firstName', 'lastName', 'address', 'dateOfBirth', 'phoneNumber', 'user', 'status', 'next status', 'timestamp'];
  dataSource!: MatTableDataSource<PersonHistory>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;


  constructor(private personService: PersonService, private userService: UserService) { }

  ngOnInit(): void {
    this.getPersonsHistory();
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
      this.dataSource = new MatTableDataSource(this.personsHistory);
      this.dataSource.paginator = this.paginator;
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
