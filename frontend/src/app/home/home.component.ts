import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TransactionService } from '../transaction.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
    if (!localStorage.getItem('foo')) { 
      localStorage.setItem('foo', 'no reload') 
      location.reload() 
    } else {
      localStorage.removeItem('foo') 
    }
  }

  goToUsersHomePage(){
    this.router.navigate(['/users-home']);
  }

  goToPersonsHomePage(){
    this.router.navigate(['/persons-home']);
  }

  goToAccountsHomePage(){
    this.router.navigate(['/accounts-home']);
  }

  goToBalancesHomePage(){
    this.router.navigate(['/balances-list']);
  }

  goToTransactionsHomePage(){
    this.router.navigate(['/transactions-home']);
  }

  goToAuditListPage(){
    this.router.navigate(['audit-list']);
  }

  logout(){
    sessionStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
