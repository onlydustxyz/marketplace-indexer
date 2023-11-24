web: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=api,github -Ddd.service=indexer-api
jobs: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=job -Ddd.service=indexer-jobs
cli: java -Dspring.profiles.active=local,cli -jar bootstrap/target/marketplace-indexer.jar
