import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Account } from './account';
import { AccountHistory } from './account-history';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  baseUrl = 'http://localhost:8080/api/accounts';

  constructor(private httpClient: HttpClient) { }

  addAccount(account: Account){
    return this.httpClient.post<Account>(this.baseUrl, account);
  }

  getAllAccounts(){
    return this.httpClient.get<Account[]>(this.baseUrl);
  }

  getHistoryOfAccounts(){
    return this.httpClient.get<AccountHistory[]>(this.baseUrl+"/history");
  }

  updateAccount(id: number | undefined, account: Account){
    return this.httpClient.put<Account>(this.baseUrl+`/${id}`,account);
  }

  approveAccount(id: number | undefined){
    return this.httpClient.put<Account>(this.baseUrl+"/approve/"+`${id}`,null);
  }
}
