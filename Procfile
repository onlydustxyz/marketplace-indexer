web: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=api,github -Ddd.service=indexer-api
jobs: ./heroku/start-dyno-with-datadog-apm.sh -Dspring.profiles.active=job -Ddd.service=indexer-jobs
cli: ./heroku/start-dyno-with-datadog-apm.sh -Xms5G -Xmx10G -XX:+UseG1GC -XX:+UseStringDeduplication -Dspring.profiles.active=local,cli -Ddd.service=indexer-cli
