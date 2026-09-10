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
