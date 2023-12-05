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

package org.gradle.api.file;

import org.gradle.api.GradleException;
import org.gradle.api.Incubating;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Strategy for handling symbolic links when copying files.
 *
 * @since 8.6
 */
@Incubating
public interface LinksStrategy extends Serializable {
    /**
     * Do not preserve any symlinks. This is the default.
     *
     * @since 8.6
     **/
    LinksStrategy FOLLOW = new LinksStrategy() {
        @Override
        public boolean preserveLinks() {
            return false;
        }

        @Override
        public void maybeThrowOnBrokenLink(FileVisitDetails fileDetails) {
            if (fileDetails.isSymbolicLink()) { //since we follow all links, it can only be a broken link
                Path linkPath;
                try {
                    linkPath = Files.readSymbolicLink(fileDetails.getFile().toPath());
                } catch (IOException e) {
                    throw new GradleException(String.format("Couldn't read symbolic link '%s'.", pathHint(fileDetails)));
                }
                throw new GradleException(String.format("Couldn't follow symbolic link '%s' pointing to '%s'.", pathHint(fileDetails), linkPath));
            }
        }

        @Override
        public String toString() {
            return "FOLLOW";
        }
    };
    /**
     * Preserve relative symlinks, throw an exception if a symlink points outside the root directory of copy spec.
     * It also throws an exception for broken links and links pointing to another link.
     * @since 8.6
     **/
    LinksStrategy PRESERVE_RELATIVE = new LinksStrategy() {
        @Override
        public void maybeThrowOnBrokenLink(FileVisitDetails fileDetails) {
            if (fileDetails.getSymbolicLinkDetails() != null && !fileDetails.getSymbolicLinkDetails().isRelative()) {
                throw new GradleException(String.format("Links strategy is set to %s, but a symlink pointing outside was visited: '%s' pointing to '%s'.",
                    this, pathHint(fileDetails), fileDetails.getSymbolicLinkDetails().getTarget())
                );
            }
        }

        @Override
        public String toString() {
            return "PRESERVE_RELATIVE";
        }
    };
    /**
     * Preserve all symlinks, even if they point to non-existent paths.
     * @since 8.6
     **/
    LinksStrategy PRESERVE_ALL = new LinksStrategy() {
        @Override
        public void maybeThrowOnBrokenLink(FileVisitDetails fileDetails) {
            // do nothing
        }

        @Override
        public String toString() {
            return "PRESERVE_ALL";
        }
    };
    /**
     * Throw an error if a symlink is visited.
     * @since 8.6
     **/
    LinksStrategy ERROR = new LinksStrategy() {
        @Override
        public void maybeThrowOnBrokenLink(FileVisitDetails fileDetails) {
            if (fileDetails.getSymbolicLinkDetails() != null) {
                throw new GradleException(String.format("Links strategy is set to %s, but a symlink was visited: '%s' pointing to '%s'.",
                    this, pathHint(fileDetails), fileDetails.getSymbolicLinkDetails().getTarget())
                );
            }
        }

        @Override
        public String toString() {
            return "ERROR";
        }
    };

    static String pathHint(FileVisitDetails fileDetails) {
        String originalPath = fileDetails.getPath();
        return originalPath.equals("") ? "." : originalPath;
    }

    default boolean preserveLinks() {
        return true;
    }

    default void maybeThrowOnBrokenLink(FileVisitDetails fileDetails) {
        if (fileDetails.getSymbolicLinkDetails() != null && !fileDetails.getSymbolicLinkDetails().targetExists()) {
            throw new GradleException(String.format("Couldn't follow symbolic link '%s' pointing to '%s'.",
                pathHint(fileDetails), fileDetails.getSymbolicLinkDetails().getTarget())
            );
        }
    }

}
