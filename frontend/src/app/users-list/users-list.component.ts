import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { SelectMultipleControlValueAccessor } from '@angular/forms';
import { Router } from '@angular/router';
import { AuditService } from '../audit.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit {

  users: User[] = [];
  user: User = new User();
  modifierUserID: number | undefined;
  objectType = 'USER';
  errorMessage = '';

  constructor(private userService: UserService, private auditService: AuditService) { }

  ngOnInit(): void {
    this.getUsers();
  }

  delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
  }

  getUsers(){
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
    })
  }

  getCurrentUser(){
    return sessionStorage.getItem('userID');
  }

  updateUser(id: any, username: any, email: any, password: any, status: any){
    this.user.id = id;
    this.user.username = username;
    this.user.email = email.value;
    this.user.password = password;
    this.user.status = status;
    this.userService.updateUser(id,this.user).subscribe(data => {
      console.log(data);
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
      console.log(error.error)
    }
    );
  }

  deleteUser(id: any){
    this.userService.deleteUser(id).subscribe(data => {
      console.log(data)
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
      console.log(error.error)
    }  
    )
  }

  filterById(id: any){
    return this.users.filter( user => user.id === id)[0];
  }

  approveUser(id: any){
    this.userService.approveUser(id).subscribe(data => {
      console.log(data);
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
      console.log(error.error)
    }
    );
  }

  rejectUser(id: any){
    this.userService.rejectUser(id).subscribe(data => {
      console.log(data);
      window.location.reload();
    },
    error => {
      this.errorMessage = error.error;
      console.log(error.error)
    }
    );
  }

}
