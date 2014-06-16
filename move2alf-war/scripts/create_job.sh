if [[ $@<2 ]]
then
    echo "Usage: create_job.sh <folder> <noderef>"
    exit
fi

folder=$1
noderef=$2
echo "folder=$folder, noderef=$noderef"

curl -s -XPOST --user admin:admin "http://localhost:8081/move2alf-war/job/create" -d "name=Vivium Employee Benefits $folder&description=Vivium Employee Benefits $folder&inputSource=CMIS&inputPath=&cmisURL=http%3A//pvx7alfebhr.ux.pv.be/alfresco/cmisatom&cmisUsername=mgt_alf-m2alf_acc&cmisPassword=R8W2uL2f_a&extension=*&cmisQuery=SELECT * FROM cmis:document WHERE IN_TREE('$noderef')&dest=1&contentStoreId=-1&destinationFolder=/eb-hr&skipContentUpload=true&command=&metadata=CMISMetadataAction&paramMetadataName=&paramMetadataValue=&transform=notransformation&paramTransformName=&paramTransformValue=&commandAfter=&mode=WRITE&writeOption=OVERWRITE&deleteOption=SKIPANDIGNORE"