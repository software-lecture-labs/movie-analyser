#!/usr/bin/env bash
# Satır sınırlarını kontrol eder: .java dosyaları <= 500 satır, diğer metin dosyaları <= 1000 satır.
#
# Kullanım:
#   bash scripts/check-loc.sh              takip edilen tüm dosyaları kontrol eder
#   bash scripts/check-loc.sh origin/dev   origin/dev'den beri eklenen veya değişen dosyaları kontrol eder
set -euo pipefail

readonly JAVA_LIMIT=500
readonly OTHER_LIMIT=1000

# core.quotePath=false ASCII olmayan dosya adlarının (örneğin Türkçe karakterler)
# okunabilir kalmasını sağlar; aksi halde git bunları tırnaklar ve dosyalar sessizce atlanırdı.
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
  # İkili dosyaları (görseller, arşivler) atla; boş dosyada kontrol edilecek bir şey yok
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
