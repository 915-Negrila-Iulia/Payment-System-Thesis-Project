import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { SelectMultipleControlValueAccessor } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { AuditService } from '../audit.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit, AfterViewInit {

  users: User[] = [];
  user: User = new User();
  modifierUserID: number | undefined;
  objectType = 'USER';
  errorMessage = '';

  displayedColumns: string[] = ['#', 'username', 'email', 'status', 'next status', 'actions'];
  dataSource!: MatTableDataSource<User>;
  
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private userService: UserService, private auditService: AuditService) { }

  ngOnInit(): void {
    this.getUsers();
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
      this.dataSource = new MatTableDataSource(this.users);
      this.dataSource.paginator = this.paginator;
    })
  } 

  ngAfterViewInit() {
    // this.dataSource.paginator = this.paginator;
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
