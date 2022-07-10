import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users-home',
  templateUrl: './users-home.component.html',
  styleUrls: ['./users-home.component.scss']
})
export class UsersHomeComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  viewUsers(){
    this.router.navigate(['users-list']);
  }

  registerUser(){
    this.router.navigate(['/register']);
  }

  viewHistory(){
    this.router.navigate(['/users-history']);
  }

}
