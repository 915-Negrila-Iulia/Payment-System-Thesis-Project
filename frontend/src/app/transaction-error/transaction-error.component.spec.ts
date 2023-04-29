import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionErrorComponent } from './transaction-error.component';

describe('TransactionErrorComponent', () => {
  let component: TransactionErrorComponent;
  let fixture: ComponentFixture<TransactionErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionErrorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
