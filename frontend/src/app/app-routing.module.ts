import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountsHomeComponent } from './accounts-home/accounts-home.component';
import { AccountsListComponent } from './accounts-list/accounts-list.component';
import { AppComponent } from './app.component';
import { AuditListComponent } from './audit-list/audit-list.component';
import { AuthenticationGuard } from './authentication.guard';
import { BalancesListComponent } from './balances-list/balances-list.component';
import { HomeComponent } from './home/home.component';
import { LoginFailedComponent } from './login-failed/login-failed.component';
import { LoginComponent } from './login/login.component';
import { PersonsHomeComponent } from './persons-home/persons-home.component';
import { RegisterComponent } from './register/register.component';
import { TransactionsAccountComponent } from './transactions-account/transactions-account.component';
import { TransactionsListComponent } from './transactions-list/transactions-list.component';
import { UsersHistoryComponent } from './users-history/users-history.component';
import { UsersHomeComponent } from './users-home/users-home.component';
import { UsersListComponent } from './users-list/users-list.component';

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
    { path: 'transactions-list', component: TransactionsListComponent },
    { path: '**', redirectTo: 'home', pathMatch: 'full' }
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
