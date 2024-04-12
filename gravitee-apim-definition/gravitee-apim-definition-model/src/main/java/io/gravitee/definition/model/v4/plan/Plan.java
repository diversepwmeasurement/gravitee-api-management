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
package io.gravitee.definition.model.v4.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.Plugin;
import io.gravitee.definition.model.v4.flow.Flow;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(name = "PlanV4")
public class Plan implements Serializable {

    @JsonProperty(required = true)
    @NotBlank
    private String id;

    @JsonProperty(required = true)
    @NotBlank
    private String name;

    @JsonProperty(required = true)
    @NotNull
    private PlanSecurity security;

    @JsonProperty(required = true)
    @NotNull
    private PlanMode mode;

    private String selectionRule;

    private List<Flow> flows;

    private Set<String> tags;

    @JsonProperty(required = true)
    @NotNull
    private PlanStatus status;

    public final boolean isSubscribable() {
        return (
            (
                this.getSecurity() != null &&
                this.getSecurity().getType() != null &&
                !"KEY_LESS".equalsIgnoreCase(this.getSecurity().getType())
            ) ||
            usePushMode()
        );
    }

    public final boolean usePushMode() {
        return this.getMode() != null && this.getMode() == PlanMode.PUSH;
    }

    public final boolean useStandardMode() {
        return this.getMode() != null && this.getMode() == PlanMode.STANDARD;
    }

    public final boolean isApiKey() {
        return (
            this.getSecurity() != null &&
            ("API_KEY".equalsIgnoreCase(this.getSecurity().getType()) || "api-key".equalsIgnoreCase(this.getSecurity().getType()))
        );
    }

    @JsonIgnore
    public List<Plugin> getPlugins() {
        return Optional
            .ofNullable(this.flows)
            .map(f -> f.stream().filter(Flow::isEnabled).map(Flow::getPlugins).flatMap(List::stream).collect(Collectors.toList()))
            .orElse(List.of());
    }
}
