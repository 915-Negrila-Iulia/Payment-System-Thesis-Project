import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Balance } from './balance';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {

  baseUrl = 'http://localhost:8080/api';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api';
  currentUserId = sessionStorage.getItem('userID');
  role = sessionStorage.getItem('role');

  constructor(private httpClient: HttpClient) { }

  getAllBalances(){
    return this.role=="ADMIN_ROLE" ? this.httpClient.get<Balance[]>(this.baseUrl+"/balances") : this.getAllBalancesOfUser();
  }

  getAllBalancesOfUser(){
    return this.httpClient.get<Balance[]>(this.baseUrl+`/balances/user/${this.currentUserId}`);
  }

  getBalancesOfAccount(id: number){
    return this.httpClient.get<Balance[]>(`${this.baseUrl}/balances/${id}`);
  }

  getCurrentBalanceOfAccount(id: number){
    return this.httpClient.get<Balance>(`${this.baseUrl}/current-balance/${id}`);
  }

  getBalancesInDateRange(startDate: string | undefined, endDate: string | undefined){
    return this.role=="ADMIN_ROLE" ? this.httpClient.get<Balance[]>(`${this.baseUrl}/balances/filter/${startDate}/${endDate}`) : this.getBalancesOfUserInDateRange(startDate, endDate);
  }

  getBalancesOfUserInDateRange(startDate: string | undefined, endDate: string | undefined){
    return this.httpClient.get<Balance[]>(`${this.baseUrl}/balances/user/filter/${this.currentUserId}/${startDate}/${endDate}`);
  } 
}
