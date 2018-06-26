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
    echo 1 > /data/zk/myid
    chown -R vagrant:vagrant /data
    nohup /usr/local/zookeeper/bin/zkServer.sh start&
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
    echo 2 > /data/zk/myid
    chown -R vagrant:vagrant /data
    nohup /usr/local/zookeeper/bin/zkServer.sh start&
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
    echo 3 > /data/zk/myid
    chown -R vagrant:vagrant /data
    nohup /usr/local/zookeeper/bin/zkServer.sh start&
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
  yum -y install java-1.8.0-openjdk
  yum -y install net-tools
  yum -y install nc
  cat /tmp/public.key >> /home/vagrant/.ssh/authorized_keys
  echo "127.0.0.1 localhost" > /etc/hosts
  echo "192.168.33.11 vm1" >> /etc/hosts
  echo "192.168.33.12 vm2" >> /etc/hosts
  echo "192.168.33.13 vm3" >> /etc/hosts
  cd /usr/local
  tar zxf /tmp/zookeeper-3.4.12.tar.gz -C ./
  ln -s zookeeper-3.4.12 zookeeper
  chown vagrant:vagrant zookeeper
  mkdir -p /data/zk
  mv -f /tmp/zoo.cfg /usr/local/zookeeper/conf/zoo.cfg
  systemctl enable firewalld.service
  systemctl start firewalld.service
  firewall-cmd --permanent --zone=public --add-source=192.168.33.0/24
  firewall-cmd --permanent --zone=public --add-source=127.0.0.0/24
  firewall-cmd --zone=public --add-port=2181/tcp
  firewall-cmd --zone=public --add-port=2888/tcp
  firewall-cmd --zone=public --add-port=3888/tcp
  firewall-cmd --reload
 SHELL
end
