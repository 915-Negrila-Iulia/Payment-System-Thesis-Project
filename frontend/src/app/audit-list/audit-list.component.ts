import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { Audit } from '../audit';
import { AuditService } from '../audit.service';
import { User } from '../user';
import { UserService } from '../user.service';

@Component({
  selector: 'app-audit-list',
  templateUrl: './audit-list.component.html',
  styleUrls: ['./audit-list.component.scss']
})
export class AuditListComponent implements OnInit, OnDestroy {

  audit: Audit[] = [];
  users: User[] = [];
  subscribe: any;
  objectId: any;
  objectType: any;
  historyRoute: any;

  constructor(private auditService: AuditService, private userService: UserService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.subscribe = this.activatedRoute.paramMap.subscribe(params => {
      this.objectId = Number(params.get('id'));
      this.objectType = params.get('obj');
      if(this.objectId !== 0 && this.objectType !== null){
        this.getAuditOfObject();
      }
      else {
        this.getAudit();
      }
    })
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
      })
  }

  ngOnDestroy(): void {
    this.subscribe.unsubscribe();
  }

  getAudit(){
    this.auditService.getAudit().subscribe(data => {
      this.audit = data;
    })
  }

  getAuditOfObject(){
    this.auditService.getAuditOfObject(this.objectId, this.objectType).subscribe(data => {
      this.audit = data;
    });
  }

  getUsername(id: number | undefined){
    if(this.users){
      let user = this.users.filter(user => user.id === id)[0];
      if(user){
        return user.username;
      }
      return '';
    }
    else{
      return '';
    }
  }

}
