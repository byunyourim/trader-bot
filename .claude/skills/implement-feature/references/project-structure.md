# 프로젝트 구조

## 아키텍처

같은 비즈니스 로직을 3개 소비자가 공유하는 구조:

```
src/types/ · src/parsers/ · src/core/   ← 순수 로직 (외부 의존성 없음)
        │
        ├── adapters/mcp/    Claude Code stdio (MCP 서버)
        ├── adapters/http/   n8n HTTP Request (REST, port 3001)
        └── client/          외부 API 래퍼 (n8n API, RPC, GitHub)
```

## 핵심 모듈

| 경로 | 역할 |
|------|------|
| `src/types/` | `Incident`, `Deployment`, `ParsedError`, `SupportedChain` 등 공유 도메인 타입 |
| `src/core/chain.ts` | chainId ↔ 이름 변환, Explorer/RPC URL 생성 |
| `src/parsers/slack-error.ts` | Slack 에러 메시지 → `ParsedError` 파싱 |
| `src/core/ai.ts` | Anthropic/Gemini API 추상화 + 에러 분석 시스템 프롬프트 |
| `adapters/mcp/index.ts` | MCP 도구 5개 (`parse_slack_error`, `chain_name`, `tx_explorer_url`, `address_explorer_url`, `rpc_url`) |
| `adapters/http/` | 동일 도구 REST 노출. `API_KEY` 환경변수 인증, `GITHUB_TOKEN`으로 `/github/search` 활성화 |
| `client/n8n.ts` | n8n API 워크플로우 목록·조회·활성화·실행이력 |

## 지원 체인

| 이름 | chainId |
|------|---------|
| Sepolia | 11155111 |
| Fuji | 43113 |
| KCP | 56357 |

## 외부 패키지로 사용 시

```json
"dependencies": { "@stablecoin/ops": "file:../StableCoin_OPS" }
```

```ts
import { parseSlackError } from '@stablecoin/ops/parsers';
import { explorerTxUrl } from '@stablecoin/ops';
import type { Incident } from '@stablecoin/ops/types';
```

## 수정 시 주의

- `adapters/mcp/index.ts` 수정 시 `tools` 배열과 `switch` 블록 **두 곳 모두** 수정
- `src/types/`의 타입 변경 시 세 소비자(mcp, http, client) 모두 영향 확인
- `n8n 워크플로우 수정 후 `workflows/` 폴더에 JSON export 커밋 (버전 관리)
