import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Person } from '../person';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-add-person',
  templateUrl: './add-person.component.html',
  styleUrls: ['./add-person.component.scss']
})
export class AddPersonComponent implements OnInit {

  person: Person = new Person();
  addPersonForm = new FormGroup({
    firstName: new FormControl('',[Validators.required]),
    lastName: new FormControl('',[Validators.required]),
    address: new FormControl('',[Validators.required, Validators.pattern('^[a-zA-Z -]{3,}/[a-zA-Z -]{3,}/[0-9]{3,}/[a-zA-Z0-9 -]{3,}$')]),
    dateOfBirth: new FormControl('',[Validators.required, Validators.pattern('^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$')]),
    phoneNumber: new FormControl('',[Validators.required, Validators.pattern('^[0-9]{10}$')]),
    userID: new FormControl('',[Validators.required]),
  })

  constructor(private personService: PersonService, private router: Router) { }

  ngOnInit(): void {
  }

  onSubmit(){
    this.person = this.addPersonForm.value;
    this.person.status = "APPROVE";
    console.log(this.person);
    this.personService.addPerson(this.person).subscribe(data => {
      console.log(data);
      if(data){
        console.log("succeded");
      }
      else{
        console.log("failed");
      }   
    },
    error => console.log(error));
  }

  get firstName(){
    return this.addPersonForm.get('firstName');
  }

  get lastName(){
    return this.addPersonForm.get('lastName');
  }

  get address(){
    return this.addPersonForm.get('address');
  }

  get dateOfBirth(){
    return this.addPersonForm.get('dateOfBirth');
  }

  get phoneNumber(){
    return this.addPersonForm.get('phoneNumber');
  }

  get userID(){
    return this.addPersonForm.get('userID');
  }

}
