/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.feature.extension.apiregions.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.sling.feature.Artifact;
import org.apache.sling.feature.ArtifactId;

/**
 * Describes an api region
 */
public class ApiRegion {

    /** Name of the global region. */
    public static final String GLOBAL = "global";

    private final List<ApiExport> exports = new ArrayList<>();

    private final Map<String, String> properties = new HashMap<>();

    private final String name;

    private volatile ApiRegion parent;

    private volatile ApiRegion child;

    /**
     * Create a new named region
     *
     * @param name The name
     */
    public ApiRegion(final String name) {
        this.name = name;
    }

    /**
     * Get the name of the region
     *
     * @return The region name
     */
    public String getName() {
        return name;
    }

    public ArtifactId[] getFeatureOrigins() {
        String origins = this.getProperties().get(Artifact.KEY_FEATURE_ORIGINS);
        Set<ArtifactId> originFeatures;
        if (origins == null || origins.trim().isEmpty()) {
            originFeatures = Collections.emptySet();
        }
        else {
            originFeatures = new LinkedHashSet<>();
            for (String origin : origins.split(",")) {
                if (!origin.trim().isEmpty()) {
                    originFeatures.add(ArtifactId.parse(origin));
                }
            }
        }
        return originFeatures.toArray(new ArtifactId[0]);
    }

    public void setFeatureOrigins(ArtifactId... featureOrigins) {
        String origins;
        if (featureOrigins != null && featureOrigins.length > 0) {
            origins = Stream.of(featureOrigins).filter(Objects::nonNull).map(ArtifactId::toMvnId).distinct().collect(Collectors.joining(","));
        }
        else {
            origins = "";
        }
        if (!origins.trim().isEmpty()) {
            this.getProperties().put(Artifact.KEY_FEATURE_ORIGINS, origins);
        }
        else {
            this.getProperties().remove(Artifact.KEY_FEATURE_ORIGINS);
        }
    }

    /**
     * Add the export. The export is only added if there isn't already a export with
     * the same name
     *
     * @param export The export to add
     * @return {@code true} if the export could be added, {@code false} otherwise
     */
    public boolean add(final ApiExport export) {
        boolean found = false;
        for (final ApiExport c : this.exports) {
            if (c.getName().equals(export.getName())) {
                found = true;
                break;
            }
        }
        if (!found) {
            this.exports.add(export);
        }
        return !found;
    }

    /**
     * Remove the export
     *
     * @param export export to remove
     * @return {@code true} if the export got removed.
     */
    public boolean remove(final ApiExport export) {
        return this.exports.remove(export);
    }

    /**
     * Check if the region has exports
     *
     * @return {@code true} if it has any export
     */
    public boolean isEmpty() {
        return this.exports.isEmpty();
    }

    /**
     * Unmodifiable collection of exports for this region
     *
     * @return The collection of exports
     */
    public Collection<ApiExport> listExports() {
        return Collections.unmodifiableCollection(this.exports);
    }

    /**
     * Unmodifiable collection of exports for this region and all parents.
     *
     * @return The collection of exports
     */
    public Collection<ApiExport> listAllExports() {
        final List<ApiExport> list = new ArrayList<>();
        if (parent != null) {
            list.addAll(parent.listAllExports());
        }
        list.addAll(this.exports);
        return Collections.unmodifiableCollection(list);
    }

    /**
     * Get an export by name
     *
     * @param name package name
     * @return The export or {@code null}
     */
    public ApiExport getExportByName(final String name) {
        for (final ApiExport e : this.exports) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Get additional properties
     *
     * @return Modifiable map of properties
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Get the parent region
     *
     * @return The parent region or {@code null}
     */
    public ApiRegion getParent() {
        return this.parent;
    }

    /**
     * Get the child region
     *
     * @return The child region or {@code null}
     */
    public ApiRegion getChild() {
        return this.child;
    }

    void setParent(final ApiRegion region) {
        this.parent = region;
    }

    void setChild(final ApiRegion region) {
        this.child = region;
    }

    @Override
    public String toString() {
        return "ApiRegion [exports=" + exports + ", properties=" + properties + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exports == null) ? 0 : exports.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApiRegion other = (ApiRegion) obj;
        if (exports == null) {
            if (other.exports != null)
                return false;
        } else if (!exports.equals(other.exports))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }
}
