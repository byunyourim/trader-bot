# 디버깅 & 에러 처리 모듈

## 에러 처리 패턴

### 1. 노드 레벨 — continueOnFail
```json
{ "onError": "continueRegularOutput" }
```
해당 노드 설정에 추가. 실패 시 다음 노드로 계속 진행.

### 2. 워크플로우 레벨 — Error Workflow
`settings.errorWorkflow`에 에러 알림 워크플로우 ID 설정.
에러 워크플로우에서 `$json.execution.error.message`로 메시지 접근.

### 3. IF 노드로 응답 코드 분기
```
HTTP Request → IF (statusCode >= 400) → [true] 에러 처리 / [false] 정상 처리
```

## 자주 발생하는 에러

| 에러 | 원인 | 해결 |
|------|------|------|
| `Cannot read property 'x' of undefined` | `$json.field` null | `$json.field ?? default` 사용 |
| `Node does not exist` | connections 노드명 불일치 | 노드 `name`과 connections 키 동기화 |
| `Credential not found` | 자격증명 미연결 | 노드 credentials 필드에 ID 매핑 |
| `Workflow timed out` | 실행 시간 초과 | 배치 분할 또는 비동기 처리 전환 |
| `Expression error` | 표현식 문법 오류 | `={{ }}` 내부 JS 문법 검사 |

## 디버깅 체크리스트

- [ ] 표현식 미리보기 패널에서 실제 값 확인
- [ ] "Execute Node"로 노드 단독 실행해 입출력 확인
- [ ] Code 노드: `console.log(JSON.stringify(items, null, 2))` 로 구조 출력
- [ ] HTTP Request 실패: `Full Response` 옵션 활성화해 상태 코드 확인
- [ ] Webhook 테스트 시 Production URL 대신 Test URL 사용
- [ ] 실패한 워크플로우는 `n8n_executions`로 실행 이력 조회 후 에러 메시지 분석
