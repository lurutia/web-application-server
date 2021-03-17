# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* BufferedReader 에 대해서 알게되었다.
* Files.readAllBytes 에 대해서 알게되었다.
* FileRead Test를 어떻게 해야할지 궁금하다 결과는 가변적 byte array인데...

### 요구사항 2 - get 방식으로 회원가입
* indexOf를 이용해 ?를 기준으로 path와 queryString을 나누었음

### 요구사항 3 - post 방식으로 회원가입
* Content-Length를 얻어오는 부분을 잘 모르겠음 header를 line단위로 읽을때마다 체크해줘야하나?
* 예제에서는 post 방식의 body에서도 queryString처럼 줬지만 일반적으로는 어떻게 오는걸까 궁금함
* GET방식이냐 POST방식이냐 처리를 위한 if문이 갈수록 복잡도가 늘어나고있음

### 요구사항 4 - redirect 방식으로 이동
* response 에서 302 status에 location을 주면 해당 url로 redirection 된다는것을 알게됨
* 각각 상황에 맞게 header를 만들어줘야 하는데 리팩토링이 중요할듯

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 