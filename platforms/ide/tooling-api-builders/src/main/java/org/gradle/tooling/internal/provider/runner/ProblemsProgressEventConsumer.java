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

package org.gradle.tooling.internal.provider.runner;

import org.gradle.api.NonNullApi;
import org.gradle.api.problems.DocLink;
import org.gradle.api.problems.Problem;
import org.gradle.api.problems.ProblemCategory;
import org.gradle.api.problems.ProblemAggregation;
import org.gradle.api.problems.Severity;
import org.gradle.api.problems.internal.DefaultProblemProgressAggregationDetails;
import org.gradle.api.problems.internal.DefaultProblemProgressDetails;
import org.gradle.api.problems.locations.FileLocation;
import org.gradle.api.problems.locations.PluginIdLocation;
import org.gradle.api.problems.locations.ProblemLocation;
import org.gradle.api.problems.locations.TaskPathLocation;
import org.gradle.internal.build.event.types.DefaultAdditionalData;
import org.gradle.internal.build.event.types.DefaultDetails;
import org.gradle.internal.build.event.types.DefaultDocumentationLink;
import org.gradle.internal.build.event.types.DefaultFileLocation;
import org.gradle.internal.build.event.types.DefaultInternalProblemAggregation;
import org.gradle.internal.build.event.types.DefaultLabel;
import org.gradle.internal.build.event.types.DefaultPluginIdLocation;
import org.gradle.internal.build.event.types.DefaultProblemAggregationDetails;
import org.gradle.internal.build.event.types.DefaultProblemCategory;
import org.gradle.internal.build.event.types.DefaultProblemDescriptor;
import org.gradle.internal.build.event.types.DefaultProblemDetails;
import org.gradle.internal.build.event.types.DefaultProblemEvent;
import org.gradle.internal.build.event.types.DefaultSeverity;
import org.gradle.internal.build.event.types.DefaultSolution;
import org.gradle.internal.build.event.types.DefaultTaskPathLocation;
import org.gradle.internal.operations.BuildOperationIdFactory;
import org.gradle.internal.operations.BuildOperationListener;
import org.gradle.internal.operations.OperationIdentifier;
import org.gradle.internal.operations.OperationProgressEvent;
import org.gradle.tooling.internal.protocol.InternalProblemAggregation;
import org.gradle.tooling.internal.protocol.InternalProblemEvent;
import org.gradle.tooling.internal.protocol.problem.InternalAdditionalData;
import org.gradle.tooling.internal.protocol.problem.InternalDetails;
import org.gradle.tooling.internal.protocol.problem.InternalDocumentationLink;
import org.gradle.tooling.internal.protocol.problem.InternalLabel;
import org.gradle.tooling.internal.protocol.problem.InternalLocation;
import org.gradle.tooling.internal.protocol.problem.InternalProblemCategory;
import org.gradle.tooling.internal.protocol.problem.InternalSeverity;
import org.gradle.tooling.internal.protocol.problem.InternalSolution;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toMap;

@NonNullApi
public class ProblemsProgressEventConsumer extends ClientForwardingBuildOperationListener implements BuildOperationListener {

    private static final InternalSeverity ADVICE = new DefaultSeverity(0);
    private static final InternalSeverity WARNING = new DefaultSeverity(1);
    private static final InternalSeverity ERROR = new DefaultSeverity(2);

    private final BuildOperationIdFactory idFactory;

    public ProblemsProgressEventConsumer(ProgressEventConsumer progressEventConsumer, BuildOperationIdFactory idFactory) {
        super(progressEventConsumer);
        this.idFactory = idFactory;
    }

    @Override
    public void progress(OperationIdentifier buildOperationId, OperationProgressEvent progressEvent) {
        Object details = progressEvent.getDetails();
        createProblemEvent(buildOperationId, details)
            .ifPresent(eventConsumer::progress);
    }

    private Optional<InternalProblemEvent> createProblemEvent(OperationIdentifier buildOperationId, @Nullable Object details) {
        if (details instanceof DefaultProblemProgressDetails) {
            Problem problem = ((DefaultProblemProgressDetails) details).getProblem();
            return of(createProblemEvent(buildOperationId, problem));
        } else if (details instanceof DefaultProblemProgressAggregationDetails) {
            return of(createProblemAggregationEvent(buildOperationId, (DefaultProblemProgressAggregationDetails) details));
        }
        return empty();
    }

    private DefaultProblemEvent createProblemAggregationEvent(OperationIdentifier buildOperationId, DefaultProblemProgressAggregationDetails details) {
        //TODO (reinhold) aggregate/composite/collection/summary problems? might be a bit better
        return new DefaultProblemEvent(
            cerateDefaultProblemDescriptor(buildOperationId),
            new DefaultProblemAggregationDetails(createInternalSummaries(details)));
    }

    private static List<InternalProblemAggregation> createInternalSummaries(DefaultProblemProgressAggregationDetails details) {
        return details.getAggregations()
            .stream()
            .map(ProblemsProgressEventConsumer::createInternalSummary)
            .collect(toImmutableList());
    }

    private OperationIdentifier createNewOperationIdentifier() {
        return new OperationIdentifier(idFactory.nextId());
    }

    private static DefaultInternalProblemAggregation createInternalSummary(ProblemAggregation summary) {
        return new DefaultInternalProblemAggregation(
            toInternalCategory(summary.getProblemCategory()),
            toInternalLabel(summary.getLabel()),
            toInternalDetails(summary.getDetails()),
            toInternalSeverity(summary.getSeverity()),
            toInternalLocations(summary.getLocations()),
            toInternalDocumentationLink(summary.getDocumentationLink()),
            toInternalSolutions(summary.getSolutions()),
            toInternalAdditionalData(summary.getAdditionalData()),
            summary.getCount());
    }

    private DefaultProblemEvent createProblemEvent(OperationIdentifier buildOperationId, Problem problem) {
        return new DefaultProblemEvent(
            cerateDefaultProblemDescriptor(buildOperationId),
            new DefaultProblemDetails(
                toInternalCategory(problem.getProblemCategory()),
                toInternalLabel(problem.getLabel()),
                toInternalDetails(problem.getDetails()),
                toInternalSeverity(problem.getSeverity()),
                toInternalLocations(problem.getLocations()),
                toInternalDocumentationLink(problem.getDocumentationLink()),
                toInternalSolutions(problem.getSolutions()),
                toInternalAdditionalData(problem.getAdditionalData()),
                problem.getException()
            )
        );
    }

    private DefaultProblemDescriptor cerateDefaultProblemDescriptor(OperationIdentifier buildOperationId) {
        return new DefaultProblemDescriptor(
            createNewOperationIdentifier(),
            buildOperationId);
    }

    private static InternalProblemCategory toInternalCategory(ProblemCategory category) {
        return new DefaultProblemCategory(category.getNamespace(), category.getCategory(), category.getSubCategories());
    }

    private static InternalLabel toInternalLabel(String label) {
        return new DefaultLabel(label);
    }

    private static @Nullable InternalDetails toInternalDetails(@Nullable String details) {
        return details == null ? null : new DefaultDetails(details);
    }

    private static InternalSeverity toInternalSeverity(Severity severity) {
        switch (severity) {
            case ADVICE:
                return ADVICE;
            case WARNING:
                return WARNING;
            case ERROR:
                return ERROR;
            default:
                throw new RuntimeException("No mapping defined for severity level " + severity);
        }
    }

    private static List<InternalLocation> toInternalLocations(List<ProblemLocation> locations) {
        return locations.stream().map((Function<ProblemLocation, InternalLocation>) location -> {
            if (location instanceof FileLocation) {
                FileLocation fileLocation = (FileLocation) location;
                return new DefaultFileLocation(fileLocation.getPath(), fileLocation.getLine(), fileLocation.getColumn(), fileLocation.getLength());
            } else if (location instanceof PluginIdLocation) {
                PluginIdLocation pluginLocation = (PluginIdLocation) location;
                return new DefaultPluginIdLocation(pluginLocation.getPluginId());
            } else if (location instanceof TaskPathLocation) {
                TaskPathLocation taskLocation = (TaskPathLocation) location;
                return new DefaultTaskPathLocation(taskLocation.getIdentityPath().toString());
            } else {
                throw new RuntimeException("No mapping defined for " + location.getClass().getName());
            }
        }).collect(toImmutableList());
    }

    @Nullable
    private static InternalDocumentationLink toInternalDocumentationLink(@Nullable DocLink link) {
        return (link == null || link.getUrl() == null) ? null : new DefaultDocumentationLink(link.getUrl());
    }

    private static List<InternalSolution> toInternalSolutions(List<String> solutions) {
        return solutions.stream()
            .map(DefaultSolution::new)
            .collect(toImmutableList());
    }

    private static InternalAdditionalData toInternalAdditionalData(Map<String, Object> additionalData) {
        return new DefaultAdditionalData(
            additionalData.entrySet().stream()
                .filter(entry -> isSupportedType(entry.getValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    private static boolean isSupportedType(Object type) {
        return type instanceof String;
    }
}
