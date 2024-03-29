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

import io.gravitee.apim.core.audit.model.AuditActor;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.integration.use_case.GetIntegrationUseCase;
import io.gravitee.apim.core.integration.use_case.IngestIntegrationApisUseCase;
import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.v2.rest.mapper.IntegrationMapper;
import io.gravitee.rest.api.management.v2.rest.model.IngestionStatus;
import io.gravitee.rest.api.management.v2.rest.model.IntegrationIngestionResponse;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.rest.annotation.Permission;
import io.gravitee.rest.api.rest.annotation.Permissions;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.exceptions.ForbiddenAccessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Remi Baptiste (remi.baptiste at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class IntegrationResource extends AbstractResource {

    @Inject
    private GetIntegrationUseCase getIntegrationUsecase;

    @Inject
    private IngestIntegrationApisUseCase ingestIntegrationApisUseCase;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.ENVIRONMENT_INTEGRATION, acls = { RolePermissionAction.READ }) })
    public Response getIntegrationById(@PathParam("integrationId") String integrationId) {
        var integration = getIntegrationUsecase
            .execute(GetIntegrationUseCase.Input.builder().integrationId(integrationId).build())
            .integration();

        return Response.ok(IntegrationMapper.INSTANCE.map(integration)).build();
    }

    @POST
    @Path("/_ingest")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions(
        {
            @Permission(value = RolePermission.ENVIRONMENT_INTEGRATION, acls = { RolePermissionAction.READ }),
            @Permission(value = RolePermission.ENVIRONMENT_API, acls = { RolePermissionAction.CREATE }),
        }
    )
    public void ingestApis(@PathParam("integrationId") String integrationId, @Suspended final AsyncResponse response) {
        var executionContext = GraviteeContext.getExecutionContext();
        if (
            !hasPermission(
                executionContext,
                RolePermission.ENVIRONMENT_INTEGRATION,
                GraviteeContext.getCurrentEnvironment(),
                RolePermissionAction.READ
            ) ||
            !hasPermission(
                executionContext,
                RolePermission.ENVIRONMENT_API,
                GraviteeContext.getCurrentEnvironment(),
                RolePermissionAction.CREATE
            )
        ) {
            throw new ForbiddenAccessException();
        }

        var userDetails = getAuthenticatedUserDetails();

        AuditInfo audit = AuditInfo
            .builder()
            .organizationId(executionContext.getOrganizationId())
            .environmentId(executionContext.getEnvironmentId())
            .actor(
                AuditActor
                    .builder()
                    .userId(userDetails.getUsername())
                    .userSource(userDetails.getSource())
                    .userSourceId(userDetails.getSourceId())
                    .build()
            )
            .build();

        ingestIntegrationApisUseCase
            .execute(new IngestIntegrationApisUseCase.Input(integrationId, audit))
            .subscribe(
                () -> response.resume(IntegrationIngestionResponse.builder().status(IngestionStatus.SUCCESS).build()),
                response::resume
            );
    }
}
