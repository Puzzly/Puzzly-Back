# 프로젝트 정보
Puzzly Back End 
---
## 개발환경
- 프로그램 언어: Java 17
- 프레임워크: Spring boot
- 라이브러리
  - jpa
  - web
  - security
  - lombok
  - swagger
  - springdoc
  - jjwt
  - jackson
  - modelmapper

## 패키지 구조


📂com  
┗ 📂puzzly  
  ┣ 📂**  
  ┃ ┗ 📜**.java  
  ┗ 📂api  
    ┗ 📂구분명  
      ┣ 📂domain  
      ┃ ┗ 📜** Entity.java  
      ┃ ┗ 📜** Request(Req).java  
      ┃ ┗ 📜** Response(Res).java  
      ┣ 📂controller  
      ┃ ┗ 📜** Controller.java  
      ┣ 📂service  
      ┃ ┗ 📜** Service.java  
      ┗ 📂repository  
        ┗ 📜** Repository.java