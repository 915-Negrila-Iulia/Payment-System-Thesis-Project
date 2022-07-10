import { Component, OnInit } from '@angular/core';
import { Audit } from '../audit';
import { AuditService } from '../audit.service';

@Component({
  selector: 'app-audit-list',
  templateUrl: './audit-list.component.html',
  styleUrls: ['./audit-list.component.scss']
})
export class AuditListComponent implements OnInit {

  audit: Audit[] = [];

  constructor(private auditService: AuditService) { }

  ngOnInit(): void {
    this.getAudit();
  }

  getAudit(){
    this.auditService.getAudit().subscribe(data => {
      this.audit = data;
    })
  }

}
