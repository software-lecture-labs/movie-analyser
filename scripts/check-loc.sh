#!/usr/bin/env bash
# Checks line limits: .java files <= 500 lines, other text files <= 1000 lines.
#
# Usage:
#   bash scripts/check-loc.sh              checks every tracked file
#   bash scripts/check-loc.sh origin/dev   checks files added or changed since origin/dev
set -euo pipefail

readonly JAVA_LIMIT=500
readonly OTHER_LIMIT=1000

# core.quotePath=false keeps non-ASCII file names (for example Turkish characters)
# readable; otherwise git quotes them and the files would be skipped silently.
if [ $# -gt 0 ]; then
  files=$(git -c core.quotePath=false diff --name-only --diff-filter=ACMR "$1...HEAD")
else
  files=$(git -c core.quotePath=false ls-files)
fi

failed=0
checked=0
while IFS= read -r file; do
  [ -n "$file" ] || continue
  [ -f "$file" ] || continue
  # Skip binary files (images, archives); an empty file has nothing to check
  [ -s "$file" ] || continue
  grep -Iq . "$file" || continue

  lines=$(wc -l < "$file" | tr -d ' ')
  case "$file" in
    *.java) limit=$JAVA_LIMIT ;;
    *) limit=$OTHER_LIMIT ;;
  esac

  checked=$((checked + 1))
  if [ "$lines" -gt "$limit" ]; then
    echo "::error file=$file::$file has $lines lines (limit $limit)"
    failed=1
  fi
done <<< "$files"

if [ "$failed" -ne 0 ]; then
  echo "Line limit check failed."
  exit 1
fi
echo "Line limit check passed ($checked files)."
