# 프로젝트 정보
Puzzly Back End 
---
## 개발환경
- 프로그램 언어: Java 17
- 프레임워크: Spring boot 3.2
- 라이브러리
  - jpa
  - mybatis
  - web
  - security
  - lombok
  - springdoc (swagger)
  - jjwt
  - objectmapper
  - h2 (local)
  - mariadb (in dev, oper , as planning)

---
## database structure
** 이준훈에게 파일을 요청해주시거나 혹은 notion을 제안해주세요
![Puzzly_DBSTRC_FIN](https://github.com/Puzzly/Puzzly-Back/assets/48429012/4b613f7b-8897-4c5e-b938-d7832ccf2032)

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


<!--
## 패키지 구조


📂com  
┗ 📂puzzly  
  ┣ 📂 api
    ┣ 📂 controller
      ┗ auth, user ..etc controller
    ┣ 📂 coreComponent
      ┣ 📂 securityCore
        ┗ springSecurity@Configuration , UserDetails를 제외한 security 관련 .java
      ┗ ApplicationListenerService, GlobalExceptionHandler .java
    ┣ 📂 domain
      ┗ DB에 저장되지 않을 객체 및 enum .java
    ┣ 📂 dto
      ┗ 📂 Request DTO (package) 
      ┗ 📂 Response DTO (package)
    ┣ 📂 entity
      ┗ DB 객체 .java
    ┣ 📂 enums
      ┗ enum 최상위객체, typeHandler .java
    ┣ 📂 exception
      ┗ Custom Exception
    ┣ 📂 repository
      ┗ 📂 jpa repository (package) interfaces
      ┗ 📂 mybatis repository (package) interfaces
    ┣ 📂 service
      ┗ auth, user ..etc service
    ┗ 📂 util
      ┗ jwtUtil, Global Util ..etc
  ┣ 📂 configuration 
    ┣ configs.java (Swagger, Security, ObjectMapper, H2Server, BcryptPassword, Database (Database Configuration is temporary @Deprecated) 
    -->
