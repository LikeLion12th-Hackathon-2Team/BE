## LikeLion12th-Hackathon-2Team BE 
## 오현우, 곽현민, 류동현


### 1주차 퀴즈

1. 유저 엔티티를 만드려고 하는데, 게체를 전부 private으로 만들어서 다른 파일에서 조회,수정이 되지 않는다. <br>이때 어떻게 해야 수정,조회가 가능할까?<br>(단 private은 유지해야한다.)

일반적으로는 setter 와 getter 메서드를 추가 해야된다 
(User.java 코드 안에)


2. 이제 로그인 API를 만드려고한다. <br>이때 controller, service, repository를 어떻게 짜야할까?<br>
   (controller이름은 userController,  사용할 service 이름은 userService, repository 이름은 userRepository이다.) <br>
   테스트까지는 할필요없고, 그냥 3개 파일에 코드만 짜면 된다. 오류나도 상관 없음

Domain:

데이터베이스 테이블과 매핑되는 엔티티 클래스를 정의합니다.
애플리케이션의 데이터 모델을 나타냅니다.

Controller:

HTTP 요청을 처리하고, 클라이언트와의 상호작용을 담당합니다.
요청을 서비스 계층으로 전달하고, 서비스 계층의 결과를 클라이언트에 반환합니다.

Service:

비즈니스 로직을 처리합니다.
트랜잭션 관리와 비즈니스 로직의 모듈화를 담당합니다.

Repository:

데이터베이스와 상호작용을 처리합니다.
데이터 접근 로직을 정의하고 캡슐화합니다

3. PutMapping과 PostMapping의 차이점을 설명하고, <br>각각 예시를 한개씩 들어주세요


POST, PUT 통신의 차이라고 볼 수 있다.
멱등성과 리소스 결정권이 누구에게 있는지에 따라 달라진다.

멱등성 : 어떤 대상에 같은 연산을 여러번 적용해도 결과가 달라지지 않는 성질
리소스 결정권 : URI를 결정하는 권리가 누구에게 있는지
- URI : 인터넷상의 리소스 자원 자체를 식별하는 고유한 문자열 시퀀스입니다


POST
- 리소스 결정권은 서버에게 있다(클라이언트는 리소스의 위치를 알 수 없고, 서버에서 이를 처리)

멱등성을 가지지 않아 메서드를 반복 수행할 때, 매번 데이터가 등록됨.
PUT
- 리소스 결정권은 클라이언트에게 있다. (변경 대상의 리소스 위치를 클라이언트가 알고 있음)
- 멱등성을 갖기에 메서드를 반복 수행할 때, 클라이언트가 받는 응답은 동일.

https://velog.io/@alswl5436/PostMapping-PutMapping-%EC%B0%A8%EC%9D%B4

참고 했습니다

4. build.gradle의 역할을 적어주시고,<br>
   만약  swaggerConfig에서 OpenApi부분을 읽어오지 못한다면, build.gradle에서 어느 부분을 못읽어왔는지 적어주세요

   
1) build.gradle -> 프로젝트 빌드 설정 , 의존성 관리 
개인적으로는 여기서 오류가 많이 발생 했던거 같다

2) swaggerConfig 에서 OpenApi 부분을 읽어 오지 못했다면
build.gradle 부분에 openapi 의존성 부분을 추가 해야된다


5. https와 http의 차이점을 적고,<br>
   이 프로젝트에서 https로 바꾸려고 도메인을 지정해주었다.<br>
   도메인을 추가해준 부분을 적어주고,<br>
   그러면 문제점이 로컬에서 테스트를 하려고해도 그 도메인으로 curl을 날려서 테스트를 할수가 없다.
   <br>이때 두줄만 추가하면, 로컬에서도 선택해서 curl를 날릴수가있는데 어디에 어떻게 추가해야할까??

   1) HTTPS는 암호화 및 인증이 있는 HTTP입니다. 두 프로토콜의 유일한 차이점은 HTTPS는 TLS(SSL)를 사용하여 일반 HTTP 요청과 응답을 암호화하고 해당 요청과 응답에 디지털 서명을 한다는 점입니다. 그 결과로 HTTPS는 HTTP보다 훨씬 더 안전합니다.

   2) application.yml 추가하기

6. aws 서버에 자동배포를 하고있는데, 이 기능을 할수있게 하는 파일은 무엇일까??

github actions , S3 ec2 이런거로 하는거 아닐까 하는데 
클라우드 부분에는 공부가 좀 필요 한거 같다
   

7. 도메인을 지정해주었고, swagger로 접속을 하려면 url이 어떻게 될까? <br>(프로젝트 내에서 써져있는 도메인을 찾아 url을 완성해주세요)

swagger config 에 써있다
도메인: https://hyunwoo9930.shop
Swagger UI URL: https://hyunwoo9930.shop/swagger-ui/index.html

8. ./gradlew build를 하면 build/lib/ 파일안에 두가지 버젼의 jar 파일이 존재한다. 이때, plain은 쓸모가 없어서 사용을 안하는데, 이 plain 파일을 없애려면 어떻게 해야할까??

GPT 참조 했습니다 

build.gradle 파일을 열고 tasks.named('jar') { enabled = false } 설정을 추가합니다.
./gradlew build를 실행하면 "plain" JAR 파일은 생성되지 않고 "boot" JAR 파일만 생성됩니다.

   
