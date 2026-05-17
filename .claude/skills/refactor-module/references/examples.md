# refactor-module 예시

## 좋은 리팩토링

Before:
```tsx
{user.role === "admin" && project.ownerId !== user.id && <DeleteButton />}
```

After:
```tsx
const canDelete = canDeleteProject(user, project);
{canDelete && <DeleteButton />}
```

이유:
- 권한 판단을 재사용 가능한 business rule로 분리
- UI 가독성 개선
- 동작은 유지

## 나쁜 리팩토링

- 기능 하나 수정하면서 전체 folder structure 변경
- 아직 필요 없는 abstract base class 추가
- 모든 component를 한 번에 이동
