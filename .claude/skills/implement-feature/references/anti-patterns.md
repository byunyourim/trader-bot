# implement-feature 안티패턴

피한다:

- 요청 이해 전 코드 수정
- 전체 repo 탐색
- 기능과 무관한 리팩토링
- 새 architecture 도입
- 작은 문제에 dependency 추가
- 기존 utility 중복 구현
- public API 불필요 변경
- 보안/삭제/결제 기능에서 확인 없이 진행

## 특히 금지

나쁨:
```txt
기능 추가하면서 폴더 구조 전체 변경
```

좋음:
```txt
기존 구조 안에서 최소 변경
```
