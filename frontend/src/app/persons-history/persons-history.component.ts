import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { PersonHistory } from '../person-history';
import { PersonService } from '../person.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-persons-history',
  templateUrl: './persons-history.component.html',
  styleUrls: ['./persons-history.component.scss']
})
export class PersonsHistoryComponent implements OnInit, OnDestroy {

  personsHistory: PersonHistory[] = [];
  users: User[] = [];
  persons: any;
  selectedPerson: any;

  displayedColumns: string[] = ['#', 'firstName', 'lastName', 'address', 'dateOfBirth', 'phoneNumber', 'user', 'status', 'next status', 'timestamp'];
  dataSource!: MatTableDataSource<PersonHistory>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  private eventsSubscription!: Subscription;
  @Input() 
  events!: Observable<void>;

  constructor(private personService: PersonService, private userService: UserService) { }

  ngOnInit(): void {
    this.getPersonsHistory();
    this.getPersonsToSelect();
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
      this.dataSource = new MatTableDataSource(this.personsHistory);
      this.dataSource.paginator = this.paginator;
    })
    this.eventsSubscription = this.events.subscribe(() => this.getPersonsHistory());
  }

  ngOnDestroy() {
    this.eventsSubscription.unsubscribe();
  }

  getPersonsHistory(){
    this.personService.getHistoryOfPersons().subscribe(data => {
      this.personsHistory = data;
      this.dataSource = new MatTableDataSource(this.personsHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  getMyPersonsHistory(){
    this.personService.getPersonsHistoryOfUser().subscribe(data => {
      this.personsHistory = data;
      this.dataSource = new MatTableDataSource(this.personsHistory);
      this.dataSource.paginator = this.paginator;
    })
  }

  filterByPerson(){
    if(this.selectedPerson){
      var personTokens = this.selectedPerson.split(' ');
      var firstName = String(personTokens[0]);
      var lastName = String(personTokens[1]);
      var phoneNumber = String(personTokens[2]);

      this.personService.getPersonByDetails(firstName,lastName,phoneNumber).subscribe(data => {
        let id = data.id;
        console.log(id);
        this.personService.getPersonsHistoryByPersonId(id).subscribe(history => {
          this.personsHistory = history;
          this.dataSource = new MatTableDataSource(this.personsHistory);
          this.dataSource.paginator = this.paginator;
        })
      })
      this.selectedPerson = '';
    }
  }

  getPersonsToSelect(){
    this.personService.getAllPersons().subscribe(data => {
      this.persons = data;
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
