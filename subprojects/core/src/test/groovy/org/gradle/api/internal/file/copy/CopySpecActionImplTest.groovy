/*
 * Copyright 2013 the original author or authors.
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
package org.gradle.api.internal.file.copy


import org.gradle.api.file.FileTree
import org.gradle.api.file.LinksStrategy
import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.internal.provider.PropertyHost
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.nativeintegration.filesystem.FileSystem
import org.gradle.internal.reflect.Instantiator
import spock.lang.Specification

class CopySpecActionImplTest extends Specification {
    CopyActionProcessingStreamAction action = Mock()
    Instantiator instantiator = Mock()
    ObjectFactory objectFactory = Mock()
    FileSystem fileSystem = Mock()
    CopySpecResolver copySpecResolver = Mock()
    FileTree source = Mock()
    Property<LinksStrategy> linksStrategy = new DefaultProperty<>(Mock(PropertyHost), LinksStrategy);

    def createAction() {
        new CopySpecActionImpl(action, instantiator, objectFactory, fileSystem, false)
    }

    def "can visit spec source"() {
        when:
        createAction().execute(copySpecResolver)

        then:
        1 * copySpecResolver.getSource() >> source
        1 * copySpecResolver.getLinksStrategy() >> linksStrategy
        1 * source.visit(_ as CopyFileVisitorImpl)
    }
}
