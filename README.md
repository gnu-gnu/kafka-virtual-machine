# kafka-virtual-machine
------
### 필요 환경
- Java8 (JRE 1.8.x) 이상
- vagrant 2.11
- Oracle virtualbox 5.2.x
### 구성 요소
- 가상머신 Provision용 Vagrantfile (CPU 1core, RAM 1GB VM 3대)
- kafka 2.11-1.10.0
- zookeeper 3.4.12
- SSH 서버용 key (public.key) 및 vagrant 계정 로그인용 putty 개인키 (private.ppk)
- 서버간 통신용 공개키(id_rsa.pub) 및 서버간 통신용 비밀키(id_rsa) - 본 키는 root용입니다. 
- Spring boot 1.5.14 기반 웹프로젝트 jar 및 소스코드 (kafka로 로그 출력 및 HTTP Request -> kafka 테스트용)
### 구동 방법
1. vagrant 2.11 및 Oracle virtualbox 5.2.x 설치
1. git clone https://github.com/gnu-gnu/kafka-virtual-machine.git 으로 프로젝트 내려 받기
1. Vagrantfile이 위치한 경로에서 vagrant up 으로 가상머신 프로비저닝 및 데몬 기동 스크립트 수행
1. /keys/private.ppk를 이용해 putty에서 vm1, vm2, vm3 접속
4-1. 가상머신들의 internal IP : vm1(192.168.33.11) vm2(192.168.33.12) vm3(192.168.33.13)
4-2. 가상머신들의 SSH 접속 IP 및 포트 : vm1(127.0.0.1:9122), vm2(127.0.0.1:9222), vm3(127.0.0.1:9322)
4-3. /keys 의 private.ppk를 이용해 4-2에 안내된 VM에 SSH 접속(ID는 vagrant)
4-4. 데몬 기동, 의존성 설치등의 작업을 루트 계정으로 수행시 <b>sudo su -</b> 로 계정 전환
4-5. kafka daemon은 kafka-server.service, zookeeper daemon은 /usr/local/zookeeper에 존재
4-6. kafka 수동 구동 : systemctl start kafka-server.service
4-7. zookeeper 수동 구동 : system start zookeeper-server.service
1. (머신내부) kafka 설치 경로 : /usr/local/kafka, zookeeper 설치 경로 : /usr/local/zookeeper
1. 프로젝트 디렉토리에서 java -jar kafka-test-web-server.jar 하여 테스트 웹서버 실행, 8080포트를 이용하는 웹서버
1. 구동되는 프로젝트는 log4j appender가 kafka로 연동되어 있음
1.  http://localhost:8080/producer/전할말 을 PUT메소드로 전송하면 kafka에 전달(POSTMAN 권장)
1. kafka 콘솔에서 카프카에 전달된 결과 확인 가능 
