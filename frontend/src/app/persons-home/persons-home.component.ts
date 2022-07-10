import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-persons-home',
  templateUrl: './persons-home.component.html',
  styleUrls: ['./persons-home.component.scss']
})
export class PersonsHomeComponent implements OnInit {

  show: string | undefined;

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  viewPersons(){
    this.show = 'personsList';
  }

  addPerson(){
    this.show = 'addPerson';
  }

  viewHistory(){
    this.show = 'personsHistory';
  }
}
