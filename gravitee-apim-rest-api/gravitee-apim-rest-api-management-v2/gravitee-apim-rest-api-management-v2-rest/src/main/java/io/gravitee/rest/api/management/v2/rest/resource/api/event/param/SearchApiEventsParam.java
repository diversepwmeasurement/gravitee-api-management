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
package io.gravitee.rest.api.management.v2.rest.resource.api.event.param;

import io.gravitee.rest.api.management.v2.rest.validation.IntervalParamConstraint;
import io.gravitee.rest.api.management.v2.rest.validation.TimeInterval;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.QueryParam;
import java.util.Set;
import lombok.Data;

@Data
@IntervalParamConstraint
public class SearchApiEventsParam implements TimeInterval {

    public static final String FROM_QUERY_PARAM_NAME = "from";
    public static final String TO_QUERY_PARAM_NAME = "to";
    public static final String EVENT_TYPES_QUERY_PARAM_NAME = "types";

    @QueryParam(FROM_QUERY_PARAM_NAME)
    @Min(0)
    Long from;

    @QueryParam(TO_QUERY_PARAM_NAME)
    @Min(0)
    Long to;

    @QueryParam(EVENT_TYPES_QUERY_PARAM_NAME)
    Set<String> types;
}
