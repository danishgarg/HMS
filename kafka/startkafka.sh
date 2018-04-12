# {
#     BROKERS=$(/Users/gdanish/Athena/Personal/kafka_2.11-1.0.1/bin/zookeeper-shell.sh localhost:2181 ls /brokers/ids | tail -1)
# }
{
    BROKERS=$($KAFKA_HOME/bin/zookeeper-shell.sh $ZK ls /brokers/ids | tail -1)
} || {
    BROKERS="[]"
}

echo "-----------------------------------------------------------------------------------------------------------------------------------------"
echo "Output from zookeeper: ${BROKERS}"
echo "-----------------------------------------------------------------------------------------------------------------------------------------"

BROKERLIST=${BROKERS//,/}
BROKERLIST=${BROKERLIST//[/}
BROKERLIST=${BROKERLIST//]/}
if [[ $BROKERLIST =~ ^[0-9[:space:]]+$ ]];then
    BROKERLIST=($BROKERLIST)
else 
    BROKERLIST=()
fi
sortedBrokers=( $( printf "%s\n" "${BROKERLIST[@]}" | sort -n ) )

echo "-----------------------------------------------------------------------------------------------------------------------------------------"
echo "Occupied broker ids: ${BROKERLIST[@]}"
echo "-----------------------------------------------------------------------------------------------------------------------------------------"

NEXTBROKER=0
for id in ${BROKERLIST[@]}
do
    if [ $id == $NEXTBROKER ]
    then
        NEXTBROKER=$((NEXTBROKER+1))
    else
        break
    fi
done
echo "-----------------------------------------------------------------------------------------------------------------------------------------"
echo "Allocating broker id: $NEXTBROKER"
echo "-----------------------------------------------------------------------------------------------------------------------------------------"

NEXTBROKER=$(($NEXTBROKER + 0))
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties --override zookeeper.connect=${ZK} --override broker.id=$NEXTBROKER

