# Project Proposal
## Team 15 '레전드사건발생'팀

----

### 0. **팀명 및 팀원**
   ### **팀 명**
   > 팀명: **'레전드사건발생'**
   - 팀명을 이렇게 작명한 이유는 두가지입니다 개발하려는 프로젝트가 충돌 및 낙하 사고를 대처하는 어플리케이션 개발이기 때문이고, 또한 저희 네명이 한 팀이 된것이 소위 말하는 **레전드사건**이기때문에 이렇게 작명하게 되었습니다. 항상 넷이 뭉쳐 놀러다니기에만 바빴는데, 그런 저희가 학술적인 목적으로 모여 어플리케이션을 개발한다는 것은 상상도 못했기 때문입니다.
   ### **팀 원**
   - 소프트웨어학부 2023203100 최 강
   - 소프트웨어학부 2023203070 김은현
   - 소프트웨어학부 2023203073 강동화
   - 소프트웨어학부 2023203098 주연우

### 1-1. **프로젝트 개요**

   - '교통사고 나자 '충돌 감지 기능'이 자동으로 119에 신고해... 아이폰 덕에 목숨 건진 여성' https://www.insight.co.kr/news/483177 
   - 위 기사는 아이폰의 충돌감지 기능으로 목숨을 건진 분의 사례입니다. 아이폰은 기본적으로 충돌감지기능이 있어 큰 충돌을 감지하고 사용자의 반응을 감지, 반응이 없을 경우 자동으로 신고하는 기능을 가지고 있습니다. 그러나 삼성 갤럭시 폰에서는 이 기능이 없으며, 최근 갤럭시s24와 갤럭시Z 폴드5에서 'Car Crash Detect'센서가 있는 것은 발견했으나, 현재 기능이 구현되어있지는 않습니다.
   - 따라서 갤럭시 스마트폰에서도 큰 충돌 및 낙하를 감지하고 자동으로 신고하는 기능을 가지는 어플을 구현하고자 합니다.
   
### 1-2. **프로젝트 목적**
   - 교통사고와 같은 큰 충돌을 감지하고 사용자의 반응을 감지하여 반응이 없을 경우 119에 자동으로 신고하는 기능의 어플리케이션을 개발
   - 강의에서 배웠던 객체지향언어의 상속과 캡슐화 등의 이론을 사용하여 어플리케이션을 제작
   - lecture13에서 배웠던 Java HTTP클라이언트를 사용하여 서버와의 통신을 구현
   - 강의에서 배웠던 git을 활용하여 원활한 협업을 진행

### 2. **주요 기능**
   - 가속도센서와 자이로센서, gps 등을 활용하여 낙하와 충돌을 감지
   - 충돌 감지 후 경고음, 메세지 출력
   - 어플이 출력한 경고음, 메세지에 대한 반응과 gps, 가속, 자이로센서의 데이터로 사용자의 반응, 움직임을 판단하여 사용자가 의식을 갖는지 판단 (예: 큰 사고후 의식의 유무 판단, 급정거 후 신호대기 등 오류가 발생할 수 있는 상황들을 판단)
   - 의식이 없음을 판단하면 비상연락망에 연락과 119 긴급신고 기능
   - 서버에서 회원가입, 정보수정, 비상연락망 등록,삭제기능과 gps데이터 관리
   - 설정창을 만들어 경고음, 메세지 출력, 센서 민감도 관리
   - 위젯 및 스마트워치 연동 개발

   >예시
<p align="center">
  <img src="img\LogIn.png" alt="시작 화면 이미지" width="15%"/>
  <img src="img\Main.png" alt="메인 화면 이미지" width="15%"/>
  <img src="img\ContactNumber.png" alt="비상 연락망 화면 이미지" width="15%"/>
  <img src="img\Message.png" alt="긴급 메시지 화면 이미지" width="15%"/>
  <img src="img\Settings.png" alt="환경 설정 화면 이미지" width="15%"/>
</p>

### 3. **역할 분담**

   >**2023203100 최 강**
   - 팀장, 프로젝트 기획, 일정관리, 충돌을 감지할 자이로센서와 가속도센서의 데이터 수집 및 처리 로직 구현, 여러 테스트 진행 및 오류검증, 프로토타입 시연
   >**2023203070 김은현**
   - 서버와 클라이언트 간 데이터 연동, 서버 수집 데이터 보안 및 접근 제어, 여러 테스트 진행 및 오류검증, 프로토타입 시연
   >**2023203073 강동화**
   - Firebase 설정 및 데이터베이스 구축, FCM (Firebase Cloud Messaging)을 통한 구조 요청 알림
   >**2023203098 주연우**
   - 어플리케이션의 UX/UI 디자인 및 기능 구현, 알림 및 구조 기능 설계(프론트 엔드), 어플리케이션 내 설정 기능 구현
   
   ##### *위젯 및 스마트워치용 어플 개발 역할 분담은 추후 결정*


### 4. **개발 일정 및  (11월 1주부터)**
>#### *1주차: 프로젝트 계획 및 설계 (11월 4일~8일)* 
주제선정, 역할분담 및 어플의 동작, 세부 기획 논의, 실현가능성 판단
프로젝트 요구사항 분석 및 초기 설계
센서 데이터 수집 방식 검토 및 연구
>#### *2주차: 어플 개발 시작 및 센서 구현, 서버 개발 시작 (11월 11일~15일)*
Android Studio를 통해 스마트폰 가속도센서와 자이로스코프 센서 데이터 수집 코드 작성
Firebase를 통한 안드로이드용 서버 개발 및 회원가입, 로그인 기능 부여
테스트 앱을 통해 센서 데이터 수집의 정확성 테스트
>#### *3주차: 충돌 감지 알고리즘 개발 및 서버 연동 및 데이터 전송 구현 (11월 18일~22일)*
가속도 및 자이로 데이터를 기반으로 충돌 감지 알고리즘 구현
초기 버전 알고리즘 테스트 및 수정
서버와의 HTTP 통신을 위한 Java HTTP 클라이언트 구현
서버에서 GPS 및 사용자 데이터를 관리할 수 있도록 통신 설정
>#### *4주차: 경고 알고리즘 개발 및 서버 보안 관리 (11월 25일~29일)*
경고음 및 메세지 출력 알고리즘 개발
서버 데이터 보안 관리
>#### *5주차: 사용자 인터페이스 및 UX 구현 (12월 2일~6일)*
사용자 인터페이스 및 UX 구현 및 신고 기능 구현
서버와 어플의 연계 기능 테스트 및 오류 수정
>#### *6주차: 기능 통합 및 위젯, 스마트워치용 어플 개발 (12월 9일~13일)*
앱의 주요 기능 통합 및 전체적인 테스트
위젯 및 스마트 워치 어플 개발
>#### *7주차: 최종 점검 및 발표 준비 (7주차 일정은 시험기간때문에 차주로 밀릴 수 있음)*
오류 검출 및 수정, 프로토타입 사용자 직접 테스트 후 최종 테스트 및 안정화
발표 자료 준비 및 발표 리허설

### 5. **사용 기술 및 도구**
   - 개발에 사용할 기술과 도구: 어플 개발언어는 java이고, Android Studio에서 xml과 함께 개발할 예정입니다. 서버개발은 Firebase를 사용할 예정이고, 어플 초기 디자인 기획에는 피그마(Figma)를 사용예정입니다. 
   - 객체지향 프로그래밍: 클래스와 객체를 이용하여 코드의 재사용성과 유지보수성을 높입니다. 상속과 캡슐화를 통해 센서 데이터 클래스 및 충돌 감지 클래스를 구현합니다.
   - Java HTTP 클라이언트: HttpURLConnection 또는 HttpClient를 사용하여 서버와의 데이터 송수신을 구현합니다.
   - 스레드 및 비동기 처리: 충돌 감지와 관련된 데이터 수집을 백그라운드 스레드에서 실행해 앱의 성능을 최적화합니다.
   - 센서 이벤트 리스너: SensorEventListener 인터페이스를 구현해 가속도계와 자이로스코프의 이벤트를 처리합니다.
   - 예외 처리: try-catch 블록을 통해 오류가 발생했을 때의 예외 상황을 처리하여 앱의 안정성을 높입니다.
   - Firebase: 로그인, 회원가입 등의 기능을 구현하고 FCM을 사용하여 알림 기능을 구현하고 Firebase 데이터베이스를 통해 데이터 저장 및 관리합니다.
   - UI 관련 클래스: AlertDialog나 Toast 등을 사용하여 경고 메세지와 사용자 피드백을 제공합니다.
   
### 6. **기대 효과 및 마무리**
   - 교통사고와 같은 큰 충돌을 감지하고 사용자의 반응을 감지하여 반응이 없을 경우 119에 자동으로 신고하는 기능의 어플리케이션을 개발한다
   - 강의에서 배웠던 객체지향언어의 이해와 개발 실습으로 객체지향언어를 더욱 깊이 이해한다
   - '팀플'에 가지고있었던 편견과 악감정을 깨끗이 씻어내고, 협업과 커뮤니케이션의 중요성을 경험한다
