import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  userCreds: User = new User();
  sessionId: any = "";
  userID: any;
  username: any ="";
  role: any = "";

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    if (!localStorage.getItem('foo')) { 
      localStorage.setItem('foo', 'no reload') 
      location.reload() 
    } else {
      localStorage.removeItem('foo') 
    }
  }

  goToHomePage(){
    this.router.navigate(['/home']);
  }

  goToFailedLoginPage(){
    this.router.navigate(['/login-failed']);
  }

  onSubmit(){
    this.userService.authenticate(this.userCreds).subscribe(data => {
      if(data){
        console.log(data);
        this.sessionId = data.accessToken; //sessionId is the token of the current user logged
        this.userID = data.id;
        this.username = data.username;
        sessionStorage.setItem('token', this.sessionId);
        sessionStorage.setItem('userID', this.userID);
        sessionStorage.setItem('username',this.username);
        sessionStorage.setItem('role', data.role);
        this.goToHomePage();
      }
      else{
        this.goToFailedLoginPage();
      }
    },
    error => this.goToFailedLoginPage())
  }

}
