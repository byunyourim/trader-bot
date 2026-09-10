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
| 2026-09-10 | ADR로 설계 의사결정을 기록한다 | Accepted |
