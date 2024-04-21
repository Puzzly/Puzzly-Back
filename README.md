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
  - mariadb (in plan)

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
