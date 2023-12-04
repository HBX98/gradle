The Gradle team is excited to announce Gradle @version@.

This release features [1](), [2](), ... [n](), and more.

<!-- 
Include only their name, impactful features should be called out separately below.
 [Some person](https://github.com/some-person)

 THiS LIST SHOULD BE ALPHABETIZED BY [PERSON NAME] - the docs:updateContributorsInReleaseNotes task will enforce this ordering, which is case-insensitive.
-->
We would like to thank the following community members for their contributions to this release of Gradle:

## Upgrade instructions

Switch your build to use Gradle @version@ by updating your wrapper:

`./gradlew wrapper --gradle-version=@version@`

See the [Gradle 8.x upgrade guide](userguide/upgrading_version_8.html#changes_@baseVersion@) to learn about deprecations, breaking changes and other considerations when upgrading to Gradle @version@.

For Java, Groovy, Kotlin and Android compatibility, see the [full compatibility notes](userguide/compatibility.html).

## New features and usability improvements

<!-- Do not add breaking changes or deprecations here! Add them to the upgrade guide instead. -->

<!--

================== TEMPLATE ==============================

<a name="FILL-IN-KEY-AREA"></a>
### FILL-IN-KEY-AREA improvements

<<<FILL IN CONTEXT FOR KEY AREA>>>
Example:
> The [configuration cache](userguide/configuration_cache.html) improves build performance by caching the result of
> the configuration phase. Using the configuration cache, Gradle can skip the configuration phase entirely when
> nothing that affects the build configuration has changed.

#### FILL-IN-FEATURE
> HIGHLIGHT the usecase or existing problem the feature solves
> EXPLAIN how the new release addresses that problem or use case
> PROVIDE a screenshot or snippet illustrating the new feature, if applicable
> LINK to the full documentation for more details

================== END TEMPLATE ==========================


==========================================================
ADD RELEASE FEATURES BELOW
vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv -->

### Public API improvements

#### Enhanced name-based filtering on NamedDomainObject containers

A new [`named(Spec<String>)` method](javadoc/org/gradle/api/NamedDomainObjectCollection.html#named-org.gradle.api.specs.Spec-) has been added to all NamedDomainObject containers, which simplifies name-based filtering and eliminates the need to touch any of the values, may they be realized or unrealized.

#### Allow Providers to be used with capabilities

[`Providers`](javadoc/org/gradle/api/provider/Provider.html) can now be passed to capability methods
[`ConfigurationPublications#capability(Object)`](javadoc/org/gradle/api/artifacts/ConfigurationPublications.html#capability-java.lang.Object-),
[`ModuleDependencyCapabilitiesHandler#requireCapability(Object)`](javadoc/org/gradle/api/artifacts/ModuleDependencyCapabilitiesHandler.html#requireCapability-java.lang.Object-),
and [`CapabilitiesResolution#withCapability(Object, Action)`](javadoc/org/gradle/api/artifacts/CapabilitiesResolution.html#withCapability-java.lang.Object-org.gradle.api.Action-).

### Error and warning reporting improvements

Gradle provides a rich set of error and warning messages to help you understand and resolve problems in your build.

#### Dependency locking now separates the error from the possible action to try

[Dependency locking](userguide/dependency_locking.html) is a mechanism for ensuring reproducible builds when using dynamic dependency versions.

This release improves error messages by separating the error from the possible action to fix the issue in the console output.
Errors from invalid [lock file format](userguide/dependency_locking.html#lock_state_location_and_format) or [missing lock state when strict mode is enabled](userguide/dependency_locking.html#fine_tuning_dependency_locking_behaviour_with_lock_mode) are now displayed as illustrated below:

```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':dependencies'.
> Could not resolve all dependencies for configuration ':lockedConf'.
   > Invalid lock state for lock file specified in '<project>/lock.file'. Line: '<<<<<<< HEAD'

* Try:
> Verify the lockfile content. For more information on lock file format, please refer to https://docs.gradle.org/@version@/userguide/dependency_locking.html#lock_state_location_and_format in the Gradle documentation.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
```

<a name="symlinks"></a>

### Symlinks support in Copy tasks

One of the core principles of Gradle was to treat any symbolic link as a file it points to.
However, there are some cases when this approach is not desirable.
One of the common use cases is packing or unpacking archives with symlinks: symlinks help reduce archive size and speed up the packing process.
The same can be said about copying files with symlinks: it is a lot faster to copy a symlink than a file or a directory.

This release presents a new property to [`CopySpec`](javadoc/org/gradle/api/file/CopySpec.html): `linksStrategy`.
Using this property, you can control how symlinks are handled during the copy process.

The default value of `linksStrategy` for a regular copy task is `LinksStrategy.FOLLOW`, which preserves the existing behavior of Gradle of following every symlink.
If the source file is an archive ([`zipTree`](javadoc/org/gradle/api/file/ArchiveOperations.html#zipTree-java.lang.Object-)
or [`tarTree`](javadoc/org/gradle/api/file/ArchiveOperations.html#tarTree-java.lang.Object-), to be more specific), then the default value is `LinksStrategy.PRESERVE_RELATIVE`.
With this strategy, symlinks from the archive are preserved unless they are pointing to a location outside the archive.
Other possible strategies are `LinksStrategy.ERROR` to fail on any symlink and `LinksStrategy.PRESERVE_ALL` which preserves all symlinks.

For example, this directory structure

```
--- input
    --- file.txt // "Some text"
    --- symlink -> file.txt
```

would be copied to the following directory structure

```
--- output
    --- file.txt // "Some text"
    --- symlink // "Some text"
```

using this task:

```groovy
tasks.register('doCopy', Copy) {
    from("input")
    into("output")
    linksStrategy = LinksStrategy.FOLLOW
}
```

```kotlin
tasks.register<Copy>("doCopy") {
    from("input")
    into("output")
    linksStrategy = LinksStrategy.FOLLOW
}
```

The same directory structure would be copied "as is" with `LinksStrategy.PRESERVE_ALL` and `LinksStrategy.PRESERVE_RELATIVE`.

See more details in the [User Manual](userguide/working_with_files.html#symlinks).


<!-- ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ADD RELEASE FEATURES ABOVE
==========================================================

-->

## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User Manual section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

## Fixed issues

<!--
This section will be populated automatically
-->

## Known issues

Known issues are problems that were discovered post release that are directly related to changes made in this release.

<!--
This section will be populated automatically
-->

## External contributions

We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](https://gradle.org/contribute).

## Reporting problems

If you find a problem with this release, please file a bug on [GitHub Issues](https://github.com/gradle/gradle/issues) adhering to our issue guidelines.
If you're not sure you're encountering a bug, please use the [forum](https://discuss.gradle.org/c/help-discuss).

We hope you will build happiness with Gradle, and we look forward to your feedback via [Twitter](https://twitter.com/gradle) or on [GitHub](https://github.com/gradle).
