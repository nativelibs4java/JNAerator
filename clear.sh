for file in *
do
  if [[ -d "$file" ]]
  then
    rm $file/target -f -r
  fi
done