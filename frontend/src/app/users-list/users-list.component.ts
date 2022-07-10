import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(){
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
    })
  }

  updateUser(id: any, username: any, email: any, password: any, status: any){
    this.user.id = id;
    this.user.username = username.value;
    this.user.email = email.value;
    this.user.password = password;
    this.user.status = status;
    this.userService.updateUser(id,this.user).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

  getUserById(id: any){
    return this.users.filter( user => user.id === id)[0];
  }

  approveUser(id: any){
    this.userService.approveUser(id).subscribe(data => {
      console.log(data);
    },
    error => console.log(error)
    );
  }

}
