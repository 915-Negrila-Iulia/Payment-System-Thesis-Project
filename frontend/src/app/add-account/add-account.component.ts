import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from '../account';
import { AccountService } from '../account.service';
import { Person } from '../person';
import { PersonService } from '../person.service';

@Component({
  selector: 'app-add-account',
  templateUrl: './add-account.component.html',
  styleUrls: ['./add-account.component.scss']
})
export class AddAccountComponent implements OnInit {

  account: Account = new Account();
  personDetails: any;
  persons: any;
  addAccountForm: any;

  constructor(private accountService: AccountService, private personService: PersonService, private router: Router) { }

  ngOnInit(): void {
    this.personService.getPersonsOfUser().subscribe(data => {
      this.persons = data;
      console.log(this.persons);
    })

    this.addAccountForm = new FormGroup({
    countryCode: new FormControl('',[Validators.required, Validators.pattern('^[A-Z]{3}$')]),
    bankCode: new FormControl('',[Validators.required]),
    currency: new FormControl('',[Validators.required, Validators.pattern('^[A-Z]{3}$')]),
    accountStatus: new FormControl('',[Validators.required, Validators.pattern('^[A-Z ]{3,}$')]),
    person: new FormControl('',[Validators.required]),
  })
  }

  onSubmit(){
    var personTokens = this.addAccountForm.value.person.split(' ');
    var firstName = String(personTokens[0]);
    var lastName = String(personTokens[1]);
    var phoneNumber = String(personTokens[2]);
    var myAccount =  this.addAccountForm.value;

    this.personService.getPersonByDetails(firstName,lastName,phoneNumber).subscribe(data => {
      this.account = myAccount;
      this.personDetails = data;
      this.account.personID = this.personDetails.id;
      this.account.status = "APPROVE";
      this.account.iban = "";

      this.accountService.addAccount(this.account).subscribe(data => {
        console.log(data);
        if(data){
          console.log("succeded");
          window.location.reload();
        }
        else{
          console.log("failed");
        }   
      },
      error => console.log(error));
    })

  }

  get countryCode(){
    return this.addAccountForm.get('countryCode');
  }

  get bankCode(){
    return this.addAccountForm.get('bankCode');
  }

  get currency(){
    return this.addAccountForm.get('currency');
  }

  get accountStatus(){
    return this.addAccountForm.get('accountStatus');
  }

  get personID(){
    return this.addAccountForm.get('personID');
  }

}
