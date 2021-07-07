cd ~/hadoop-3.2.1/sbin
./start-dfs.sh
jps
cd /home/linuxbrew/.linuxbrew/Cellar/apache-spark/3.0.1/libexec/sbin
sudo ./start-master.sh
sudo ./start-slave.sh spark://$hostname:7077
cd ~
