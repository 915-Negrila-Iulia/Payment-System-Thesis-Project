import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../account.service';
import { Transaction } from '../transaction';
import { TransactionService } from '../transaction.service';

@Component({
  selector: 'app-transactions-account',
  templateUrl: './transactions-account.component.html',
  styleUrls: ['./transactions-account.component.scss']
})
export class TransactionsAccountComponent implements OnInit, OnChanges {

  @Input()
  accountId: number | undefined;
  types: any = ['INTERNAL', 'EXTERNAL'];
  actions: any = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'];
  transactionForm = new FormGroup({
    iban: new FormControl({value: '', disabled: true}, Validators.required),
    amount: new FormControl('',[Validators.required]),
    type: new FormControl('',[Validators.required]),
    action: new FormControl('',[Validators.required]),
    targetAccountID: new FormControl('',[]),
  })
  transaction: Transaction = new Transaction();

  constructor(private accountService: AccountService, private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.setIbanById();
  }

  ngOnChanges(): void {
    console.log(this.accountId);
  }

  onSubmit(){
    this.setIbanById();

    let actionPerformed = this.transactionForm.value.action;
    this.transaction = this.transactionForm.value;
    this.transaction.accountID = this.accountId;
    this.transaction.status = "APPROVE";
    this.transaction.nextStatus = "ACTIVE";
    console.log(this.transaction);
    if(actionPerformed === "DEPOSIT"){
      this.transactionService.deposit(this.transaction).subscribe(data => {
        console.log(data);
      },
      error => console.log(error));
    }
    else if(actionPerformed === "WITHDRAWAL"){
      this.transactionService.withdrawal(this.transaction).subscribe(data => {
        console.log(data);
      },
      error => console.log(error));
    }
    else if(actionPerformed === "TRANSFER"){
      this.transactionService.transfer(this.transaction).subscribe(data => {
        console.log(data);
      },
      error => console.log(error));
    }
}

  get amount(){
    return this.transactionForm.get('amount');
  }

  get action(){
    return this.transactionForm.get('action');
  }

  setIbanById(){
    this.accountService.getAccountById(this.accountId).subscribe(data => {
      this.transactionForm.patchValue({
        iban: data.iban
      });
    })
  }

}
