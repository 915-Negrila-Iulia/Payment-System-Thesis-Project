import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountsHistoryComponent } from './accounts-history.component';

describe('AccountsHistoryComponent', () => {
  let component: AccountsHistoryComponent;
  let fixture: ComponentFixture<AccountsHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AccountsHistoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
