# HM World 모바일 API 스켈레톤

## Swagger 사용법
- UI 접속: `http://localhost:8080/swagger-ui/index.html`
- API 스펙(JSON): `http://localhost:8080/v3/api-docs`
- 화면 좌측에서 엔드포인트를 선택하고 **Try it out**으로 바로 호출 테스트 가능
- 공통 베이스 URL은 `http://localhost:8080`, 모든 REST는 `/api` 하위에 위치

## 주요 엔드포인트
- 계정: `/api/accounts` (회원 CRUD, `/login`)
- 어트랙션: `/api/attractions`
- 상품: `/api/items`
- 주문: `/api/orders`, `/api/orders/{orderId}/details`, `/api/orders/user/{userId}`
- 리뷰: `/api/reviews/items/{itemId}`, `/api/reviews/attractions/{attId}`
- 줄서기: `/api/lines`, `/api/lines/attraction/{attId}`, `/api/lines/{lineId}/friends`
- 친구: `/api/friends`, `/api/friends/user/{userId}`

## DB 설정
- `src/main/resources/application.properties`에서 HMworld 스키마 접속 정보(`ssafy/ssafy`)를 환경에 맞게 수정하세요.
- 스키마 생성 스크립트: 루트의 `hm_world_db.txt`

## 빌드/실행
```bash
mvn clean package
mvn spring-boot:run
```
