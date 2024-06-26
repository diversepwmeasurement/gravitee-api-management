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
package io.gravitee.apim.core.api_key.model;

import io.gravitee.apim.core.subscription.model.SubscriptionEntity;
import io.gravitee.common.utils.TimeProvider;
import io.gravitee.rest.api.service.common.UuidString;
import io.gravitee.rest.api.service.exceptions.ApiKeyAlreadyExistingException;
import io.gravitee.rest.api.service.exceptions.SubscriptionClosedException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ApiKeyEntity {

    private String id;

    private String key;

    private String applicationId;

    private List<String> subscriptions = new ArrayList<>();

    private ZonedDateTime expireAt;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private boolean revoked;

    private ZonedDateTime revokedAt;

    private boolean paused;

    /**
     * Number of days before the expiration of this API Key when the last pre-expiration notification was sent
     */
    private Integer daysToExpirationOnLastNotification;

    public static ApiKeyEntity generateForSubscription(SubscriptionEntity subscription, String customApiKey) {
        if (customApiKey == null) {
            throw new IllegalArgumentException("Custom api key cannot be null");
        }

        var now = TimeProvider.now();
        if (subscription.getEndingAt() != null && subscription.getEndingAt().isBefore(now)) {
            throw new SubscriptionClosedException(subscription.getId());
        }

        return ApiKeyEntity
            .builder()
            .id(UuidString.generateRandom())
            .applicationId(subscription.getApplicationId())
            .createdAt(now)
            .updatedAt(now)
            .key(customApiKey)
            .subscriptions(List.of(subscription.getId()))
            // By default, the API Key will expire when subscription is closed
            .expireAt(subscription.getEndingAt())
            .build();
    }

    public static ApiKeyEntity generateForSubscription(SubscriptionEntity subscription) {
        return generateForSubscription(subscription, UuidString.generateRandom());
    }

    public ApiKeyEntity addSubscription(String subscriptionId) {
        var list = new ArrayList<>(subscriptions);
        list.add(subscriptionId);
        return this.toBuilder().subscriptions(list).updatedAt(TimeProvider.now()).build();
    }

    public ApiKeyEntity revoke() {
        var now = ZonedDateTime.now();
        return this.toBuilder().revoked(true).updatedAt(now).revokedAt(now).build();
    }

    public boolean isExpired() {
        return this.expireAt != null && ZonedDateTime.now().isAfter(this.getExpireAt());
    }

    public boolean canBeRevoked() {
        return !this.isRevoked() && !this.isExpired();
    }

    public boolean hasSubscription(String subscriptionId) {
        return subscriptions.contains(subscriptionId);
    }
}
