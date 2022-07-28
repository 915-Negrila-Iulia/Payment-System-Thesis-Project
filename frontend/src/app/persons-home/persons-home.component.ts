import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-persons-home',
  templateUrl: './persons-home.component.html',
  styleUrls: ['./persons-home.component.scss']
})
export class PersonsHomeComponent implements OnInit {

  show: string = "personsList";
  eventsSubject: Subject<void> = new Subject<void>();

  constructor(private router: Router) { 
  }

  ngOnInit(): void {
  }

  viewPersons(){
    this.show = 'personsList';
    window.location.reload();
  }

  addPerson(){
    this.show = 'addPerson';
  }

  viewHistory(){
    this.show = 'personsHistory';
    this.eventsSubject.next();
  }
}
