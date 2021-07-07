cd ~/Documents/GitHub/COVID19-Data-Analysis/scalable-solution/COVID19-Data-Analysis
mvn compile
mvn package
cd /home/linuxbrew/.linuxbrew/Cellar/apache-spark/3.0.1/bin
sudo ./spark-submit --class SparkWorks.SparkMainApp ~/Documents/GitHub/COVID19-Data-Analysis/scalable-solution/COVID19-Data-Analysis/target/test-1.8-SNAPSHOT.jar --master spark://$hostname:7077
cd ~
