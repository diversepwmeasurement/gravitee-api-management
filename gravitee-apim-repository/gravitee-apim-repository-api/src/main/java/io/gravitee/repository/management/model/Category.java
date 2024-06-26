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
package io.gravitee.repository.management.model;

import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    public enum AuditEvent implements Audit.AuditEvent {
        CATEGORY_CREATED,
        CATEGORY_UPDATED,
        CATEGORY_DELETED,
    }

    private String id;
    private String environmentId;
    private String key;
    private String name;
    private String description;
    private boolean hidden;
    private int order;
    private String highlightApi;
    private String picture;
    private String background;
    private String page;
    private Date createdAt;
    private Date updatedAt;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getHighlightApi() {
        return highlightApi;
    }

    public void setHighlightApi(String highlightApi) {
        this.highlightApi = highlightApi;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return (
            Objects.equals(id, category.id) &&
            Objects.equals(key, category.key) &&
            Objects.equals(name, category.name) &&
            Objects.equals(description, category.description)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, name, description);
    }

    @Override
    public String toString() {
        return (
            "Category{" +
            "id='" +
            id +
            '\'' +
            ", environmentId='" +
            environmentId +
            '\'' +
            ", key='" +
            key +
            '\'' +
            ", name='" +
            name +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", hidden='" +
            hidden +
            '\'' +
            ", order='" +
            order +
            '\'' +
            ", highlightApi='" +
            highlightApi +
            '\'' +
            ", page='" +
            page +
            '\'' +
            ", updatedAt='" +
            updatedAt +
            '\'' +
            ", createdAt='" +
            createdAt +
            '\'' +
            '}'
        );
    }
}
