#!/usr/bin/zsh
SCRIPT=${0##*/}
RUNUSER=$(whoami)
SIZE=102400  #100k
TARGETDIR=/home/${RUNUSER}
LOG=/tmp/${SCRIPT%%.sh}_${RUNUSER}.log
FILES=($(find ${TARGETDIR} -type f -size +${SIZE}c))
 
echo "Showing all files larger than ${SIZE} bytes in ${TARGETDIR} sorted by size."
find ${TARGETDIR} -type f -size +${SIZE}c -ls | sort +6 -nr
echo -e "\n\n____File menu____"
while [[ ${#FILES[@]} -gt 0 ]]; do
        PS3="Please choose a file:"
        select file in ${FILES[@]}
                do
                        [[ -z ${file} ]] && {
                                echo "Invalid choice!"
                        } || {
                break
                        }
        done    
        
PS3="Select an option:"
echo -e "\nYou have selected the below file. Do you want to REMOVE or ZIP the file?"
ls -ltr ${file}
 
select option in ZIP REMOVE BACK
do
        [[ -z ${option} ]] && {
                echo "Invalid choice!"
        } || {
                break
        }
done
 
case ${option} in
        "ZIP" )
        echo "You chose ZIP. Zipping file now..."
        gzip ${file}
        echo "$(date "+%F %T") ${file} compressed."
                ;;
                
        "REMOVE" )
        echo "You chose REMOVE. Removing file now..."
        rm ${file}
        echo "$(date "+%F %T") ${file} removed." | tee -a ${LOG}
                ;;
                
        "BACK" )
        continue
                ;;
esac
 
FILES=($(find ${TARGETDIR} -type f -size +${SIZE}c))
 
done