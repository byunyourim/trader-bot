# review-pr 예시

## 좋은 리뷰 코멘트

```md
### High
- 파일: `app/api/users/route.ts`
- 문제: user id scope 없이 모든 사용자를 조회할 수 있음
- 영향: 일반 사용자가 다른 사용자의 데이터를 볼 수 있음
- 수정: query에 `where: { ownerId: currentUser.id }` 추가
```

## 나쁜 리뷰 코멘트

```md
이 변수명 별로예요.
```

이런 스타일 지적은 프로젝트 컨벤션을 깨는 경우에만 한다.

## 테스트 누락 예시

```md
## Missing Tests
- admin이 아닌 사용자가 delete API 호출 시 403을 받는 케이스 필요
```
