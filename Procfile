web: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=api,github -DDD_SERVICE=indexer-api
jobs: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=job -DDD_SERVICE=indexer-jobs
cli: java -Dspring.profiles.active=local,cli -jar bootstrap/target/marketplace-indexer.jar
