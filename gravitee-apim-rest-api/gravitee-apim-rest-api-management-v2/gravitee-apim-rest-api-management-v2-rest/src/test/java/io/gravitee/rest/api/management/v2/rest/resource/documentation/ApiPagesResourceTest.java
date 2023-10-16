/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.v2.rest.resource.documentation;

import static io.gravitee.common.http.HttpStatusCode.FORBIDDEN_403;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import assertions.MAPIAssertions;
import inmemory.*;
import io.gravitee.apim.core.documentation.model.Page;
import io.gravitee.rest.api.management.v2.rest.model.*;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResourceTest;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.common.UuidString;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

class ApiPagesResourceTest extends AbstractResourceTest {

    @Autowired
    private PageQueryServiceInMemory pageQueryServiceInMemory;

    @Autowired
    private PageCrudServiceInMemory pageCrudServiceInMemory;

    @Autowired
    private PageRevisionCrudServiceInMemory pageRevisionCrudServiceInMemory;

    @Autowired
    private AuditCrudServiceInMemory auditCrudService;

    @Autowired
    private UserCrudServiceInMemory userCrudService;

    protected static final String ENVIRONMENT = "my-env";
    protected static final String PAGE_ID = "page-id";

    @BeforeEach
    void init() {
        GraviteeContext.cleanContext();

        EnvironmentEntity environmentEntity = new EnvironmentEntity();
        environmentEntity.setId(ENVIRONMENT);
        environmentEntity.setOrganizationId(ORGANIZATION);
        doReturn(environmentEntity).when(environmentService).findById(ENVIRONMENT);
        doReturn(environmentEntity).when(environmentService).findByOrgAndIdOrHrid(ORGANIZATION, ENVIRONMENT);

        GraviteeContext.setCurrentEnvironment(ENVIRONMENT);
        GraviteeContext.setCurrentOrganization(ORGANIZATION);
    }

    @AfterEach
    void tearDown() {
        GraviteeContext.cleanContext();
        Stream
            .of(pageQueryServiceInMemory, pageCrudServiceInMemory, pageRevisionCrudServiceInMemory, auditCrudService, userCrudService)
            .forEach(InMemoryAlternative::reset);
    }

    @Override
    protected String contextPath() {
        return "/environments/" + ENVIRONMENT + "/apis/api-id/pages";
    }

    @Nested
    class GetApiPagesTest {

        @Test
        public void should_return_403_if_incorrect_permissions() {
            when(
                permissionService.hasPermission(
                    eq(GraviteeContext.getExecutionContext()),
                    eq(RolePermission.API_DOCUMENTATION),
                    eq("api-id"),
                    eq(RolePermissionAction.READ)
                )
            )
                .thenReturn(false);

            final Response response = rootTarget().request().get();

            MAPIAssertions
                .assertThat(response)
                .hasStatus(FORBIDDEN_403)
                .asError()
                .hasHttpStatus(FORBIDDEN_403)
                .hasMessage("You do not have sufficient rights to access this resource");
        }

        @Test
        void should_get_pages_empty_list() {
            final Response response = rootTarget().request().get();
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.readEntity(List.class)).isEqualTo(List.of());
        }

        @Test
        void should_get_pages() {
            Page page1 = Page
                .builder()
                .referenceType(Page.ReferenceType.API)
                .referenceId("api-id")
                .type(Page.Type.MARKDOWN)
                .id("page-1")
                .name("page-1")
                .build();
            Page page2 = Page
                .builder()
                .referenceType(Page.ReferenceType.API)
                .referenceId("api-id")
                .type(Page.Type.FOLDER)
                .id("folder")
                .name("folder")
                .build();
            givenApiPagesQuery(List.of(page1, page2));
            final Response response = rootTarget().request().get();
            assertThat(response.getStatus()).isEqualTo(200);
            List<io.gravitee.rest.api.management.v2.rest.model.Page> pages = response.readEntity(new GenericType<>() {});
            assertThat(pages)
                .isEqualTo(
                    List.of(
                        io.gravitee.rest.api.management.v2.rest.model.Page
                            .builder()
                            .id("page-1")
                            .type(PageType.MARKDOWN)
                            .name("page-1")
                            .order(0)
                            .published(false)
                            .homepage(false)
                            .configuration(Map.of())
                            .metadata(Map.of())
                            .excludedAccessControls(false)
                            .build(),
                        io.gravitee.rest.api.management.v2.rest.model.Page
                            .builder()
                            .id("folder")
                            .type(PageType.FOLDER)
                            .name("folder")
                            .order(0)
                            .published(false)
                            .homepage(false)
                            .configuration(Map.of())
                            .metadata(Map.of())
                            .excludedAccessControls(false)
                            .build()
                    )
                );
        }
    }

    @Nested
    class CreateDocumentationTest {

        @Test
        public void should_return_403_if_incorrect_permissions() {
            when(
                permissionService.hasPermission(
                    eq(GraviteeContext.getExecutionContext()),
                    eq(RolePermission.API_DOCUMENTATION),
                    eq("api-id"),
                    eq(RolePermissionAction.CREATE)
                )
            )
                .thenReturn(false);

            final Response response = rootTarget().request().post(Entity.json(CreateDocumentation.builder().build()));

            MAPIAssertions
                .assertThat(response)
                .hasStatus(FORBIDDEN_403)
                .asError()
                .hasHttpStatus(FORBIDDEN_403)
                .hasMessage("You do not have sufficient rights to access this resource");
        }

        @Test
        public void should_create_markdown_page() {
            var pageToCreate = CreateDocumentationMarkdown
                .builder()
                .name("created page")
                .homepage(true)
                .content("nice content")
                .type(CreateDocumentationType.MARKDOWN)
                .order(1)
                .parentId(null)
                .visibility(Visibility.PUBLIC)
                .build();

            final Response response = rootTarget().request().post(Entity.json(pageToCreate));
            var createdPage = response.readEntity(io.gravitee.rest.api.management.v2.rest.model.Page.class);

            assertThat(createdPage)
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", PageType.MARKDOWN)
                .hasFieldOrPropertyWithValue("name", pageToCreate.getName())
                .hasFieldOrPropertyWithValue("homepage", pageToCreate.getHomepage())
                .hasFieldOrPropertyWithValue("content", pageToCreate.getContent())
                .hasFieldOrPropertyWithValue("order", pageToCreate.getOrder())
                .hasFieldOrPropertyWithValue("parentId", pageToCreate.getParentId())
                .hasFieldOrPropertyWithValue("visibility", pageToCreate.getVisibility());

            assertThat(createdPage.getId()).isNotNull();
            assertThat(createdPage.getUpdatedAt()).isNotNull();
        }

        @Test
        public void should_create_folder() {
            var folderToCreate = CreateDocumentationFolder
                .builder()
                .name("created page")
                .type(CreateDocumentationType.FOLDER)
                .order(1)
                .parentId(null)
                .visibility(Visibility.PUBLIC)
                .build();

            final Response response = rootTarget().request().post(Entity.json(folderToCreate));
            var createdPage = response.readEntity(io.gravitee.rest.api.management.v2.rest.model.Page.class);

            assertThat(createdPage)
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", PageType.FOLDER)
                .hasFieldOrPropertyWithValue("name", folderToCreate.getName())
                .hasFieldOrPropertyWithValue("order", folderToCreate.getOrder())
                .hasFieldOrPropertyWithValue("parentId", folderToCreate.getParentId())
                .hasFieldOrPropertyWithValue("visibility", folderToCreate.getVisibility());

            assertThat(createdPage.getId()).isNotNull();
            assertThat(createdPage.getUpdatedAt()).isNotNull();
        }
    }

    private void givenApiPagesQuery(List<Page> pages) {
        pageQueryServiceInMemory.initWith(pages);
    }
}
