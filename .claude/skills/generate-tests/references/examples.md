# generate-tests 예시

## 좋은 테스트

```ts
it("returns 403 when non-admin tries to delete a user", async () => {
  const response = await deleteUser({ actor: normalUser, targetUserId });
  expect(response.status).toBe(403);
});
```

이유:
- 사용자에게 중요한 권한 동작을 검증한다.
- 구현 세부사항이 아니라 결과를 본다.

## 나쁜 테스트

```ts
expect(component.state.isModalOpen).toBe(true);
```

이유:
- 내부 구현에 과하게 의존한다.
- UI 변경에 쉽게 깨진다.

## Regression Test

버그가 있었다면:
- 실패 조건을 먼저 테스트로 고정
- 수정 후 같은 테스트가 통과하는지 확인
