import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Audit } from './audit';
import { ObjectStateUtils } from './object-state-utils';

@Injectable({
  providedIn: 'root'
})
export class AuditService {

  baseUrl = 'http://localhost:8080/api/audit';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api/audit';
  currentUserId = sessionStorage.getItem('userID');
  role = sessionStorage.getItem('role');

  constructor(private httpClient: HttpClient) { }

  getAudit(){
    return this.role == "ADMIN_ROLE" ? this.httpClient.get<Audit[]>(this.baseUrl) : this.getAuditOfCurrentUser();
  }

  getAuditOfCurrentUser(){
    return this.httpClient.get<Audit[]>(this.baseUrl+`/user/${this.currentUserId}`);
  }

  getAuditOfObject(id: number, type: string){
    return this.httpClient.get<Audit[]>(this.baseUrl+`/${id}/${type}`);
  }

  getObjectState(objStateDto: ObjectStateUtils){
    return this.httpClient.post<any>(this.baseUrl+`/history`,objStateDto);
  }

}
