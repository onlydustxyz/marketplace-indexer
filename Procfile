web: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=api,github -Ddd.service=indexer-api
jobs: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=job -Ddd.service=indexer-jobs
cli: java -d64 -Xms5G -Xmx10G -XX:+UseG1GC -XX:+UseStringDeduplication -Dspring.profiles.active=local,cli -jar bootstrap/target/marketplace-indexer.jar
