import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalyticsSamplingResultsComponent } from './analytics-sampling-results.component';

describe('AnalyticsSamplingResultsComponent', () => {
  let component: AnalyticsSamplingResultsComponent;
  let fixture: ComponentFixture<AnalyticsSamplingResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalyticsSamplingResultsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalyticsSamplingResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
