import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalyticsTableResultsComponent } from './analytics-table-results.component';

describe('AnalyticsTableResultsComponent', () => {
  let component: AnalyticsTableResultsComponent;
  let fixture: ComponentFixture<AnalyticsTableResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalyticsTableResultsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalyticsTableResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
