# 자격증명 & 보안 모듈

## 안전 관리 규칙

1. **코드에 직접 작성 금지** — API 키·비밀번호는 반드시 n8n Credentials에 등록 후 노드에서 참조
2. **환경변수 활용** — n8n `.env`에 `N8N_ENCRYPTION_KEY` 설정 (32자 이상 랜덤)
3. **export 전 확인** — 워크플로우 JSON export 시 credentials 값은 포함되지 않음 (ID만 포함됨)
4. **최소 권한 원칙** — API 키 생성 시 필요한 scope만 부여
5. **Git 커밋 금지** — `.env`, `credentials.json`은 반드시 `.gitignore`에 추가
6. **90일 로테이션** — API 키 교체 후 n8n Credentials 업데이트

## 노드에서 credentials 연결 방법 안내 (사용자 가이드)

워크플로우 생성 후 사용자에게 아래를 안내한다:

1. n8n UI → Settings → Credentials → New Credential
2. 노드 클릭 → Credential 드롭다운에서 생성한 자격증명 선택
3. 저장 후 테스트 실행으로 연결 확인

> Claude는 실제 credentials 값(API 키 등)을 요청하거나 JSON에 포함시키지 않는다.
