import { TestBed } from '@angular/core/testing';

import { BookstorageService } from './bookstorage.service';

describe('BookstorageService', () => {
  let service: BookstorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookstorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
