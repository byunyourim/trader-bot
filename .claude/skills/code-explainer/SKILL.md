---
name: code-explainer
description: 코드베이스, 특정 파일, 함수, 모듈, 요청 흐름을 설명할 때 사용한다. 전체 repo를 요약하지 않고 현재 질문에 필요한 코드 흐름만 설명한다.
when_to_use: 코드 구조 설명, 특정 파일/함수 이해, 어디를 수정해야 하는지 파악, 온보딩이 필요할 때
version: 1.0.0
---

# code-explainer

목표: 질문 해결에 필요한 코드 흐름만 빠르게 설명한다.

## 워크플로우

```txt
1. 질문 범위 확인
2. 관련 파일만 탐색
3. 실행/데이터 흐름 파악
4. 핵심 경로 설명
5. 수정 후보와 주의점 제시
```

## 규칙

- 전체 repo 설명을 기본으로 하지 않는다.
- 파일 경로와 함수명을 구체적으로 말한다.
- 추측은 추측이라고 표시한다.
- 관련 없는 내부 구현을 길게 설명하지 않는다.
- 사용자가 초보자 설명을 원하지 않으면 과도한 비유를 쓰지 않는다.

## 탐색 순서

1. entrypoint
2. 호출되는 service/domain logic
3. type/schema/contract
4. 비슷한 기능
5. 테스트

## 출력 형식

```md
## 한 줄 요약
## 핵심 파일
## 동작 흐름
## 수정이 필요할 가능성이 높은 위치
## 주의점
```

## 참고 문서

필요할 때만 읽는다.

- `.claude/skills/implement-feature/references/project-structure.md` — 전체 아키텍처·모듈·체인 정보
- `references/routing.md` — 요청 유형별 탐색 순서
- `references/examples.md` — 설명 예시
