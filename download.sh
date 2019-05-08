# Script to fast download all the stack
VERSION="7.0.0"

declare -a links=(
    "https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-$VERSION.tar.gz"
    "https://artifacts.elastic.co/downloads/kibana/kibana-$VERSION-darwin-x86_64.tar.gz"
    # "https://artifacts.elastic.co/downloads/kibana/kibana-$VERSION-linux-x86_64.tar.gz"
    # "https://artifacts.elastic.co/downloads/logstash/logstash-$VERSION.tar.gz"
    "https://artifacts.elastic.co/downloads/elasticsearch-hadoop/elasticsearch-hadoop-$VERSION.zip"
    "https://artifacts.elastic.co/downloads/apm-server/apm-server-$VERSION-darwin-x86_64.tar.gz"
    # "https://artifacts.elastic.co/downloads/apm-server/apm-server-$VERSION-linux-x86_64.tar.gz"
    )

mkdir -p binaries

for i in "${links[@]}"
do
   echo "Downloading from $i"
   wget -c $i -P binaries/
done