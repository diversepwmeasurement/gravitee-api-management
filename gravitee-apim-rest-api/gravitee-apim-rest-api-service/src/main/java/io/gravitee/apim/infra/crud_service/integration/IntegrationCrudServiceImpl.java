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
package io.gravitee.apim.infra.crud_service.integration;

import io.gravitee.apim.core.integration.crud_service.IntegrationCrudService;
import io.gravitee.apim.core.integration.model.Integration;
import io.gravitee.apim.infra.adapter.IntegrationAdapter;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.IntegrationRepository;
import io.gravitee.rest.api.service.exceptions.TechnicalManagementException;
import io.gravitee.rest.api.service.impl.AbstractService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author Remi Baptiste (remi.baptiste at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
@Service
public class IntegrationCrudServiceImpl extends AbstractService implements IntegrationCrudService {

    private final IntegrationRepository integrationRepository;

    public IntegrationCrudServiceImpl(@Lazy IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    @Override
    public Integration create(Integration integration) {
        try {
            var createdIntegration = integrationRepository.create(IntegrationAdapter.INSTANCE.toRepository(integration));
            return IntegrationAdapter.INSTANCE.toEntity(createdIntegration);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("Error when creating Integration: " + integration.getName(), e);
        }
    }

    @Override
    public Optional<Integration> findById(String id) {
        try {
            return integrationRepository.findById(id).map(IntegrationAdapter.INSTANCE::toEntity);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurs while trying to find the integration: " + id, e);
        }
    }

    @Override
    public Integration update(Integration integration) {
        try {
            var updatedIntegration = integrationRepository.update(IntegrationAdapter.INSTANCE.toRepository(integration));
            return IntegrationAdapter.INSTANCE.toEntity(updatedIntegration);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("An error occurred when updating integration: " + integration.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            integrationRepository.delete(id);
        } catch (TechnicalException e) {
            throw new TechnicalManagementException("Error when deleting Integration: " + id, e);
        }
    }
}
