import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Balance } from './balance';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {

  baseUrl = 'http://localhost:8080/api';

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
}
