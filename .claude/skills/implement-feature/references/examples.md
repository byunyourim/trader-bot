# implement-feature 예시

## 예시 1: CSV export 버튼

요청:
"users table에 CSV export 버튼 추가해줘."

좋은 흐름:
1. 기존 export/download 패턴 검색
2. users table component 확인
3. CSV 생성 로직이 이미 있는지 확인
4. 기존 패턴으로 버튼 추가
5. CSV 로직이 있다면 focused test 추가
6. typecheck 또는 관련 test 실행

나쁜 흐름:
- 새 CSV 라이브러리부터 추가
- users table 전체 rewrite
- 관련 없는 table까지 수정

## 예시 2: 사용자 확인이 필요한 기능

요청:
"admin delete user 기능 추가해줘."

먼저 질문:
- soft delete인가 hard delete인가?
- 누가 삭제할 수 있는가?
- audit log가 필요한가?
- 관련 데이터는 어떻게 처리하는가?

이건 데이터 손실과 권한 이슈가 있으므로 확인 전 구현하지 않는다.

## 예시 3: 합리적 가정 가능

요청:
"billing page에 loading state 추가해줘."

가능한 진행:
- 기존 skeleton/loading component를 찾는다.
- billing page가 하나면 그대로 진행한다.
- 결과 요약에 "기존 loading 패턴을 재사용했다"고 적는다.
