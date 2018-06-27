# configure("2") indicates Vagrant version2
Vagrant.configure("2") do |config|
  ## machine specific settings
  # Virtual machine 1
  config.vm.define "vm1" do |vm1|
    vm1.vm.hostname = "vm1"
    vm1.ssh.port = 9122
    vm1.ssh.guest_port = 22
    vm1.vm.network "private_network", ip: "192.168.33.11"
    # id: 'ssh' override default ssh forwarded_port 2222 -> 22
    vm1.vm.network "forwarded_port", guest: 22, host: 9122, id: 'ssh'
    vm1.vm.network "forwarded_port", guest: 80, host: 8080
    # zookeeper identifier
    vm1.vm.provision "shell", inline: <<-SHELL
    sudo su -
    systemctl daemon-reload
    echo 1 > /data/zk/myid
    echo "broker.id=1" > /usr/local/kafka/config/server.properties
    echo "advertised.listeners=PLAINTEXT://192.168.33.11:9092" >> /usr/local/kafka/config/server.properties
    cat /tmp/server.properties >> /usr/local/kafka/config/server.properties
    SHELL
  end
  # Virtual machine 2
  config.vm.define "vm2" do |vm2|
    vm2.vm.hostname = "vm2"
    vm2.ssh.port = 9222
    vm2.ssh.guest_port = 22    
    vm2.vm.network "private_network", ip: "192.168.33.12"
    vm2.vm.network "forwarded_port", guest: 22, host: 9222, id: 'ssh'
    # httpd port (8080 -> 80) is only available at vm1
    vm2.vm.network "forwarded_port", guest: 80, host: 8080, disabled: true
    # zookeeper identifier
    vm2.vm.provision "shell", inline: <<-SHELL
    sudo su -
    systemctl daemon-reload
    echo 2 > /data/zk/myid
    systemctl start zookeeper-server.service
    echo "broker.id=2" > /usr/local/kafka/config/server.properties
    echo "advertised.listeners=PLAINTEXT://192.168.33.12:9092" >> /usr/local/kafka/config/server.properties
    cat /tmp/server.properties >> /usr/local/kafka/config/server.properties
    SHELL
  end
  # Virtual machine 3
  config.vm.define "vm3" do |vm3|
    vm3.vm.hostname = "vm3"
    vm3.ssh.port = 9322
    vm3.ssh.guest_port = 22    
    vm3.vm.network "private_network", ip: "192.168.33.13"
    vm3.vm.network "forwarded_port", guest: 22, host: 9322, id: 'ssh'
    # httpd port (8080 -> 80) is only available at vm1
    vm3.vm.network "forwarded_port", guest: 80, host: 8080, disabled: true
    # zookeeper identifier
    vm3.vm.provision "shell", inline: <<-SHELL
    sudo su -
    systemctl daemon-reload
    echo 3 > /data/zk/myid
    systemctl start zookeeper-server.service
    echo "broker.id=3" > /usr/local/kafka/config/server.properties
    echo "advertised.listeners=PLAINTEXT://192.168.33.13:9092" >> /usr/local/kafka/config/server.properties
    cat /tmp/server.properties >> /usr/local/kafka/config/server.properties
    # after all provision, start kafka daemon
    ssh -o StrictHostKeyChecking=no -i /tmp/id_rsa root@vm1 "systemctl start kafka-server.service;/usr/local/kafka/bin/kafka-topics.sh --zookeeper vm1:2181,vm2:2181,vm3:2181/gnu-kafka --replication-factor 1 --partitions 1 --topic gnu-topic --create"
    ssh -o StrictHostKeyChecking=no -i /tmp/id_rsa root@vm2 "systemctl start kafka-server.service"
    ssh -o StrictHostKeyChecking=no -i /tmp/id_rsa root@vm3 "systemctl start kafka-server.service"
    SHELL
  end
  
  ## common settings for all machines
  # Virtual machine system
  config.vm.box = "centos/7"
  config.vm.provider "virtualbox" do |v|
    v.memory = 1024
    v.cpus = 1
  end
  ## ssh settings
  config.ssh.forward_agent = true
  config.ssh.insert_key = false
  # public key is replaced with an user custom key
  config.vm.provision "file", source: './keys/public.key', destination: "/tmp/public.key"
  config.vm.provision "file", source: './provision', destination: "/tmp"
  # provisioning script
  config.vm.provision "shell", inline: <<-SHELL
  sudo su -
  # consumer alias
  alias consumer="/usr/local/kafka/bin/kafka-console-consumer.sh --bootstrap-server vm:9092,vm2:9092,vm3:9092 --topic gnu-topic --from-beginning"
  # root key setting
  mkdir -p /root/.ssh
  mv -f /tmp/id_rsa.pub /root/.ssh
  cat /root/.ssh/id_rsa.pub > /root/.ssh/authorized_keys
  chmod 600 -R /root/.ssh
  # kafka and zookeeper install
  cd /tmp
  chown root:root zookeeper-3.4.12.tar.gz
  chown root:root kafka_2.11-1.1.0.tgz
  chown root:root kafka-zookeeper.xml
  chown root:root kafka-server.service
  chown root:root zookeeper-server.service
  yum -y install java-1.8.0-openjdk
  yum -y install net-tools
  yum -y install nc
  # vargrant key and host setting
  cat /tmp/public.key >> /home/vagrant/.ssh/authorized_keys
  echo "127.0.0.1 localhost" > /etc/hosts
  echo "192.168.33.11 vm1" >> /etc/hosts
  echo "192.168.33.12 vm2" >> /etc/hosts
  echo "192.168.33.13 vm3" >> /etc/hosts
  cd /usr/local
  tar zxf /tmp/zookeeper-3.4.12.tar.gz -C ./
  tar zxf /tmp/kafka_2.11-1.1.0.tgz -C ./
  ln -s zookeeper-3.4.12 zookeeper
  ln -s kafka_2.11-1.1.0 kafka
  # zookeeper and kafka data dir
  mkdir -p /data/zk
  mkdir -p /data1
  mkdir -p /data2
  mkdir -p /data3
  mkdir -p /data4
  mv -f /tmp/zoo.cfg /usr/local/zookeeper/conf/zoo.cfg
  #firewall open
  mv -f /tmp/kafka-zookeeper.xml /usr/lib/firewalld/services
  ln -s /usr/lib/firewalld/services/kafka-zookeeper.xml /etc/firewalld/services/kafka-zookeeper.xml
  systemctl enable firewalld.service
  systemctl start firewalld.service
  firewall-cmd --permanent --zone=public --add-service=kafka-zookeeper
  firewall-cmd --reload
  #daemon setting
  mv -f /tmp/zookeeper-server.service /etc/systemd/system/zookeeper-server.service
  mv -f /tmp/kafka-server.service /etc/systemd/system/kafka-server.service
 SHELL
end
