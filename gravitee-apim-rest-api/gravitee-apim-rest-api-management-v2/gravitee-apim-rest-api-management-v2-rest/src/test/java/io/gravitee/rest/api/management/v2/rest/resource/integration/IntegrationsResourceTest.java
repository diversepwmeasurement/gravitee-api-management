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
package io.gravitee.rest.api.management.v2.rest.resource.integration;

import static assertions.MAPIAssertions.assertThat;
import static io.gravitee.common.http.HttpStatusCode.OK_200;
import static org.mockito.Mockito.doReturn;

import fixtures.core.model.IntegrationFixture;
import inmemory.IntegrationCrudServiceInMemory;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.rest.api.management.v2.rest.model.*;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResourceTest;
import io.gravitee.rest.api.model.EnvironmentEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.common.UuidString;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IntegrationsResourceTest extends AbstractResourceTest {

    @Autowired
    IntegrationCrudServiceInMemory integrationCrudServiceInMemory;

    private static final String ENVIRONMENT = "my-env";
    private static final String INTEGRATION_NAME = "test-name";
    private static final String INTEGRATION_DESCRIPTION = "integration-description";
    private static final String INTEGRATION_PROVIDER = "test-provider";
    private static final String INTEGRATION_ID = "generated-id";

    WebTarget target;

    @Override
    protected String contextPath() {
        return "/environments/" + ENVIRONMENT + "/integrations";
    }

    @BeforeEach
    public void init() throws TechnicalException {
        target = rootTarget();

        EnvironmentEntity environmentEntity = EnvironmentEntity.builder().id(ENVIRONMENT).organizationId(ORGANIZATION).build();
        doReturn(environmentEntity).when(environmentService).findById(ENVIRONMENT);
        doReturn(environmentEntity).when(environmentService).findByOrgAndIdOrHrid(ORGANIZATION, ENVIRONMENT);

        GraviteeContext.setCurrentEnvironment(ENVIRONMENT);
        GraviteeContext.setCurrentOrganization(ORGANIZATION);

        UuidString.overrideGenerator(() -> INTEGRATION_ID);
    }

    @AfterEach
    public void tearDown() {
        integrationCrudServiceInMemory.reset();
    }

    @Nested
    class CreateIntegrations {

        @Test
        public void should_create_integration() {
            //Given
            var createIntegration = CreateIntegration
                .builder()
                .name(INTEGRATION_NAME)
                .description(INTEGRATION_DESCRIPTION)
                .provider(INTEGRATION_PROVIDER)
                .build();

            //When
            Response response = target.request().post(Entity.json(createIntegration));

            //Then

            assertThat(response)
                .hasStatus(HttpStatusCode.CREATED_201)
                .asEntity(Integration.class)
                .isEqualTo(
                    Integration
                        .builder()
                        .id(INTEGRATION_ID)
                        .name(INTEGRATION_NAME)
                        .description(INTEGRATION_DESCRIPTION)
                        .provider(INTEGRATION_PROVIDER)
                        .build()
                );
        }

        @Test
        public void should_throw_bad_request_when_name_is_missing() {
            //Given
            CreateIntegration createIntegration = CreateIntegration
                .builder()
                .description(INTEGRATION_DESCRIPTION)
                .provider(INTEGRATION_PROVIDER)
                .build();

            //When
            Response response = target.request().post(Entity.json(createIntegration));

            //Then
            assertThat(response).hasStatus(HttpStatusCode.BAD_REQUEST_400);
        }

        @Test
        public void should_throw_bad_request_when_payload_is_empty() {
            //When
            Response response = target.request().post(Entity.json(null));

            //Then
            assertThat(response).hasStatus(HttpStatusCode.BAD_REQUEST_400);
        }
    }

    @Nested
    class ListIntegrations {

        @BeforeEach
        void setup() {
            var integration = IntStream.range(0, 15).mapToObj(i -> IntegrationFixture.anIntegration()).toList();
            integrationCrudServiceInMemory.initWith(integration);
        }

        @Test
        public void get_first_page_of_integrations_for_specific_env_id() {
            var integrationOtherEnv = IntegrationFixture.anIntegration("other-env");
            integrationCrudServiceInMemory.create(integrationOtherEnv);

            //When
            Response response = target.queryParam("page", 1).queryParam("perPage", 5).request().get();

            //Then
            assertThat(response)
                .hasStatus(HttpStatusCode.OK_200)
                .asEntity(IntegrationsResponse.class)
                .extracting(IntegrationsResponse::getPagination)
                .isEqualTo(Pagination.builder().page(1).perPage(5).pageItemsCount(5).pageCount(3).totalCount(15L).build());
        }

        @Test
        public void get_second_page_of_integrations() {
            //When
            Response response = target.queryParam("page", 2).queryParam("perPage", 2).request().get();

            //Then
            assertThat(response)
                .hasStatus(HttpStatusCode.OK_200)
                .asEntity(IntegrationsResponse.class)
                .extracting(IntegrationsResponse::getPagination)
                .isEqualTo(Pagination.builder().page(2).perPage(2).pageItemsCount(2).pageCount(8).totalCount(15L).build());
        }

        @Test
        public void get_first_page_with_page_size_10_when_pagination_param_missing() {
            //When
            Response response = target.request().get();

            //Then
            assertThat(response)
                .hasStatus(HttpStatusCode.OK_200)
                .asEntity(IntegrationsResponse.class)
                .extracting(IntegrationsResponse::getPagination)
                .isEqualTo(Pagination.builder().page(1).perPage(10).pageItemsCount(10).pageCount(2).totalCount(15L).build());
        }

        @Test
        public void get_sorted_list_of_integrations() {
            //Given
            var name = "most-recent-integration";
            var description = "should-be-returned-first";
            var provider = "test-provider";
            var recentDate = ZonedDateTime.parse("2024-02-03T20:22:02.00Z");
            var integration = IntegrationFixture.BASE
                .get()
                .name(name)
                .description(description)
                .provider(provider)
                .createdAt(recentDate)
                .updatedAt(recentDate)
                .build();
            integrationCrudServiceInMemory.create(integration);

            //When
            Response response = target.request().get();

            //Then
            assertThat(response)
                .hasStatus(HttpStatusCode.OK_200)
                .asEntity(IntegrationsResponse.class)
                .extracting(IntegrationsResponse::getData)
                .extracting(integrations -> integrations.get(0))
                .isEqualTo(Integration.builder().id(INTEGRATION_ID).name(name).description(description).provider(provider).build());
        }

        @Test
        void should_compute_links() {
            Response response = target.queryParam("page", 2).queryParam("perPage", 5).request().get();

            assertThat(response)
                .hasStatus(OK_200)
                .asEntity(ApiLogsResponse.class)
                .extracting(ApiLogsResponse::getLinks)
                .isEqualTo(
                    Links
                        .builder()
                        .self(target.queryParam("page", 2).queryParam("perPage", 5).getUri().toString())
                        .first(target.queryParam("page", 1).queryParam("perPage", 5).getUri().toString())
                        .last(target.queryParam("page", 3).queryParam("perPage", 5).getUri().toString())
                        .previous(target.queryParam("page", 1).queryParam("perPage", 5).getUri().toString())
                        .next(target.queryParam("page", 3).queryParam("perPage", 5).getUri().toString())
                        .build()
                );
        }
    }
}
