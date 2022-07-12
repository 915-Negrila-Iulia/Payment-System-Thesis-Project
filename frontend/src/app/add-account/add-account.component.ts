import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Account } from '../account';
import { AccountService } from '../account.service';

@Component({
  selector: 'app-add-account',
  templateUrl: './add-account.component.html',
  styleUrls: ['./add-account.component.scss']
})
export class AddAccountComponent implements OnInit {

  account: Account = new Account();
  addAccountForm = new FormGroup({
    iban: new FormControl('',[Validators.required]),
    countryCode: new FormControl('',[Validators.required, Validators.pattern('^[A-Z]{3}$')]),
    bankCode: new FormControl('',[Validators.required]),
    currency: new FormControl('',[Validators.required, Validators.pattern('^[A-Z]{3}$')]),
    accountStatus: new FormControl('',[Validators.required, Validators.pattern('^[A-Z ]{3,}$')]),
    personID: new FormControl('',[Validators.required]),
  })

  constructor(private accountService: AccountService, private router: Router) { }

  ngOnInit(): void {
  }

  onSubmit(){
    this.account = this.addAccountForm.value;
    this.account.status = "APPROVE";
    console.log(this.account);
    this.accountService.addAccount(this.account).subscribe(data => {
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

  get iban(){
    return this.addAccountForm.get('iban');
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
