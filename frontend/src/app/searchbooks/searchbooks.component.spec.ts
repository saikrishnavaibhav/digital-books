import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchbooksComponent } from './searchbooks.component';

describe('SearchbooksComponent', () => {
  let component: SearchbooksComponent;
  let fixture: ComponentFixture<SearchbooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchbooksComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchbooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
