import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionsAccountComponent } from './transactions-account.component';

describe('TransactionsAccountComponent', () => {
  let component: TransactionsAccountComponent;
  let fixture: ComponentFixture<TransactionsAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionsAccountComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionsAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
