import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonsHistoryComponent } from './persons-history.component';

describe('PersonsHistoryComponent', () => {
  let component: PersonsHistoryComponent;
  let fixture: ComponentFixture<PersonsHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonsHistoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonsHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
