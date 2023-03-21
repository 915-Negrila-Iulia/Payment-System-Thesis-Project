import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Account } from './account';
import { AccountHistory } from './account-history';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  baseUrl = 'http://localhost:8080/api/accounts';
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api/accounts';
  currentUserId = sessionStorage.getItem('userID');
  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();

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

  getAccountsOfUser(){
    return this.httpClient.get<Account[]>(this.baseUrl+`/user/${this.currentUserId}`);
  }

  getAccountsByStatus(status: string){
    return this.httpClient.get<Account[]>(this.baseUrl+`/status/${status}`);
  }

  getHistoryOfAccounts(){
    return this.httpClient.get<AccountHistory[]>(this.baseUrl+"/history");
  }

  getAccountsHistoryOfUser(){
    return this.httpClient.get<AccountHistory[]>(this.baseUrl+`/history/user/${this.currentUserId}`);
  }

  getAccountsHistoryByAccountId(accountId: number | undefined){
    return this.httpClient.get<AccountHistory[]>(this.baseUrl+`/history/${accountId}`);
  }

  updateAccount(id: number | undefined, account: Account){
    this.objectDto.currentUserDto.objectId = id;
    this.objectDto.object = account;
    return this.httpClient.put<Account>(this.baseUrl,this.objectDto);
  }

  deleteAccount(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Account>(this.baseUrl+"/delete",this.currentUserDto);
  }

  approveAccount(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Account>(this.baseUrl+"/approve",this.currentUserDto);
  }

  rejectAccount(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Account>(this.baseUrl+"/reject",this.currentUserDto);
  }

  getAccountById(id: number | undefined){
    return this.httpClient.get<Account>(this.baseUrl+`/${id}`);
  }
}
