# 프로젝트 정보
Puzzly Back End 
---
## 개발환경
- 프로그램 언어: Java 17
- 프레임워크: Spring boot 3.2
- 라이브러리
  - jpa / mybatis
  - Springboot-web
  - Springboot-security
  - Springdoc-api
  - lombok
  - jjwt
  - h2 (local) / mariadb (dev/deploy)

---
## Mybatis, JPA 사용 기준

### JPA
- 단순 C, U, D
- 단순 Read ( FrontEnd로 리턴할 필요 없이 단순 존재여부 확인, 단순 데이터 조회 후 해당 데이터로 서비스 로직을 굴릴경우)
- 단순 C 후 FE에 결과값을 리턴하려 하는 경우

### Mybatis
- 통계성 CRUDL
- 스케쥴로 제어하는 CRUDL
- 단순 C 후 FE에 결과 리턴을 제외한 쿼리의 결과값을 FrontEnd로 내릴 목적이 있는 객체, FrontEnd로 내리기 전에 추가적인 조작이 필요한 경우

---
## git branch 관리

- 개발 : develop 브랜치에서 새 브랜치 생성하여 작업
- PR : 새 브랜치에서 작업 종료되면 해당 브랜치로 remote push, 새브랜치 -> develop 브랜치 PR
- Deploy : develop -> main PR, release note 작성

- 업무 종류 : feature(기능개발), chore(기능에 영향을 미치지 않는 잡일, 예시: md update), bugfix(버그수정), refactor(로직은 변하지 않으나 코드 수정이 발생한 리펙터링) 

## git.release 버전 기준

- Major.Minor.patch

- Major ↑ : N차 MVP 기능 개발이 패치 될 경우
- Minor ↑ : 신규 API가 생성되었을 경우, API 리턴값이 변경될 경우 (Controller의 URL, Parameter, Return 형태 변경) (Major 업데이트 될 경우 초기화)
- patch ↑ : 이외의 모든 상황 (디버깅패치, chore패치, etc..) (Minor 업데이트 될 경우 초기화)

--- 
## response 기준

- 정상 리턴 (SUCCESS)
  ```
  {
    "status": 200,
    "message": "SUCCESS",
    "timestamp": "2024-05-10 01:19:15",
    "result": {
      {{결과값}}
    }
  }
  // result 내부에 단일 객체면 변수명 그대로 (예 : user: {})
  // result 내부에 리스트 형태가 있으면 변수명에 List 붙여서 리턴 (예 : userList: [{},{},{}])
  ```
- 실패 리턴 (Fail)
  ```
  {
    "status": 400,
    "timestamp": "2024-05-10 01:10:30",
    "message": "SERVER_MESSAGE_USER_INFO_NOT_FOUND"
    // message가 SERVER_MESSAGE_* 이면 의도된 예외처리, FE로 오류메시지를 출력해주길 바라는 상황 (위 예시 : ID/PW가 틀렸습니다?)
    // SERVER_MESSAGE_* 가 아닐경우 내부에서 발생한 오류. 발생하는 케이스를 확인해서 고치거나 SERVER_MESSAGE_* 형태로 변경해야함
  }
  ```
