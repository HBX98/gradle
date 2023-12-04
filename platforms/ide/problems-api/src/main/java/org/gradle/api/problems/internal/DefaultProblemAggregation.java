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

package org.gradle.api.problems.internal;

import org.gradle.api.problems.DocLink;
import org.gradle.api.problems.Problem;
import org.gradle.api.problems.ProblemAggregation;
import org.gradle.api.problems.ProblemCategory;
import org.gradle.api.problems.Severity;
import org.gradle.api.problems.UnboundBasicProblemBuilder;
import org.gradle.api.problems.locations.ProblemLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class DefaultProblemAggregation implements ProblemAggregation {
    private final ProblemCategory problemCategory;
    private final String label;
    private final List<Problem> value;

    public DefaultProblemAggregation(ProblemCategory problemCategory, String label, List<Problem> value) {
        this.problemCategory = problemCategory;
        this.label = label;
        this.value = value;
    }

    @Override
    public ProblemCategory getProblemCategory() {
        return problemCategory;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Nullable
    @Override
    public String getDetails() {
        StringBuilder accumulatedDetails = new StringBuilder();
        for (Problem problem : value) {
            accumulatedDetails.append(problem.getDetails()).append("\n");
        }
        return accumulatedDetails.toString();
    }

    @Override
    public Severity getSeverity() {
        return value.get(0).getSeverity();
    }

    @Override
    public List<ProblemLocation> getLocations() {
//        for (Problem problem : value) {
//            accumulatedDetails.append(problem.getDetails()).append("\n");
//        }
        return value.get(0).getLocations();
    }

    @Nullable
    @Override
    public DocLink getDocumentationLink() {
        return value.get(0).getDocumentationLink();
    }

    @Override
    public List<String> getSolutions() {
        return value.get(0).getSolutions();
    }

    @Nullable
    @Override
    public RuntimeException getException() {
        return value.get(0).getException();
    }

    @Override
    public Map<String, Object> getAdditionalData() {
        return value.get(0).getAdditionalData();
    }

    @Override
    public UnboundBasicProblemBuilder toBuilder() {
        return null;
    }

    @Override
    public int getCount() {
        return value.size();
    }
}
