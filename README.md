# rest-storage

[![CI with Maven](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/maven.yml/badge.svg)](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/maven.yml)
[![CI with CodeQL](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/codeql.yml/badge.svg)](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/codeql.yml)
[![CI with Sonar](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/sonar.yml/badge.svg)](https://github.com/thiagolvlsantos/rest-storage/actions/workflows/sonar.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_rest-storage&metric=alert_status)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_rest-storage)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_rest-storage&metric=coverage)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_rest-storage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/rest-storage/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/rest-storage/)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

A generic endpoint for versioned entities stored in Git repositories using git-transactions and file-storage.

## Usage

Include latest version [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/rest-storage/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/rest-storage/) to your project.

```xml
		<dependency>
			<groupId>io.github.thiagolvlsantos</groupId>
			<artifactId>rest-storage</artifactId>
			<version>${latestVersion}</version>
		</dependency>
```

## Add `@EnableRestStorage` to you app.

```java
...
@EnableRestStorage
public class Application {
	...main(String[] args) {...}
}
```

```java
// ...
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FileRepo(Tag.REPO)
public class Tag extends FileObjectVersionedAuditable {

	public static final String REPO = "tags";

	@Schema(description = "Tags's name.", example = "openshift", required = true)
	@NotNull
	@NotBlank
	@Size(min = 1)
	@FileKey
	private String name;

	@Schema(description = "Tag's description.", example = "Tag for openshift items.")
	private String description;
}
```

Example using YAMLs configuration file.

```yaml
# OpenAPI params
springdoc:
  api-docs:
    path: /swagger
  swagger-ui:
    path: /swagger/ui
    operationsSorter: alpha # Sorting endpoints alphabetically
    tagsSorter: alpha       # Sorting tags alphabetically

# Git-Transactions properties
gitt:
  repository:
    # General properties
    user: ${YOUR_USER}
    password: ${YOUR_TOKEN}
    # tags properties
    tags:
      read: data/read/tags
      write: data/write/tags
      remote: https://github.com/thiagolvlsantos/git-example.git
```

## Endpoints

According to this setup, after server start, the OpenApi interface will be available at [http://localhost:8080/swagger](http://localhost:8080/swagger) and Swagger UI at [http://localhost:8080/swagger/ui](http://localhost:8080/swagger/ui).

![Auto-generated API](doc/img/swagger-ui.jpg =x600)

## Build

Localy, from this root directory call Maven commands or `bin/<script name>` at our will.
