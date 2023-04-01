import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../user';
import { UserService } from '../user.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  checked: boolean = false;
  user: User = new User();
  registerForm = new FormGroup({
    username: new FormControl('',[Validators.required]),
    email: new FormControl('',[Validators.required, Validators.email]),
    password: new FormControl('',[Validators.required, Validators.pattern('(?=.*[a-z])(?=.*[0-9]).{8,}')]),
    role: new FormControl('',[])
  })

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit(): void {
  }

  slideToggleChange(event: any){
    if(event.source.checked){
      this.registerForm.controls['role'].setValue("admin");
    }
    else{
      this.registerForm.controls['role'].setValue(null);
    }
  }

  onSubmit(){
    this.user = this.registerForm.value;
    this.user.status = "APPROVE";
    if(this.user.role == "admin"){
      this.user.role = "ADMIN_ROLE";
    }
    else{
      this.user.role = "USER_ROLE";
    }
    console.log(this.user);
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

  get username(){
    return this.registerForm.get('username');
  }

  get email(){
    return this.registerForm.get('email');
  }

  get password(){
    return this.registerForm.get('password');
  }
}
