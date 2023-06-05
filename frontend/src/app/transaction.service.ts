import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';
import { StatisticDto } from './statistic-dto';
import { Transaction } from './transaction';
import { TransactionHistory } from './transaction-history';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  baseUrl = "http://localhost:8080/api/transactions";
  //baseUrl = 'http://backendpaymentsystem-env.eba-ffkt3wf3.eu-west-1.elasticbeanstalk.com/api/transactions';
  currentUserId = sessionStorage.getItem('userID');
  role = sessionStorage.getItem('role');

  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();

  constructor(private httpClient: HttpClient) { }

  getAllTransactions(){
    return this.role=="ADMIN_ROLE" ?  this.httpClient.get<Transaction[]>(this.baseUrl) : this.getAllTransactionsByUserId();
  }

  getAllTransactionsByUserId(){
    return this.httpClient.get<Transaction[]>(this.baseUrl+`/user/${this.currentUserId}`);
  }

  getTransactionsByAccountId(id: number | undefined){
    return this.httpClient.get<Transaction[]>(this.baseUrl+`/account/${id}`);
  }

  getTransactionsByStatus(status: string | undefined){
    return this.httpClient.get<Transaction[]>(this.baseUrl+`/status/${status}`);
  }

  getHistoryOfTransactions(){
    return this.role=="ADMIN_ROLE" ?  this.httpClient.get<TransactionHistory[]>(this.baseUrl+"/history") : this.getTransactionsHistoryOfCurrentUser();
  }

  getTransactionsHistoryOfCurrentUser(){
    return this.httpClient.get<TransactionHistory[]>(this.baseUrl+`/history/user/${this.currentUserId}`);
  }

  deposit(transaction: Transaction){
    this.objectDto.object = transaction;
    return this.httpClient.put<Transaction>(`${this.baseUrl}/deposit`,this.objectDto);
  }

  withdrawal(transaction: Transaction){
    this.objectDto.object = transaction;
    return this.httpClient.put<Transaction>(`${this.baseUrl}/withdrawal`,this.objectDto);
  }

  transfer(transaction: Transaction){
    this.objectDto.object = transaction;
    return this.httpClient.put<Transaction>(`${this.baseUrl}/transfer`,this.objectDto);
  }

  approveTransaction(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Transaction>(this.baseUrl+"/approve",this.currentUserDto);
  }

  rejectTransaction(id: number | undefined){
    this.currentUserDto.objectId = id;
    return this.httpClient.put<Transaction>(this.baseUrl+"/reject",this.currentUserDto);
  }

  getStatisticsOfAccount(id: number | undefined){
    return this.httpClient.get<StatisticDto[]>(this.baseUrl+`/statistics/${id}`);
  }

  setFraudSystemClassifier(classifierType: string){
    const body = { "classifierType": classifierType };
    return this.httpClient.put<any>(`${this.baseUrl}/setClassifier`, body);
  }

  getFraudSystemClassifier(){
    return this.httpClient.get<any>(`${this.baseUrl}/getClassifier`);
  }

  getSystemTransactions(){
    return this.httpClient.get<any>(`${this.baseUrl}/getSystemTransactions`);
  }

}
