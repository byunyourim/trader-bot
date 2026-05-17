# code-explainer 예시

## 좋은 설명

요청:
"이 로그인 흐름 설명해줘."

좋은 출력:
- `app/login/page.tsx`에서 form submit
- `actions/login.ts`가 credentials 검증
- `lib/auth/session.ts`가 session cookie 생성
- 실패 시 `LoginError`를 UI에서 표시

나쁜 출력:
- Next.js 전체 설명
- auth 개념 일반론
- 관련 없는 dashboard 구조 설명

## 수정 위치 추천 예시

요청:
"로그인 실패 메시지를 바꾸려면 어디 봐야 해?"

좋음:
- UI 문구는 `components/login-form.tsx`
- error mapping은 `lib/auth/errors.ts`
- 서버 validation은 `actions/login.ts`
