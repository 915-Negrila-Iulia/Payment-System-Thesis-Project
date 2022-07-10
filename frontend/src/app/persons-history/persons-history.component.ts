import { Component, OnInit } from '@angular/core';
import { PersonHistory } from '../person-history';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-persons-history',
  templateUrl: './persons-history.component.html',
  styleUrls: ['./persons-history.component.scss']
})
export class PersonsHistoryComponent implements OnInit {

  personsHistory: PersonHistory[] = [];

  constructor(private personService: PersonService) { }

  ngOnInit(): void {
    this.getPersonsHistory();
  }

  getPersonsHistory(){
    this.personService.getHistoryOfPersons().subscribe(data => {
      this.personsHistory = data;
    })
  }

}
