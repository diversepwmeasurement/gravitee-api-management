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
package io.gravitee.rest.api.service;

import io.gravitee.repository.management.model.Token;
import io.gravitee.rest.api.model.NewTokenEntity;
import io.gravitee.rest.api.model.TokenEntity;
import io.gravitee.rest.api.service.common.ExecutionContext;
import java.util.List;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface TokenService {
    List<TokenEntity> findByUser(String userId);
    Token findByToken(String token);
    boolean tokenExistsForUser(String tokenId, String userId);
    TokenEntity create(ExecutionContext executionContext, NewTokenEntity token, String userId);
    void revokeByUser(ExecutionContext executionContext, String userId);
    void revoke(ExecutionContext executionContext, String tokenId);
}
