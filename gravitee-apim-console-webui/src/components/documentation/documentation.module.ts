/*
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
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
import { NgModule } from '@angular/core';

import { DocumentationManagementComponent } from './documentation-management.component';
import { DocumentationNewPageComponent } from './new-page.component';
import { DocumentationEditPageComponent } from './edit-page.component';
import { DocumentationImportPagesComponent } from './import-pages.component';

import { ajsDocumentationServiceProvider } from '../../services/documentation.service';
import { ajsFetcherServiceProvider } from '../../services/fetcher.service';
import { ajsCategoryServiceProvider } from '../../services/category.service';
import { ajsApiServiceProvider } from '../../services-ngx/api.service';

@NgModule({
  declarations: [
    DocumentationManagementComponent,
    DocumentationNewPageComponent,
    DocumentationEditPageComponent,
    DocumentationImportPagesComponent,
  ],
  exports: [
    DocumentationManagementComponent,
    DocumentationNewPageComponent,
    DocumentationEditPageComponent,
    DocumentationImportPagesComponent,
  ],
  providers: [ajsDocumentationServiceProvider, ajsFetcherServiceProvider, ajsCategoryServiceProvider, ajsApiServiceProvider],
})
export class DocumentationModule {}
