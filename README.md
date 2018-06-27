# kafka-virtual-machine
kafka virtual machine

카프카3노드 주키퍼3앙상블이 설치된 가상머신입니다.

virtualbox와 vagrant 2.x가 필요합니다.

브랜치 pull후 디렉토리에서 vagrant up으로 머신을 프로비저닝한 후

각 머신의 ssh는 key파일에 첨부된 ppk를 pageant혹은 putty에 등록하여 접속합니다.

머신 구동 후 각 머신에서 sudo systemctl start kafka-server.service가 머신 구동 후 실행 필요합니다
