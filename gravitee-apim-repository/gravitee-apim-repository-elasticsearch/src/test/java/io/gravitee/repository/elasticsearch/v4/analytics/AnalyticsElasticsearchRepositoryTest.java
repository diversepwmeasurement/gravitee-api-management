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
package io.gravitee.repository.elasticsearch.v4.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.repository.elasticsearch.AbstractElasticsearchRepositoryTest;
import io.gravitee.repository.elasticsearch.v4.log.LogElasticsearchRepository;
import io.gravitee.repository.log.v4.model.analytics.RequestsCountQuery;
import io.gravitee.repository.log.v4.model.connection.ConnectionLogQuery;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource(properties = "reporters.elasticsearch.template_mapping.path=src/test/resources/freemarker-v4-analytics")
class AnalyticsElasticsearchRepositoryTest extends AbstractElasticsearchRepositoryTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    @Autowired
    private AnalyticsElasticsearchRepository cut;

    @Test
    void should_return_all_the_requests_count_by_entrypoint_for_a_given_api() {
        var result = cut.searchRequestsCount(RequestsCountQuery.builder().apiId("f1608475-dd77-4603-a084-75dd775603e9").build());

        assertThat(result)
            .hasValueSatisfying(countAggregate -> {
                assertThat(countAggregate.getTotal()).isEqualTo(7);
                assertThat(countAggregate.getCountBy())
                    .containsAllEntriesOf(Map.of("http-post", 3L, "http-get", 1L, "websocket", 2L, "sse", 1L));
            });
    }
}
