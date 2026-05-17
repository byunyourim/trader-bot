# 노드 & 표현식 참조 모듈

## 자주 쓰는 노드 파라미터 패턴

### Webhook
```json
{ "httpMethod": "POST", "path": "my-hook", "responseMode": "onReceived" }
```
- Production URL: `https://<host>/webhook/<path>`, Test URL: `/webhook-test/<path>`

### Schedule (Cron)
```json
{ "rule": { "interval": [{ "field": "cronExpression", "expression": "0 9 * * 1-5" }] } }
```
- 크론 표현식 5자리 (초 없음)

### HTTP Request
```json
{
  "method": "POST",
  "url": "https://api.example.com/data",
  "authentication": "genericCredentialType",
  "genericAuthType": "httpHeaderAuth",
  "sendBody": true,
  "bodyParameters": { "parameters": [{ "name": "key", "value": "={{ $json.value }}" }] }
}
```

### Code
```json
{
  "mode": "runOnceForAllItems",
  "jsCode": "return items.map(item => ({ json: { result: item.json.value * 2 } }));"
}
```
- `mode`: `runOnceForAllItems`(배치) / `runOnceForEachItem`(개별)
- 반드시 `[{ json: {...} }]` 배열 return

### IF (조건 분기)
```json
{
  "conditions": {
    "conditions": [{
      "leftValue": "={{ $json.status }}",
      "rightValue": "success",
      "operator": { "type": "string", "operation": "equals" }
    }]
  },
  "combinator": "and"
}
```
- `true` → `main[0]`, `false` → `main[1]`

### Set
```json
{
  "mode": "manual",
  "fields": { "values": [{ "name": "userId", "type": "string", "stringValue": "={{ $json.id }}" }] },
  "include": "none"
}
```
- `include: "none"` = 지정 필드만, `"all"` = 기존 필드 유지 후 추가

## 표현식 치트시트

| 표현식 | 용도 |
|--------|------|
| `$json.field` | 현재 아이템 필드 |
| `$json.field ?? 'default'` | null 안전 접근 |
| `$node["이름"].json.field` | 특정 노드 출력 참조 |
| `$items("이름").length` | 특정 노드 아이템 수 |
| `$env.VAR_NAME` | 환경변수 |
| `$now.toISO()` | 현재 시각 (ISO 8601) |
| `$today.toFormat('yyyy-MM-dd')` | 오늘 날짜 |
| `$execution.id` | 현재 실행 ID |
| `$workflow.id` | 현재 워크플로우 ID |
| `$runIndex` | 루프 반복 인덱스 (0-based) |

> `$json`은 현재 아이템 기준. 이전 노드 전체 참조는 `$node["이름"].json` 사용.
