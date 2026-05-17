# debug-root-cause 예시

## 좋은 분석

에러:
`Cannot read properties of undefined (reading 'id')`

좋은 흐름:
1. stack trace의 컴포넌트 확인
2. `user`가 언제 undefined일 수 있는지 확인
3. loading/auth state 확인
4. null guard 또는 loading state 제안

나쁜 흐름:
- 전체 컴포넌트 rewrite
- 상태관리 라이브러리 교체 제안
- 근거 없이 API 문제라고 단정

## 빌드 실패

좋음:
- 실패 명령 확인
- 타입 에러 파일 확인
- 최근 타입 변경 확인
- 최소 타입 수정 제안
