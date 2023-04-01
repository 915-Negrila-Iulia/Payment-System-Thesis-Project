import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoggerService {

  constructor() { }

  logStatus(message: string){
    let currentDateTime = new Date();
    let currentDateTimeString = currentDateTime.toDateString();
    console.log(`${currentDateTimeString}: `, message);
  }
}
