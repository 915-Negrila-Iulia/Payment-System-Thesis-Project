import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginFailedComponent } from './login-failed/login-failed.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { UsersHistoryComponent } from './users-history/users-history.component';
import { UsersListComponent } from './users-list/users-list.component';
import { UsersHomeComponent } from './users-home/users-home.component';
import { AuditListComponent } from './audit-list/audit-list.component';
import { PersonsListComponent } from './persons-list/persons-list.component';
import { PersonsHomeComponent } from './persons-home/persons-home.component';
import { AddPersonComponent } from './add-person/add-person.component';
import { PersonsHistoryComponent } from './persons-history/persons-history.component';
import { RequestInterceptor } from './request.interceptor';
import { AccountsHomeComponent } from './accounts-home/accounts-home.component';
import { AccountsListComponent } from './accounts-list/accounts-list.component';
import { AddAccountComponent } from './add-account/add-account.component';
import { AccountsHistoryComponent } from './accounts-history/accounts-history.component';
import { CurrentBalanceComponent } from './current-balance/current-balance.component';
import { BalanceHistoryComponent } from './balance-history/balance-history.component';
import { TransactionsAccountComponent } from './transactions-account/transactions-account.component';
import { TransactionsListComponent } from './transactions-list/transactions-list.component';
import { BalancesListComponent } from './balances-list/balances-list.component';
import { ObjectStatusHistoryComponent } from './object-status-history/object-status-history.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TransactionsHomeComponent } from './transactions-home/transactions-home.component';
import { TransactionsHistoryComponent } from './transactions-history/transactions-history.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    LoginFailedComponent,
    HomeComponent,
    RegisterComponent,
    UsersHistoryComponent,
    UsersListComponent,
    UsersHomeComponent,
    AuditListComponent,
    PersonsListComponent,
    PersonsHomeComponent,
    AddPersonComponent,
    PersonsHistoryComponent,
    AccountsHomeComponent,
    AccountsListComponent,
    AddAccountComponent,
    AccountsHistoryComponent,
    CurrentBalanceComponent,
    BalanceHistoryComponent,
    TransactionsAccountComponent,
    TransactionsListComponent,
    BalancesListComponent,
    ObjectStatusHistoryComponent,
    TransactionsHomeComponent,
    TransactionsHistoryComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatPaginatorModule,
    MatTableModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSlideToggleModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: RequestInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }
