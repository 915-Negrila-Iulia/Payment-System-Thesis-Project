import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonsHomeComponent } from './persons-home.component';

describe('PersonsHomeComponent', () => {
  let component: PersonsHomeComponent;
  let fixture: ComponentFixture<PersonsHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PersonsHomeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PersonsHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
