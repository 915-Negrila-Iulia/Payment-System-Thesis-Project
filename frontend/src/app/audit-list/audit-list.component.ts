import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { Audit } from '../audit';
import { AuditService } from '../audit.service';

@Component({
  selector: 'app-audit-list',
  templateUrl: './audit-list.component.html',
  styleUrls: ['./audit-list.component.scss']
})
export class AuditListComponent implements OnInit, OnDestroy {

  audit: Audit[] = [];
  subscribe: any;
  objectId: any;
  objectType: any;
  historyRoute: any;

  constructor(private auditService: AuditService, private activatedRoute: ActivatedRoute) { }

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

}
