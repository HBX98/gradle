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

package org.gradle.internal.code

import org.gradle.api.problems.Problem
import org.gradle.api.problems.ProblemAggregation
import org.gradle.api.problems.internal.DeduplicationProblemEmitter
import org.gradle.api.problems.internal.DefaultProblemCategory
import org.gradle.api.problems.internal.ProblemEmitter
import spock.lang.Specification

class DeduplicationProblemEmitterTest extends Specification {
    def "pass through non duplicates" (){
        def problemEmitter = Mock(ProblemEmitter)
        given:
        def emitter = new DeduplicationProblemEmitter(problemEmitter)


        when:
        for (int i = 0; i < 3; i++) {
            emitter.emit(createMockProblem("foo$i"))
        }

        then:
        3 * problemEmitter.emit(_)
    }

    def "emit summary if there are deduplicated events" (){
        given:
        def problemEmitter = Mock(ProblemEmitter)
        def emitter = new DeduplicationProblemEmitter(problemEmitter)

        when:
        for (int i = 0; i < 15; i++) {
            emitter.emit(createMockProblem("foo"))
        }

        emitter.report(null, null)

        then:
        1 * problemEmitter.emit(_ as List<ProblemAggregation>)
        1 * problemEmitter.emit(_ as Problem)
    }

    def "don't emit summary for single events" (){
        given:
        def problemEmitter = Mock(ProblemEmitter)
        def emitter = new DeduplicationProblemEmitter(problemEmitter)

        when:
        emitter.emit(createMockProblem("foo"))

        emitter.report(null, null)

        then:
        1 * problemEmitter.emit(_ as Problem)
        0 * problemEmitter.emit(_ as List<ProblemAggregation>)
    }

    private createMockProblem(String categoryName) {
        def problem = Mock(Problem)
        problem.problemCategory >> new DefaultProblemCategory(categoryName)
        problem
    }
}
