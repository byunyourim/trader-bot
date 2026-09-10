# ADR 의사결정 기록 시스템 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:subagent-driven-development 또는 superpowers:executing-plans 로 task 단위 실행. 체크박스(`- [ ]`)로 진행 추적.

**Goal:** `/adr` 슬래시 커맨드 하나로, 대화에서 나온 설계 결정을 일관된 ADR 문서로 남기는 시스템 구축.

**Architecture:** `docs/adr/` 아래 영역별 폴더 + 날짜 프리픽스 파일명. 템플릿을 고정 계약으로 두고, `/adr` 커맨드가 규칙·검증을 모두 담아 초안 작성 및 README 인덱스 갱신. 기존 결정 변경은 immutable + supersede.

**Tech Stack:** Markdown, Claude Code 슬래시 커맨드(`.claude/commands/`).

**참고:** 이 프로젝트는 git commit을 사용자가 직접 수행한다. 각 Task 끝의 커밋은 사용자 몫이므로 실행자는 커밋하지 말고 "커밋 지점"만 알린다.

---

### Task 1: ADR 디렉토리 스캐폴딩

**Files:**
- Create: `docs/adr/market/.gitkeep`
- Create: `docs/adr/order/.gitkeep`
- Create: `docs/adr/account/.gitkeep`
- Create: `docs/adr/infra/.gitkeep`
- Create: `docs/adr/arch/.gitkeep`

- [ ] **Step 1: 5개 영역 폴더와 `.gitkeep` 생성**

각 파일 내용은 빈 파일. 폴더가 git에 잡히도록 `.gitkeep`만 둔다.

- [ ] **Step 2: 검증**

Run: `ls docs/adr` (PowerShell: `ls docs/adr`)
Expected: `market order account infra arch` 5개 폴더 존재.

- [ ] **Step 3: 커밋 지점 알림** (사용자가 커밋)

제안 메시지: `chore: ADR 디렉토리 구조 추가`

---

### Task 2: ADR 템플릿 작성

**Files:**
- Create: `docs/adr/template.md`

- [ ] **Step 1: 템플릿 파일 작성**

아래 내용 그대로 작성한다.

````markdown
---
status: Proposed        # Proposed | Accepted | Superseded  (이 3개만)
date: YYYY-MM-DD
area: order              # market | order | account | infra | arch  (이 5개만)
supersedes:              # (선택) 이 결정이 대체하는 이전 ADR 경로
superseded-by:           # (선택) 이 결정을 대체한 ADR 경로
---

# <결정 제목: 동사형 한 문장, 예: 주문 멱등성을 requestId로 보장한다>

## Context
<배경·제약. 무엇을 풀려는가, 어떤 조건이 있었나.>

## Options
1. **<옵션 A>** — <트레이드오프>
2. **<옵션 B>** — <트레이드오프>

## Decision
<선택한 옵션과 근거. 다른 걸 안 고른 이유 포함.>

## Codex second opinion
<선택: 코덱스 교차검증을 돌린 경우 핵심 반론·동의 요약. 안 돌렸으면 N/A.>

## Consequences
- <긍정적 영향>
- <감수하는 단점·리스크>
- <후속 작업 / 영향받는 코드·모듈>
````

- [ ] **Step 2: 검증**

Run: 파일 열어 frontmatter 5개 필드(`status/date/area/supersedes/superseded-by`)와 섹션 5개(Context/Options/Decision/Codex second opinion/Consequences)가 모두 있는지 확인.
Expected: 누락 없음.

- [ ] **Step 3: 커밋 지점 알림** (사용자가 커밋)

제안 메시지: `chore: ADR 템플릿 추가`

---

### Task 3: README 인덱스 초기본 작성

**Files:**
- Create: `docs/adr/README.md`

- [ ] **Step 1: 인덱스 파일 작성**

아래 내용 그대로 작성한다.

````markdown
# ADR (Architecture Decision Records)

설계·기술 의사결정을 영역별로 기록한다. 작성은 `/adr` 커맨드로 한다.

## 규칙
- 결정 1건 = 파일 1개. 파일명: `YYYY-MM-DD-<english-slug>.md`.
- 같은 날 같은 영역 2건 이상: `YYYY-MM-DD-1-<slug>.md`, `-2-` …
- ADR은 수정하지 않는다. 결정이 바뀌면 새 ADR로 대체(supersede)한다.
  - 옛 ADR: `status: Superseded` + `superseded-by` 링크 추가.
  - 새 ADR: `supersedes` 링크 추가.
- 여러 영역에 걸친 결정은 `arch/`에 둔다.
- 본문은 한글, 파일명 슬러그·코드·기술용어는 영어.

## market
| 날짜 | 결정 | 상태 |
|---|---|---|

## order
| 날짜 | 결정 | 상태 |
|---|---|---|

## account
| 날짜 | 결정 | 상태 |
|---|---|---|

## infra
| 날짜 | 결정 | 상태 |
|---|---|---|

## arch
| 날짜 | 결정 | 상태 |
|---|---|---|
````

- [ ] **Step 2: 검증**

Run: 파일 열어 5개 영역 표 헤더가 모두 있는지 확인.
Expected: market/order/account/infra/arch 표 존재.

- [ ] **Step 3: 커밋 지점 알림** (사용자가 커밋)

제안 메시지: `docs: ADR 인덱스 초기본 추가`

---

### Task 4: `/adr` 슬래시 커맨드 작성

**Files:**
- Create: `.claude/commands/adr.md`

- [ ] **Step 1: 커맨드 파일 작성**

아래 내용 그대로 작성한다. (frontmatter는 Claude Code 커맨드 규격)

````markdown
---
description: 현재 대화의 설계 결정을 ADR 문서로 기록한다
argument-hint: "[area] [제목] | --codex"
---

현재 대화에서 확정된 설계·기술 **의사결정**을 `docs/adr/` 에 ADR 문서로 남긴다.
설계·구현은 하지 말고, 이미 나온 결정을 기록만 한다.

## 입력
`$ARGUMENTS` — 선택적으로 `area`와 제목 힌트, `--codex` 플래그를 포함할 수 있다.
- 예: `order 주문 멱등성`, `--codex`, 빈 값.

## 절차
1. **결정 추출**: 최근 대화에서 (a) 배경·제약, (b) 검토한 옵션 2개 이상,
   (c) 최종 선택과 근거를 뽑는다. 결정이 불명확하면 사용자에게 1가지만 되묻는다.
2. **area 판별**: `market | order | account | infra | arch` 중 하나.
   인자로 주어지면 그걸 쓰고, 아니면 대화 맥락으로 정하되 애매하면 `arch`.
3. **파일 경로**: `docs/adr/<area>/<오늘날짜>-<english-slug>.md`.
   같은 날 같은 영역에 이미 있으면 `-1-`, `-2-` 순번을 붙인다.
   오늘 날짜는 세션에 주어진 currentDate를 쓴다.
4. **초안 작성**: `docs/adr/template.md` 계약을 그대로 따른다.
   - frontmatter `status` 기본 `Accepted` (확정된 결정이므로), `date`, `area` 채움.
   - H1 제목: 동사형 한 문장(한글).
   - Options: 2개 이상, `**옵션명** — 트레이드오프` 형식.
   - Consequences: 불릿, 긍정 → 부정 → 후속작업 순.
   - 본문 한글, 슬러그·코드·기술용어 영어.
5. **코덱스 교차검증(선택)**: `--codex` 가 있거나 중요한 설계 결정이면
   codex 로 반론을 받아 `Codex second opinion` 섹션에 3~5줄 요약한다.
   안 하면 그 섹션은 `N/A`.
6. **supersede 처리**: 이 결정이 기존 ADR을 뒤집으면,
   옛 ADR의 `status` 를 `Superseded` 로, `superseded-by` 에 새 파일 경로를 넣는다.
   (옛 ADR 본문·파일명·날짜는 절대 수정하지 않는다.)
   새 ADR의 `supersedes` 에 옛 파일 경로를 넣는다.
7. **README 갱신**: `docs/adr/README.md` 의 해당 영역 표에 행을 추가한다.
   supersede된 옛 결정의 상태도 `Superseded` 로 갱신한다.

## 저장 전 검증 체크리스트 (하나라도 실패하면 고쳐서 다시)
- [ ] frontmatter: `status` ∈ {Proposed, Accepted, Superseded}, `area` ∈ 5개 값.
- [ ] 섹션 5개(Context/Options/Decision/Codex second opinion/Consequences) 모두 존재, 빈 섹션은 `N/A`.
- [ ] Options 2개 이상, 각 항목에 트레이드오프 있음.
- [ ] H1 제목이 동사형 한 문장.
- [ ] 파일명 `YYYY-MM-DD-<slug>.md` 형식, 슬러그 영어.
- [ ] README 해당 영역 표에 행 추가됨.

## 커밋
파일 생성/수정 후 **커밋하지 않는다**. "ADR 작성 완료: <경로>" 로 알리고
커밋은 사용자에게 맡긴다.
````

- [ ] **Step 2: 커맨드 인식 확인**

Run: Claude Code 에서 `/adr` 입력 시 자동완성에 노출되는지 확인.
Expected: `/adr` 커맨드가 목록에 뜨고 description 이 보인다.

- [ ] **Step 3: 커밋 지점 알림** (사용자가 커밋)

제안 메시지: `feat: ADR 기록용 /adr 슬래시 커맨드 추가`

---

### Task 5: 엔드투엔드 검증 (실제 ADR 1건 작성)

**Files:**
- Create: `docs/adr/arch/<오늘날짜>-adr-decision-recording-system.md` (검증 겸 첫 ADR)
- Modify: `docs/adr/README.md` (arch 표에 행 추가)

- [ ] **Step 1: `/adr arch ADR 기록 시스템 도입` 실행**

이 시스템 도입 결정 자체를 첫 ADR로 남긴다. Context/Options/Decision 은
`docs/superpowers/specs/2026-09-10-adr-decision-recording-system-design.md` 의
핵심 결정을 요약해 채운다.

- [ ] **Step 2: 검증 체크리스트 전 항목 통과 확인**

Run: 생성된 파일과 README 를 열어 Task 4 의 체크리스트 6개 항목을 수동 확인.
Expected: 6개 모두 통과, README arch 표에 행 1개 추가됨.

- [ ] **Step 3: 커밋 지점 알림** (사용자가 커밋)

제안 메시지: `docs: ADR 시스템 도입 결정 기록 (첫 ADR)`

---

## Self-Review 결과

- **Spec coverage**: 디렉토리 구조(Task 1), 템플릿 고정 계약(Task 2), README 인덱스(Task 3),
  `/adr` 커맨드·검증·supersede·코덱스 선택(Task 4), 실동작 검증(Task 5) — 스펙 항목 모두 커버.
- **범위 밖**: 전용 에이전트·자동 생성·인프라 개선은 스펙대로 제외.
- **Placeholder**: 없음. 커맨드 본문·템플릿·README 전문 포함.
- **일관성**: 영역 enum(5), status enum(3), 파일명 규칙, 섹션 5개가 Task 2/3/4/5에서 동일하게 사용됨.
