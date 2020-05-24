# powerunit-extensions-matchers


* Travis ci : [![Build Status](https://travis-ci.org/powerunit/powerunit-extensions-matchers.svg?branch=master)](https://travis-ci.org/powerunit/powerunit-extensions-matchers)
* Vulnerabilities : [![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-matchers/depshield.svg)](https://depshield.github.io)
* Coverage : [![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-matchers/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-matchers?branch=master) - [![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-matchers/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-matchers) - [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-matchers?utm_source=github.com&utm_medium=referral&utm_content=powerunit/powerunit-extensions-matchers&utm_campaign=Badge_Coverage)
* Quality : [![Codacy Badge](https://api.codacy.com/project/badge/Grade/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-matchers?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-matchers&amp;utm_campaign=Badge_Grade) - [![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers) - [![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-matchers?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-matchers) - [![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-matchers-master)
* Maven : [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers)
* Git : ![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-matchers.png?style=cut)


This is an extension to powerunit (a unit test framework for java 8). [Please check the site for more information](http://powerunit.github.io/powerunit-extensions-matchers/) that doesn't require this framework, but provides generation of hamcrest matchers.

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
