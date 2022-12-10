import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllmybooksComponent } from './allmybooks.component';

describe('AllmybooksComponent', () => {
  let component: AllmybooksComponent;
  let fixture: ComponentFixture<AllmybooksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllmybooksComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllmybooksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
