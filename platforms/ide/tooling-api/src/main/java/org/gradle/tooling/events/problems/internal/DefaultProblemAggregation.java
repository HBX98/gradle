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

package org.gradle.tooling.events.problems.internal;

import org.gradle.tooling.events.problems.AdditionalData;
import org.gradle.tooling.events.problems.Details;
import org.gradle.tooling.events.problems.DocumentationLink;
import org.gradle.tooling.events.problems.Label;
import org.gradle.tooling.events.problems.Location;
import org.gradle.tooling.events.problems.ProblemCategory;
import org.gradle.tooling.events.problems.ProblemAggregation;
import org.gradle.tooling.events.problems.Severity;
import org.gradle.tooling.events.problems.Solution;

import java.util.List;

public class DefaultProblemAggregation implements ProblemAggregation {

    private final ProblemCategory problemCategory;
    private final Label problemLabel;
    private final Details problemDetails;
    private final Severity problemSeverity;
    private final List<Location> locations;
    private final DocumentationLink documentationLink;
    private final List<Solution> solutions;
    private final AdditionalData additionalData;
    private final int count;

    public DefaultProblemAggregation(ProblemCategory problemCategory,
                                     Label problemLabel,
                                     Details problemDetails,
                                     Severity problemSeverity,
                                     List<Location> locations,
                                     DocumentationLink documentationLink,
                                     List<Solution> solutions,
                                     AdditionalData additionalData,
                                     int count
    ) {
        this.problemCategory = problemCategory;
        this.problemLabel = problemLabel;
        this.problemDetails = problemDetails;
        this.problemSeverity = problemSeverity;
        this.locations = locations;
        this.documentationLink = documentationLink;
        this.solutions = solutions;
        this.additionalData = additionalData;
        this.count = count;
    }

    @Override
    public ProblemCategory getCategory() {
        return problemCategory;
    }

    @Override
    public Label getLabel() {
        return problemLabel;
    }

    @Override
    public Details getDetails() {
        return problemDetails;
    }

    @Override
    public Severity getSeverity() {
        return problemSeverity;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public DocumentationLink getDocumentationLink() {
        return documentationLink;
    }

    @Override
    public List<Solution> getSolutions() {
        return solutions;
    }

    @Override
    public AdditionalData getAdditionalData() {
        return additionalData;
    }

    @Override
    public int getCount() {
        return count;
    }
}
