#!/bin/bash

SCRIPT_DIR=$(readlink -f "$0" | xargs dirname)
. "$SCRIPT_DIR"/utils.sh

# Variables
BUCKET_NAME="githubarchivedownloadstack-ghadatabucket05a9f62a-td7l0nngguru"
YEAR_TO_CHECK=$1
MONTH_TO_CHECK=$2
DAY_TO_CHECK=$3

# Check if year is provided
if [ -z "$YEAR_TO_CHECK" ]; then
    echo "Usage: $0 <year>"
    exit 1
fi

# Function to calculate days in a month
get_days_in_month() {
    local YEAR_TO_CHECK=$1
    local MONTH=$2

    case $MONTH in
        1|3|5|7|8|10|12) echo 31 ;;
        4|6|9|11) echo 30 ;;
        2)
            if (( (YEAR_TO_CHECK % 4 == 0 && YEAR_TO_CHECK % 100 != 0) || (YEAR_TO_CHECK % 400 == 0) )); then
                echo 29  # Leap year
            else
                echo 28
            fi
            ;;
        *) echo 0 ;;  # Invalid month
    esac
}

CURRENT_YEAR=$(date +'%Y')
CURRENT_MONTH=$(date +'%m')
CURRENT_DAY=$(date +'%d')
CURRENT_HOUR=$(date +'%H')

MONTHS=$(if [ -z "$MONTH_TO_CHECK" ]; then seq 1 12; else echo "$MONTH_TO_CHECK"; fi)
for MONTH in $MONTHS; do

    DAYS_IN_MONTH=$(get_days_in_month "$YEAR_TO_CHECK" "$MONTH")
    DAYS=$(if [ -z "$DAY_TO_CHECK" ]; then seq 1 "$DAYS_IN_MONTH"; else echo "$DAY_TO_CHECK"; fi)
    for DAY in $DAYS; do

        log_info "[$(date +'%Y-%m-%d %H:%M:%S')] Checking $YEAR_TO_CHECK/$MONTH/$DAY"

        for HOUR in $(seq 0 23); do
            # skip if in the future
            if [ "$YEAR_TO_CHECK" -gt " $CURRENT_YEAR" ]; then
                continue
            fi

            if [ "$YEAR_TO_CHECK" -eq " $CURRENT_YEAR" ] && [ "$MONTH" -gt " $CURRENT_MONTH" ]; then
                continue
            fi

            if [ "$YEAR_TO_CHECK" -eq " $CURRENT_YEAR" ] && [ "$MONTH" -eq " $CURRENT_MONTH" ] && [ "$DAY" -gt "$CURRENT_DAY" ]; then
                continue
            fi

            if [ "$YEAR_TO_CHECK" -eq " $CURRENT_YEAR" ] && [ "$MONTH" -eq " $CURRENT_MONTH" ] && [ "$DAY" -eq "$CURRENT_DAY" ] && [ "$HOUR" -gt "$CURRENT_HOUR" ]; then
                continue
            fi
            
            # Construct the prefix
            PREFIX="year=${YEAR_TO_CHECK}/month=${MONTH}/day=${DAY}/hour=${HOUR}/"

            # List objects in the current prefix
            OBJECT_COUNT=$(aws --profile production s3api list-objects-v2 --bucket "$BUCKET_NAME" --prefix "$PREFIX" --query "Contents[].Key" --output json | jq -r '. | length')

            if [ -z "$OBJECT_COUNT" ]; then
                log_error "☠️ Error listing objects"
                exit 1
            fi

            # Check if exactly one object is present
            case $OBJECT_COUNT in
                0) if ! curl -Is "https://data.gharchive.org/$YEAR_TO_CHECK-$MONTH-$DAY-$HOUR.json.gz" | grep -q "HTTP/2 404"; then
                      # pad with zeros
                      from=$(printf "%04d-%02d-%02dT%02d:00:00Z" "$YEAR_TO_CHECK" "$MONTH" "$DAY" "$HOUR")
                      to=$(printf "%04d-%02d-%02dT%02d:00:00Z" "$YEAR_TO_CHECK" "$MONTH" "$DAY" "$((HOUR+1))")
                      jo "from=$from" "to=$to"
                    fi ;;
                1) ;;
                *) log_error "❌ $PREFIX: Found $OBJECT_COUNT objects" ;;
            esac
        done
    done
done
