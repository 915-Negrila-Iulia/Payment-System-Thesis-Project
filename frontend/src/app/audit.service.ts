import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Audit } from './audit';

@Injectable({
  providedIn: 'root'
})
export class AuditService {

  baseUrl = 'http://localhost:8080/api/audit';

  constructor(private httpClient: HttpClient) { }

  getAudit(){
    return this.httpClient.get<Audit[]>(this.baseUrl);
  }

  getAuditOfObject(id: number, type: string){
    return this.httpClient.get<Audit[]>(this.baseUrl+`/${id}/${type}`);
  }

}
