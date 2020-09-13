# powerunit-extensions-matchers


* Travis ci : [![Build Status](https://travis-ci.org/powerunit/powerunit-extensions-matchers.svg?branch=master)](https://travis-ci.org/powerunit/powerunit-extensions-matchers)
* Vulnerabilities : [![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-matchers/depshield.svg)](https://depshield.github.io)
* Coverage : [![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-matchers/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-matchers?branch=master) - [![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-matchers/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-matchers)
* Quality : [![Codacy Badge](https://app.codacy.com/project/badge/Grade/244810ab50934eebb04cd1d9da7fd57c)](https://www.codacy.com/gh/powerunit/powerunit-extensions-matchers?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-matchers&amp;utm_campaign=Badge_Grade) - [![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers) - [![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-matchers?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-matchers) - [![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-matchers-master)
* Maven : [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers)
* Git : ![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-matchers.png?style=cut)


This is an extension to powerunit (a unit test framework for java 8)  that doesn't require this framework, but provides generation of hamcrest matchers. [Please check the site for more information](http://powerunit.github.io/powerunit-extensions-matchers/)

# Usage

Basic usage is to add this libraray as a dependency and use the annotation :

```java
@ProvideMatchers
public class PojoShort {
  ...
}
```

Matchers classes must be created by the annotation processor (in this example, named `PojoShortMatchers`).

# Compatibility

Powerunit-extensions-matchers version | With          | Version | Status
------------------------------------- | ------------- | ------- | -------
0.1.6                                 | hamcrest      | 1.3     | :heavy_check_mark:
0.1.6                                 | hamcrest      | 2.1+    | :grey_question: Not tested by itself, but as some extension use this version, should be OK
0.1.6                                 | JDK           | 1.8     | :heavy_check_mark:
0.1.6                                 | JDK           | 9       | Compilation with source and target = 1.8 tested. The `@Generated` annotation may not be available OOTB and the META-INF information are not generated
0.1.6                                 | JDK           | 10      | Compilation with source and target = 1.8 tested. The `@Generated` annotation may not be available OOTB and the META-INF information are not generated
0.2.0                                 | hamcrest      | 1.3     | :heavy_check_mark: The `@Factory` annotation is not produced or parsed anymore.
0.2.0                                 | hamcrest      | 2.1+    | :heavy_check_mark:
0.2.0                                 | JDK           | 1.8     | :heavy_check_mark:
0.2.0                                 | JDK           | 9       | Compilation with source and target = 1.8 tested. Execution of generated matcher tested. The `@Generated` annotation may not be available OOTB.
0.2.0                                 | JDK           | 10      | Compilation with source and target = 1.8 tested. Execution of generated matcher tested. The `@Generated` annotation may not be available OOTB.
0.3.0                                 | hamcrest      | 1.3     | :heavy_check_mark:
0.3.0                                 | hamcrest      | 2.1+    | :heavy_check_mark:
0.3.0                                 | JDK           | 1.8     | :heavy_check_mark:
0.3.0                                 | JDK           | 9       | Compilation with source and target = 1.9 tested. Execution of generated matcher tested. The `@Generated` annotation may not be available OOTB.
0.3.0                                 | JDK           | 10      | Compilation with source and target = 1.9 tested. Execution of generated matcher tested. The `@Generated` annotation may not be available OOTB.
0.3.0                                 | powerunit-extensions-matchers | 0.2.0 | :heavy_check_mark: Matchers generated with version 0.2.0 can be used from matcher of the version 0.3.0. The ignored fields and cycle detection is not propagated to the 0.2.0 matchers.

# Migration

## Migration to version 0.2.X

The version 0.2.0 introduces some major changes in the annotation processor. The idea is mainly that this annotation processor must support several version
of the hamcrest library.

When migrating to this version, some actions may be needed :


*   The library doesn't provide anymore a transitive dependency to the hamcrest library. You may need to add this library as a dependency of your project.
	Dependency may be : org.hamcrest:hamcrest:2.2 ; org.hamcrest:hamcrest:2.1 ; org.hamcrest:hamcrest-all:1.3.
*   The artifacts powerunit-extensions-matchers-factory and powerunit-extensions-matchers-providematchers are not available any more. Only the master
    artifact **powerunit-extensions-matchers** is available and only this one can be used.
*   The generated classes doesn't use anymore the `@Factory` annotation. This annotation only exists in hamcrest 1.3. If you use the annotation processor
    based on the configuration property `ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor.targets` you
    will have to change your configuration (see below). This feature was based on this annotation and only compatible with hamcrest 1.3
*   By default, the annotation processor only generate the Matchers. If a *master* *DSL* interface is required (like the ones produced by the old configuration 
    `ch.powerunit.extensions.matchers.factoryprocessor.FactoryAnnotationsProcessor.targets` you may use the configuration property
    `ch.powerunit.extensions.matchers.provideprocessor.ProvidesMatchersAnnotationsProcessor.factory`. This property takes as value the
    fully qualified name of an interface to be generated, which will expose all the entry DSL methods generated by this annotation processor. This interface **only** 
    contains these entry DSL methods ; no additional methods may be added (as it was possible before with `@Factory` annotation).
*   The detection of link between classes and related Matcher has been enhanced. It is possible that for some case when before a field or a parent was not 
    linked, now there are correctly linked. It may change the way the matcher operate (using another matcher to compare and not a `is` Matcher).
*   The META-INF xml file is not generated anymore. This should not be an issue, except if you use it. Metadatas information are added directly to the generated
    matchers.