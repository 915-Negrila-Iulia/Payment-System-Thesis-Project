import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Transaction } from './transaction';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  baseUrl = "http://localhost:8080/api/transactions";
  currentUserId = sessionStorage.getItem('userID');

  constructor(private httpClient: HttpClient) { }

  getAllTransactions(){
    return this.httpClient.get<Transaction[]>(this.baseUrl);
  }

  deposit(transaction: Transaction){
    return this.httpClient.put<Transaction>(`${this.baseUrl}/deposit/${this.currentUserId}`,transaction);
  }

  withdrawal(transaction: Transaction){
    return this.httpClient.put<Transaction>(`${this.baseUrl}/withdrawal/${this.currentUserId}`,transaction);
  }

  transfer(transaction: Transaction){
    return this.httpClient.put<Transaction>(`${this.baseUrl}/transfer/${this.currentUserId}`,transaction);
  }

  approveTransaction(id: number | undefined){
    return this.httpClient.put<Transaction>(this.baseUrl+"/approve/"+`${id}/${this.currentUserId}`,null);
  }

  rejectTransaction(id: number | undefined){
    return this.httpClient.put<Transaction>(this.baseUrl+"/reject/"+`${id}/${this.currentUserId}`,null);
  }
}
