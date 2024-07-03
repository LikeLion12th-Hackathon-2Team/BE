## LikeLion12th-Hackathon-2Team BE 
## 오현우, 곽현민, 류동현


### 1주차 퀴즈

1. 유저 엔티티를 만드려고 하는데, 게체를 전부 private으로 만들어서 다른 파일에서 조회,수정이 되지 않는다. <br>이때 어떻게 해야 수정,조회가 가능할까?<br>(단 private은 유지해야한다.)
   <br><br>  답) user entity에 @Getter, @Setter를 선언해주면 자동으로 엔티티 내의 모든 필드에 대해 Getter, Setter 메서드를 생성해준다. 그러면 외부에서 필드 값에 대해 수정 및 조회가 가능해진다.<br>
2. 이제 로그인 API를 만드려고한다. <br>이때 controller, service, repository를 어떻게 짜야할까?<br>
   (controller이름은 userController,  사용할 service 이름은 userService, repository 이름은 userRepository이다.) <br>
   테스트까지는 할필요없고, 그냥 3개 파일에 코드만 짜면 된다. 오류나도 상관 없음

3. PutMapping과 PostMapping의 차이점을 설명하고, <br>각각 예시를 한개씩 들어주세요
   <br><br> PutMapping의 경우 반복 호출 시에도 동일한 응답을 준다.(특정 사용자의 필드?값을 변경해야 하는 경우, Update의 기능)<br> PostMapping의 경우 반복 호출 시 동일 응답 X. (사용자를 계속 생성해야하는 경우, Create 기능)
   <br>
4. build.gradle의 역할을 적어주시고,<br>
   만약  swaggerConfig에서 OpenApi부분을 읽어오지 못한다면, build.gradle에서 어느 부분을 못읽어왔는지 적어주세요

   <br><br>
   의존성 관리 및 프로젝트 build에 필요한 기능들을 정리
   implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2' 를 읽지 못하면 OpenApi 라이브러리를 사용할 수없음
   <br>
5. https와 http의 차이점을 적고,<br>
   이 프로젝트에서 https로 바꾸려고 도메인을 지정해주었다.<br>
   도메인을 추가해준 부분을 적어주고,<br>
   그러면 문제점이 로컬에서 테스트를 하려고해도 그 도메인으로 curl을 날려서 테스트를 할수가 없다.
   <br>이때 두줄만 추가하면, 로컬에서도 선택해서 curl를 날릴수가있는데 어디에 어떻게 추가해야할까?? 

   <br><br>
   HTTP의 경우 요청과 응답을 모든 사람들이 볼 수 있어 보안에 취약하다. 따라서 HTTPS는 TLS를 사용하여 HTTP 요청과 응답을 암호화.
   
   <br>

6. aws 서버에 자동배포를 하고있는데, 이 기능을 할수있게 하는 파일은 무엇일까??
   <br><br>
   .github/workflows/cicd.yml
   <br>
7. 도메인을 지정해주었고, swagger로 접속을 하려면 url이 어떻게 될까? <br>(프로젝트 내에서 써져있는 도메인을 찾아 url을 완성해주세요)
   <br><br>
   
   <br>
8. ./gradlew build를 하면 build/lib/ 파일안에 두가지 버젼의 jar 파일이 존재한다. 이때, plain은 쓸모가 없어서 사용을 안하는데, 이 plain 파일을 없애려면 어떻게 해야할까??
   <br><br>
   rm build/libs/plain.jar 코드를 이용해서 파일 삭제?
   <br>