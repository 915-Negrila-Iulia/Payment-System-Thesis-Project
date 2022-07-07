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
      console.log(data);
      if(data){
        console.log("succeded");
        this.goToHomePage();
      }
      else{
        console.log("failed");
        this.goToFailedLoginPage();
      }   
    },
    error => console.log(error));
  }

}
