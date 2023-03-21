import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Balance } from './balance';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {

  baseUrl = 'http://localhost:8080/api';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api';

  constructor(private httpClient: HttpClient) { }

  getAllBalances(){
    return this.httpClient.get<Balance[]>(this.baseUrl+"/balances");
  }

  getBalancesOfAccount(id: number){
    return this.httpClient.get<Balance[]>(`${this.baseUrl}/balances/${id}`);
  }

  getCurrentBalanceOfAccount(id: number){
    return this.httpClient.get<Balance>(`${this.baseUrl}/current-balance/${id}`);
  }

  getBalancesInDateRange(startDate: string | undefined, endDate: string | undefined){
    return this.httpClient.get<Balance[]>(`${this.baseUrl}/balances/${startDate}/${endDate}`);
  }
}
