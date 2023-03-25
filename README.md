# powerunit-extensions-matchers


* Travis ci : [![Build Status](https://travis-ci.org/powerunit/powerunit-extensions-matchers.svg?branch=master)](https://travis-ci.org/powerunit/powerunit-extensions-matchers)
* Vulnerabilities : [![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-matchers?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-matchers/depshield.svg)](https://depshield.github.io)
* Coverage : [![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-matchers/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-matchers?branch=master) - [![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-matchers/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-matchers)
* Quality : [![Codacy Badge](https://app.codacy.com/project/badge/Grade/244810ab50934eebb04cd1d9da7fd57c)](https://www.codacy.com/gh/powerunit/powerunit-extensions-matchers?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-matchers&amp;utm_campaign=Badge_Grade) - [![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-matchers) - [![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-matchers?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-matchers) - [![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-matchers-master)
* Maven : [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-matchers)
* Git : ![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-matchers.png?style=cut)


This is an extension to powerunit (a unit test framework for java 8)  that doesn't require this framework, but provides generation of hamcrest matchers. [Please check the site for more information](http://powerunit.github.io/powerunit-extensions-matchers/)

**This version of the library doesn't support version below java 17**

# Usage

Basic usage is to add this libraray as a dependency and use the annotation :

```java
@ProvideMatchers
public class PojoShort {
  ...
}
```

Matchers classes must be created by the annotation processor (in this example, named `PojoShortMatchers`).
