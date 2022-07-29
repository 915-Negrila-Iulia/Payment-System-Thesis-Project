import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CurrentUserDto } from './current-user-dto';
import { ObjectDto } from './object-dto';
import { Transaction } from './transaction';
import { TransactionHistory } from './transaction-history';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  baseUrl = "http://localhost:8080/api/transactions";
  currentUserId = sessionStorage.getItem('userID');

  objectDto: ObjectDto = new ObjectDto();
  currentUserDto: CurrentUserDto = new CurrentUserDto();

  constructor(private httpClient: HttpClient) { }

  getAllTransactions(){
    return this.httpClient.get<Transaction[]>(this.baseUrl);
  }

  getTransactionsByAccountId(id: number | undefined){
    return this.httpClient.get<Transaction[]>(this.baseUrl+`/account/${id}`);
  }

  getTransactionsByStatus(status: string | undefined){
    return this.httpClient.get<Transaction[]>(this.baseUrl+`/status/${status}`);
  }

  getHistoryOfTransactions(){
    return this.httpClient.get<TransactionHistory[]>(this.baseUrl+"/history");
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
}
