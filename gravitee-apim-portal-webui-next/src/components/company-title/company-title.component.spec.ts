/*
 * Copyright (C) 2024 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CompanyTitleComponent } from './company-title.component';

describe('CompanyTitleComponent', () => {
  let component: CompanyTitleComponent;
  let fixture: ComponentFixture<CompanyTitleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyTitleComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(CompanyTitleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
