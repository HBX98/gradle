/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.build.event.types;

import org.gradle.api.NonNullApi;
import org.gradle.tooling.internal.protocol.InternalProblemAggregation;
import org.gradle.tooling.internal.protocol.problem.InternalAdditionalData;
import org.gradle.tooling.internal.protocol.problem.InternalDetails;
import org.gradle.tooling.internal.protocol.problem.InternalDocumentationLink;
import org.gradle.tooling.internal.protocol.problem.InternalLabel;
import org.gradle.tooling.internal.protocol.problem.InternalLocation;
import org.gradle.tooling.internal.protocol.problem.InternalProblemCategory;
import org.gradle.tooling.internal.protocol.problem.InternalSeverity;
import org.gradle.tooling.internal.protocol.problem.InternalSolution;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

@NonNullApi
public class DefaultInternalProblemAggregation implements InternalProblemAggregation, Serializable {

    private final InternalProblemCategory category;
    private final InternalLabel internalLabel;
    @Nullable
    private final InternalDetails internalDetails;
    private final InternalSeverity internalSeverity;
    private final List<InternalLocation> internalLocations;
    @Nullable
    private final InternalDocumentationLink internalDocumentationLink;
    private final List<InternalSolution> internalSolutions;
    private final InternalAdditionalData internalAdditionalData;
    private final int count;

    public DefaultInternalProblemAggregation(
        InternalProblemCategory category,
        InternalLabel internalLabel,
        @Nullable InternalDetails internalDetails,
        InternalSeverity internalSeverity,
        List<InternalLocation> internalLocations,
        @Nullable InternalDocumentationLink internalDocumentationLink,
        List<InternalSolution> internalSolutions,
        InternalAdditionalData internalAdditionalData,
        int count
    ) {

        this.category = category;
        this.internalLabel = internalLabel;
        this.internalDetails = internalDetails;
        this.internalSeverity = internalSeverity;
        this.internalLocations = internalLocations;
        this.internalDocumentationLink = internalDocumentationLink;
        this.internalSolutions = internalSolutions;
        this.internalAdditionalData = internalAdditionalData;
        this.count = count;
    }

    @Override
    public InternalProblemCategory getCategory() {
        return category;
    }

    @Override
    public InternalLabel getLabel() {
        return internalLabel;
    }

    @Nullable
    @Override
    public InternalDetails getDetails() {
        return internalDetails;
    }

    @Override
    public InternalSeverity getSeverity() {
        return internalSeverity;
    }

    @Override
    public List<InternalLocation> getLocations() {
        return internalLocations;
    }

    @Nullable
    @Override
    public InternalDocumentationLink getDocumentationLink() {
        return internalDocumentationLink;
    }

    @Override
    public List<InternalSolution> getSolutions() {
        return internalSolutions;
    }

    @Override
    public InternalAdditionalData getAdditionalData() {
        return internalAdditionalData;
    }

    @Override
    public int getCount() {
        return count;
    }
}
