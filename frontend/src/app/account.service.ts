import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Account } from './account';
import { AccountHistory } from './account-history';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  baseUrl = 'http://localhost:8080/api/accounts';
  currentUserId = sessionStorage.getItem('userID');

  constructor(private httpClient: HttpClient) { }

  addAccount(account: Account){
    return this.httpClient.post<Account>(this.baseUrl+`/${this.currentUserId}`, account);
  }

  getAllAccounts(){
    return this.httpClient.get<Account[]>(this.baseUrl);
  }

  getValidAccounts(){
    return this.httpClient.get<Account[]>(this.baseUrl+`/valid`);
  }

  getAccountByIban(iban: String){
    return this.httpClient.get<Account>(this.baseUrl+`/by-iban/${iban}`);
  }

  getHistoryOfAccounts(){
    return this.httpClient.get<AccountHistory[]>(this.baseUrl+"/history");
  }

  updateAccount(id: number | undefined, account: Account){
    return this.httpClient.put<Account>(this.baseUrl+`/${id}/${this.currentUserId}`,account);
  }

  deleteAccount(id: number | undefined){
    return this.httpClient.put<Account>(this.baseUrl+"/delete/"+`${id}/${this.currentUserId}`,null);
  }

  approveAccount(id: number | undefined){
    return this.httpClient.put<Account>(this.baseUrl+"/approve/"+`${id}/${this.currentUserId}`,null);
  }

  rejectAccount(id: number | undefined){
    return this.httpClient.put<Account>(this.baseUrl+"/reject/"+`${id}/${this.currentUserId}`,null);
  }

  getAccountById(id: number | undefined){
    return this.httpClient.get<Account>(this.baseUrl+`/${id}`);
  }
}
