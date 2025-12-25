# 🎡 Hoonmin World

오프라인 테마파크(놀이공원)의 예약·티켓·상품 구매를  
모바일 앱과 백엔드 서버로 통합한 **O2O 플랫폼 프로젝트**입니다.

---

## 📌 프로젝트 개요 (Overview)

Hoonmin World는  
놀이공원 이용 과정에서 발생하는 **예약, 대기, 티켓, 상품 구매**를  
모바일 앱 하나로 통합 관리할 수 있도록 설계된 서비스입니다.

사용자는 앱을 통해 놀이기구 예약 현황을 확인하고,  
티켓 및 상품을 구매하며, 개인 이용 내역을 한눈에 확인할 수 있습니다.

---

## 🎯 프로젝트 배경 및 목적

### 배경
- 기존 놀이공원은 오프라인 중심 운영으로 정보 접근성이 낮음
- 긴 대기시간으로 놀이공원에서 즐기는 시간보다 줄서는 시간이 길어짐
- 사람이 많은 기념품 가게에서 물건을 사기가 어려움

### 목적
- 모바일 앱 기반의 **디지털 고객 여정 구축**
- 놀이공원 이용 전 과정의 통합 관리
- 사용자 경험(UX) 개선 및 운영 효율성 향상
- 온라인 줄서기로 고객 만족 향상

---

## ⭐ 주요 기능 (Features)

### 🎟️ 티켓 / 예약
- 놀이기구 예약 및 예약 가능 여부 확인
- 사용자별 티켓 구매 상태 관리
- 놀이기구 수용 인원 및 이용 가능 여부 반영

### 🛍️ 상품 구매
- 놀이공원 내 상품 조회
- 상품 구매 및 재고 수량 관리
- 온라인 구매 및 오프라인 수령
- 기록 조회, 리뷰, 재구매

### 🏠 홈
- 공지사항 및 배너 노출
- 현재 예상 대기시간 노출

### 👤 마이페이지
- 사용자 정보 조회
- 친구 목록 및 티켓 구매 친구 확인

### 🤖 AI 추천 
- 선호도에 따른 어트랙션, 상품 추천 - 온디바이스 ai
- 이미지 분석으로 카테고리 추천 - chat gpt api

---

## 🔄 서비스 흐름 (User Flow)

### 🚪 Step 1. 회원가입 · 로그인  
> 앱 최초 진입 후 계정 생성 및 로그인 진행

<p align="center">
  <img src="Screenshot_20251225_204626.png" width="30%" />
  <img src="Screenshot_20251225_204721.png" width="30%" />
  <img src="Screenshot_20251225_204731.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_204802.png" width="30%" />
  <img src="Screenshot_20251225_204809.png" width="30%" />
</p>

### 🏠 Step 2. 홈 화면 · 공지 & AI 추천  
> 공지사항 확인 및 AI 기반 맞춤 추천 정보 제공

<p align="center">
  <img src="Screenshot_20251225_204854.png" width="30%" />
  <img src="Screenshot_20251225_204906.png" width="30%" />
</p>
<p align="center">
<img src="Screenshot_20251225_204918.png" width="30%" />
  <img src="Screenshot_20251225_204926.png" width="30%" />
</p>

### 🎢 Step 3. 놀이기구 예약 & 티켓 구매  
> 실시간 예약 현황 확인 및 온라인 줄서기 기능 제공
<p align="center">
  <img src="Screenshot_20251225_205315.png" width="30%" />
  <img src="Screenshot_20251225_205334.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_205033.png" width="30%" />
  <img src="Screenshot_20251225_205050.png" width="30%" />
  <img src="Screenshot_20251225_205108.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_205258.png" width="30%" />
  <img src="Screenshot_20251225_205345.png" width="30%" />
</p>

### 🛍️ Step 4. 매장 상품 구매  
> 온라인 구매 후 오프라인 수령이 가능한 상품 구매 기능
<p align="center">
  <img src="Screenshot_20251225_205910.png" width="30%" />
  <img src="Screenshot_20251225_205940.png" width="30%" />
  <img src="Screenshot_20251225_205955.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_210005.png" width="30%" />
  <img src="Screenshot_20251225_210019.png" width="30%" />
  <img src="Screenshot_20251225_210029.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_210056.png" width="30%" />
  <img src="Screenshot_20251225_210106.png" width="30%" />
  <img src="Screenshot_20251225_210156.png" width="30%" />
</p>
<p align="center">
  <img src="Screenshot_20251225_210203.png" width="30%" />
  <img src="Screenshot_20251225_210208.png" width="30%" />
  <img src="Screenshot_20251225_210219.png" width="30%" />
</p>

### 🧠 Step 5. AI 이미지 분석 추천  
> 이미지 분석을 통한 상품 및 카테고리 자동 추천
<p align="center">
  <img src="Screenshot_20251225_210236.png" width="30%" />
  <img src="Screenshot_20251225_210259.png" width="30%" />
  <img src="Screenshot_20251225_210339.png" width="30%" />
</p>

### 📡 Step 6. NFC 기반 입장 확인 & 앱 실행  
> NFC 태그를 통한 입장 인증 및 앱 자동 실행
<p align="center">
  <img src="KakaoTalk_20251225_211649700.jpg" width="30%" />
  <img src="KakaoTalk_20251225_211649700_01.jpg" width="30%" />
  <img src="KakaoTalk_20251225_211649700_02.jpg" width="30%" />
</p>

### 🔔 Step 7. FCM 탑승 시간 안내 · 공지사항 알림  
> 탑승 시간 알림 및 공지사항 실시간 전달
<p align="center">
  <img src="Screenshot_20251225_205618.png" width="30%" />
  <img src="KakaoTalk_20251225_211649700_07.jpg" width="30%" />
  <img src="Screenshot_20251225_210644.png" width="30%" />
</p>
<p align="center">
  <img src="KakaoTalk_20251225_211649700_09.jpg" width="30%" />
  <img src="KakaoTalk_20251225_211649700_14.jpg" width="30%" />
  <img src="Screenshot_20251225_211012.png" width="30%" />
</p>

---

## 🛠️ 기술 스택 (Tech Stack)

### Frontend
- Android
- Kotlin
- Jetpack Compose

### Backend
- Spring Boot
- MyBatis
- MySQL

### 기타
- REST API
- Swagger UI (API 문서화)
- MVC / Layered Architecture

---

## 🧱 시스템 아키텍처 (Architecture)

[ Android App ]
↓
[ Spring Boot REST API ]
↓
[ MySQL Database ], [ firebase ], [ chat gpt]


- 모바일 앱과 서버 간 REST 통신
- 서버에서 비즈니스 로직 및 DB 처리

---

## 🗃️ DB 설계 (Database)

### 주요 테이블
- `account` : 사용자 정보
- `attraction` : 놀이기구 정보
- `item` : 상품 정보
- `ticket` : 티켓 구매 정보
- `reservation` : 예약 정보
- `review` : 이용 후기
![alt text](image.png)
(ERD 기반으로 설계)

---

## ▶️ 실행 방법 (How to Run)

### Backend 실행
1. MySQL 데이터베이스 생성
2. `application.yml` 또는 `application.properties` DB 설정
3. Spring Boot 프로젝트 실행

### Frontend 실행
1. Android Studio 실행
2. 에뮬레이터 또는 실기기 연결
3. 앱 실행

---

## 📈 결과 및 성과

- Android 앱과 Spring Boot 서버 간 안정적인 REST API 연동
- 놀이공원 예약·티켓·상품 구매 통합 서비스 구현
- DB 설계 및 API 구조에 대한 이해도 향상
- Ai를 통한 다양한 기능 구성

---

## ⚠️ 한계 및 개선 사항

- 하나의 앱으로 관리자, 고객 기능 구현 -> 분리해서 보안 유지 필요.
- 결제 기능과 Google, Naver 로그인 연동 미구현
- 향후 실제 맵 로드를 통해 편의성 제공

---

## 👥 팀원 및 역할

- 한민지 : 서비스 흐름 설계, DB 설계, Android UI 및 Jetpack Compose, 줄서기 알고리즘, 고객 페이지, chat gpt 연동, DB 연동, nfc 기능
- 성경훈 : Rest API 설계, Android UI 및 Jetpack Compose, 관리자 페이지, onDevice ai, 지도 api, fcm 기능

---

## ✍️ 마무리

본 프로젝트는  
모바일 앱과 백엔드 서버를 연계한 **실전형 O2O 서비스 구조**를 이해하고  
직접 구현하는 것을 목표로 진행되었습니다.

팀원과 관심있는 분야를 고민하며 만든 프로젝트로 재미있게 진행했습니다.
팀원 모두 0부터 100까지 처음으로 만들어본 프로젝트이지만 완성도 높은 프로젝트라고 생각합니다.
향후 프로젝트에서도 자신감을 가지고 진행하도록 하겠습니다.
