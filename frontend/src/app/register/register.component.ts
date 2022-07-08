import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  user: User = new User();

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  onSubmit(){
    this.user.status = 'INACTIVE';
    this.userService.register(this.user).subscribe(data => {
      console.log(data);
      if(data){
        console.log("succeded");
      }
      else{
        console.log("failed");
      }   
    },
    error => console.log(error));
  }

  goToHomePage(){
    this.router.navigate(['/home']);
  }

}
