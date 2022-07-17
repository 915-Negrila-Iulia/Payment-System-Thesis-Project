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

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
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
        sessionStorage.setItem('token', this.sessionId);
        sessionStorage.setItem('userID', this.userID);
        this.goToHomePage();
      }
      else{
        this.goToFailedLoginPage();
      }
    },
    error => this.goToFailedLoginPage())
  }

}
