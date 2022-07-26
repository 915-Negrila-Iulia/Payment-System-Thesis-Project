import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountsHomeComponent } from './accounts-home/accounts-home.component';
import { AuditListComponent } from './audit-list/audit-list.component';
import { AuthenticationGuard } from './authentication.guard';
import { BalancesListComponent } from './balances-list/balances-list.component';
import { HomeComponent } from './home/home.component';
import { LoginFailedComponent } from './login-failed/login-failed.component';
import { LoginComponent } from './login/login.component';
import { ObjectStatusHistoryComponent } from './object-status-history/object-status-history.component';
import { PersonsHomeComponent } from './persons-home/persons-home.component';
import { TransactionsHomeComponent } from './transactions-home/transactions-home.component';
import { TransactionsListComponent } from './transactions-list/transactions-list.component';
import { UsersHomeComponent } from './users-home/users-home.component';

const routes: Routes = [
  { path: '', canActivate:[AuthenticationGuard], children: [
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },
    { path: 'login-failed', component: LoginFailedComponent },
    { path: 'users-home', component: UsersHomeComponent },
    { path: 'audit-list', component: AuditListComponent },
    { path: 'audit-list/:id/:obj', component: AuditListComponent },
    { path: 'persons-home', component: PersonsHomeComponent },
    { path: 'accounts-home', component: AccountsHomeComponent },
    { path: 'balances-list', component: BalancesListComponent },
    { path: 'transactions-home', component: TransactionsHomeComponent },
    { path: 'object-status-history/:timestamp/:type', component: ObjectStatusHistoryComponent },
    { path: '**', redirectTo: 'home', pathMatch: 'full' }
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
