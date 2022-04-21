# Test app for APM

## Running APM env

in order to run the APM environment run `docker-compose up`

## Running application

Run application in one or more environmental formats for the demo.
Dev, staging, and prod have different log configurations (check property files for more details). 

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=predev # doesn't send anything to AMP
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=staging
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Populating APM with test data

In order to populate APM make `test-apis.sh` executable and run it:

```shell
chmod +x test-apis.sh
./test-apis.sh
```

This should create enough cases for the test/demo.