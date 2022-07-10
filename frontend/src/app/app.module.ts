import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';

import { HttpClientModule } from "@angular/common/http";
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginFailedComponent } from './login-failed/login-failed.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { UsersHistoryComponent } from './users-history/users-history.component';
import { UsersListComponent } from './users-list/users-list.component';
import { UsersHomeComponent } from './users-home/users-home.component';
import { AuditListComponent } from './audit-list/audit-list.component';

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
    AuditListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
