import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Account } from '../account';
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
  types: any = ['INTERNAL','EXTERNAL'];
  actions: any = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'];
  transaction: Transaction = new Transaction();
  errorMessage = '';
  targetAccounts: any;
  accountDetails: Account = new Account();
  transactionForm: any;

  // targetIban: any;

  constructor(private accountService: AccountService, private transactionService: TransactionService) { }

  ngOnInit(): void {
    this.setIbanById();

    this.accountService.getValidAccounts().subscribe(data => {
      this.targetAccounts = data;
      console.log(this.targetAccounts);
    })

    this.transactionForm = new FormGroup({
      iban: new FormControl({value: '', disabled: true}, Validators.required),
      amount: new FormControl('',[Validators.required]),
      reenteredAmount: new FormControl('',[]),
      type: new FormControl('',[Validators.required]),
      action: new FormControl('',[Validators.required]),
      targetAccount: new FormControl('',[])
    })
  }

  ngOnChanges(): void {
    console.log(this.accountId);
  }

  onSubmit(){
    this.setIbanById();

    var myTransaction = this.transactionForm.value;
    var targetIban;

    if(myTransaction.action !== 'TRANSFER'){
      targetIban = this.iban.value;
    }
    else{
      targetIban = this.transactionForm.value.targetAccount;
    }

    if(myTransaction.amount === myTransaction.reenteredAmount || myTransaction.amount < 1000){

      this.accountService.getAccountByIban(targetIban).subscribe(data => {
        this.transaction.type = myTransaction.type;
        this.transaction.amount = myTransaction.amount;
        this.transaction.action = myTransaction.action;
        let actionPerformed = myTransaction.action;
        this.transaction.accountID = this.accountId;
        if(data != null){
          this.transaction.targetAccountID = data.id;
        }
        else{
          this.transaction.targetIban = myTransaction.targetAccount;
        }
        this.transaction.status = "APPROVE";
        this.transaction.nextStatus = "ACTIVE";
        console.log(this.transaction);
        if(actionPerformed === "DEPOSIT"){
          this.transactionService.deposit(this.transaction).subscribe(data => {
            console.log(data);
            window.location.reload();
          },
          error => {
            this.errorMessage = error.error;
          });
        }
        else if(actionPerformed === "WITHDRAWAL"){
          this.transactionService.withdrawal(this.transaction).subscribe(data => {
            console.log(data);
            window.location.reload();
          },
          error => {
            this.errorMessage = error.error;
          });
        }
        else if(actionPerformed === "TRANSFER"){
          this.transactionService.transfer(this.transaction).subscribe(data => {
            console.log(data);
            window.location.reload();
          },
          error => {
            console.log(error);
            this.errorMessage = error.error;
          });
        }
      },
      err => {
        console.log("null");
        console.log(err);
      })
    }
    else{
      this.errorMessage = "Reentered Amount is not the same";
    }
}

  get iban(){
    return this.transactionForm.get('iban');
  }

  get amount(){
    return this.transactionForm.get('amount');
  }

  get reenteredAmount(){
    return this.transactionForm.get('reenteredAmount');
  }

  get action(){
    return this.transactionForm.get('action');
  }

  get targetAccount(){
    return this.transactionForm.get('targetAccount');
  }

  setIbanById(){
    this.accountService.getAccountById(this.accountId).subscribe(data => {
      this.transactionForm.patchValue({
        iban: data.iban
      });
    })
  }

}
