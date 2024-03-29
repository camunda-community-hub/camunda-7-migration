[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![](https://img.shields.io/badge/Lifecycle-Abandoned-lightgrey)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#abandoned-)
[![](https://img.shields.io/badge/Lifecycle-Needs%20Maintainer%20-ff69b4)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#abandoned-)
[![Build project with Maven](https://github.com/camunda-community-hub/camunda-bpm-migration/actions/workflows/build.yml/badge.svg)](https://github.com/camunda-community-hub/camunda-bpm-migration/actions/workflows/build.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension.migration/camunda-bpm-migration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.camunda.bpm.extension.migration/camunda-bpm-migration)
![Compatible with: Camunda Platform 7](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%207-26d07c)


# camunda-7-migration

camunda-7-migration is a framework for making process instance migration easier.
It builds upon the migration functionality introduced in camunda BPM 7.5.

Camunda already provides the ability to do a migration based on process definition mapping.
Still, the migration plans can only be defined for deployed processes. So it is hard to impossible to define a migration plan on a dev machine, test it on pre-productoin and then apply it to the live system.

We add stuff like
* deployment independent migration plans
* variable mapping

And maybe some day some fancy sh*t like
* migrating only some instances, e.g. everything that is before that super-critical service task

## Installation

To use this extension, please add the following dependency to your `pom.xml`:
```xml
<dependency>
  <groupId>org.camunda.bpm.extension.migration</groupId>
  <artifactId>camunda-bpm-migration-parent</artifactId>
  <version>1.0</version>
</dependency>
```

## PAQ (Possibly Asked Questions)

### Why are migration plans "stored" as Java code?
Each process implementation has its own specialties. When process instances are migrated, a lot of implementation details have to be considered.
So, who is able to understand these details best? Who is able to provide a really good migration plan?
Ah, right, the developer!
All developers that work with camunda speak one common language: Java
Do I have to say more?

## Get started

_A quick description how your project can be used, including where the relevant resources can be obtained from.
Put into another file if too big._


## Resources

* [Issue Tracker](link-to-issue-tracker) _use github unless you got your own_
* [Roadmap](link-to-issue-tracker-filter) _if in terms of tagged issues_
* [Changelog](link-to-changelog) _lets users track progress on what has been happening_
* [Download](link-to-downloadable-archive) _if downloadable_
* [Contributing](link-to-contribute-guide) _if desired, best to put it into a CONTRIBUTE.md file_


## Roadmap

## Release _*BARN SWALLOW*_
1. deployment-independent migration plans
1. variable mapping

### Why "Barn Swallow"?
1. Software is cooler, when it has a name.
1. The Barn Swallow is also known as European Swallow and famous from the movie "Monty Python and the Holy Grail".
1. The Barn Swallow is a _migratory_ bird. Maybe I should have put this first.


## Maintainer

_Your Name with link to Github profile or email_


## License

* [Apache License, Version 2.0](./LICENSE)

