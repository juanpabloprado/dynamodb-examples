## Micronaut test resources DynamoDB

Run `./gradlew -t run` or `./gradlew test` to spawn a test container for DynamoDB when running the app or launching tests using Testcontainers

### A bit more context

If you run `./gradlew test` or `./gradlew run`, a test container will be spawned, and shutdown at the end of the build.

If you run with `./gradlew -t test`, then the container will only be stopped at the end of the whole build session, which means that you can make changes to your code and reuse containers without paying the startup price.

Last but not least, you can also start the test resources server separately, making it possible to reuse it in independent builds:

- `./gradlew startTestResourcesService` : starts the test resources server
- `./gradlew run`: uses the test resources
- `./gradlew test`: uses the same test resources
- `./gradlew stopTestResourcesService`: stops the test resources server

Please read the [snapshot documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/snapshot/#test-resources) for all the possible configuration options.

### Testing the Application

To run the tests:
```bash
./gradlew test
```
```bash
open build/reports/tests/test/index.html
```

---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature aws-v2-sdk documentation

- [Micronaut AWS SDK 2.x documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/)

- [https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/welcome.html)

## Feature dynamodb documentation

- [Micronaut Amazon DynamoDB documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/#dynamodb)

- [https://aws.amazon.com/dynamodb/](https://aws.amazon.com/dynamodb/)


