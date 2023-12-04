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

import org.gradle.api.problems.Problem;
import org.gradle.api.problems.ProblemAggregation;
import org.gradle.problems.buildtree.ProblemReporter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class DeduplicationProblemEmitter implements ProblemEmitter {
    private ProblemEmitter emitter;
    private final Map<String, List<Problem>> seenProblems = new HashMap<String, List<Problem>>();

    public DeduplicationProblemEmitter(ProblemEmitter emitter) {
       this.setEmitter(emitter);
    }

    public void report(File reportDir /*unused*/, ProblemReporter.ProblemConsumer validationFailures /*unused*/) {
        Set<Map.Entry<String, List<Problem>>> entries = seenProblems.entrySet();
        List<ProblemAggregation> problemSummaries = newArrayList();
        for (Map.Entry<String, List<Problem>> entry : entries) {
            List<Problem> value = entry.getValue();
            if(value.size() == 1) {
                continue;
            }
            Problem firstProblem = value.get(0);
            problemSummaries.add(new DefaultProblemAggregation(firstProblem.getProblemCategory(), firstProblem.getLabel(), value));
        }
        if(problemSummaries.isEmpty()) {
            return;
        }
        emitter.emit(problemSummaries);
    }

    public void emit(Problem problem) {
        String deduplicationKey = problem.getProblemCategory().getCategory() + ";" + problem.getLabel();
        List<Problem> seenProblem = seenProblems.get(deduplicationKey);
        if(seenProblem != null) {
            seenProblem.add(problem);
            return;
        }

        seenProblems.put(deduplicationKey, newArrayList(problem));

        emitter.emit(problem);
    }

    @Override
    public void emit(List<ProblemAggregation> summaries) {
        emitter.emit(summaries);
    }

    public void setEmitter(ProblemEmitter emitter) {
        this.emitter = emitter;
    }
}
