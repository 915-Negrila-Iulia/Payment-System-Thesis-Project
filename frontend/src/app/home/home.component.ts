import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  users: User[] = [];

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  getUsers(){
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
    })
  }

  registerUser(){
    this.router.navigate(['/register']);
  }

}
