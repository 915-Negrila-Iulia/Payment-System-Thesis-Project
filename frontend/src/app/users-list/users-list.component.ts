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

  getUserWhoMadeChanges(id: any){
    this.userService.getUserWhoMadeChanges(id,"USER").subscribe(data => {
        this.modifierUserID = data.id;
    });
    return this.modifierUserID?.toString();
  }

  updateUser(id: any, username: any, email: any, password: any, status: any){
    this.user.id = id;
    this.user.username = username;
    this.user.email = email.value;
    this.user.password = password;
    this.user.status = status;
    this.userService.updateUser(id,this.user).subscribe(data => {
      //console.log(data);
    },
    error => console.log(error)
    );
    window.location.reload();
  }

  deleteUser(id: any){
    this.userService.deleteUser(id).subscribe(data => {
      console.log(data)
    },
    error => console.log(error))
    window.location.reload();
  }

  filterById(id: any){
    return this.users.filter( user => user.id === id)[0];
  }

  approveUser(id: any){
    console.log(this.getUserWhoMadeChanges(id))
    console.log("sess"+this.getCurrentUser())
    //if(this.getUserWhoMadeChanges(id) && this.getUserWhoMadeChanges(id) !== this.getCurrentUser()){
      this.userService.approveUser(id).subscribe(data => {
        //console.log(data);
      },
      error => console.log(error)
      );
    //}
    console.log(this.getCurrentUser());
    //window.location.reload();
  }

  rejectUser(id: any){
    this.userService.rejectUser(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error))
    window.location.reload();
  }

}
