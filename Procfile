web: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=api,github -Ddd.service=indexer-api
jobs: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=job -Ddd.service=indexer-jobs
cli: java -Xms5G -Xmx10G -XX:+UseG1GC -XX:+UseStringDeduplication -Dspring.profiles.active=local,cli -Ddd.service=indexer-cli -XX:FlightRecorderOptions=stackdepth=256 -jar bootstrap/target/marketplace-indexer.jar
liquibase: java -Dspring.profiles.active=local,cli,liquibase -Ddd.service=indexer-cli -jar bootstrap/target/marketplace-indexer.jar
