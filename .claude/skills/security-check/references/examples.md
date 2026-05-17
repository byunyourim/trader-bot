# security-check 예시

## 권한 누락

문제:
```ts
await db.project.findMany();
```

수정:
```ts
await db.project.findMany({
  where: { ownerId: currentUser.id }
});
```

리뷰 코멘트:
- 일반 사용자가 다른 사용자의 project를 볼 수 있음
- user scope 조건 추가 필요

## secret 노출

문제:
```tsx
const apiKey = process.env.INTERNAL_API_KEY;
```

client component에서 사용하면 위험하다.

수정:
- server route/action에서만 secret 사용
- client에는 필요한 결과만 전달
