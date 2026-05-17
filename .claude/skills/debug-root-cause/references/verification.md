# 디버깅 검증

## 실행 순서 (전부 통과 필수)

```bash
# 1. 실패한 테스트만 먼저
npm test -- path/to/failing.test.ts

# 2. 빌드 — 반드시 실행 (실패 시 미완료)
npm run build

# 3. 전체 테스트 — 기존 회귀 없는지 확인
npm test
```

## 사이드 이펙트 확인

- 수정한 파일을 import하는 다른 모듈에 영향 없는가
- 변경한 함수 시그니처가 유지되는가
- 기존 테스트가 전부 통과하는가

## 버그 재현이 있으면

1. 재현 명령으로 실패 확인
2. 수정
3. `npm run build` → `npm test` 순으로 검증
4. regression test 추가 여부 판단 (같은 버그 재발 방지)
