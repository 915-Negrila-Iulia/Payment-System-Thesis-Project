import { Component, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './user';
import { UserService } from './user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'frontend';
  token = sessionStorage.getItem('token');
  username = sessionStorage.getItem('username');
  role = sessionStorage.getItem('role');
  displayRole = (this.role == "ADMIN_ROLE") ? "Admin" : "User";

  constructor(private router: Router) { }

  logout(){
    sessionStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

}
