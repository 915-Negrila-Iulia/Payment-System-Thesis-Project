import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Account } from '../account';
import { AuditService } from '../audit.service';
import { ObjectStateUtils } from '../object-state-utils';
import { Person } from '../person';
import { User } from '../user';

@Component({
  selector: 'app-object-status-history',
  templateUrl: './object-status-history.component.html',
  styleUrls: ['./object-status-history.component.scss']
})
export class ObjectStatusHistoryComponent implements OnInit, OnDestroy {

  object: any;
  subscribe: any;
  objStateDto: ObjectStateUtils = new ObjectStateUtils();

  constructor(private auditService: AuditService, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.subscribe = this.activatedRoute.paramMap.subscribe(params => {
      this.objStateDto.timestamp = params.get('timestamp');
      this.objStateDto.type = params.get('type');
      this.auditService.getObjectState(this.objStateDto).subscribe(data => {
        this.object = data[0];
      })
    })
  }

  ngOnDestroy(): void {
    this.subscribe.unsubscribe();
  }

}
