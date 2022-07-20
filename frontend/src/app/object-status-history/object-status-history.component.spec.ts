import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObjectStatusHistoryComponent } from './object-status-history.component';

describe('ObjectStatusHistoryComponent', () => {
  let component: ObjectStatusHistoryComponent;
  let fixture: ComponentFixture<ObjectStatusHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ObjectStatusHistoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ObjectStatusHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
