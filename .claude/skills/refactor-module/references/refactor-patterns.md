# 리팩토링 패턴

## Extract Function

사용:
- 같은 로직이 반복됨
- 함수가 너무 김
- 조건문이 복잡함

주의:
- 한 번만 쓰는 함수를 의미 없이 빼지 않는다.

## Extract Component

사용:
- UI 블록이 독립된 책임을 가짐
- parent component가 너무 커짐

주의:
- props drilling이 더 심해지면 하지 않는다.

## Separate Business Logic

사용:
- UI 안에 권한/계산/validation이 섞임

예:
```ts
const canEdit = canEditProject(user, project);
```

## Rename

사용:
- 이름이 의미를 숨김
- boolean 이름이 조건을 헷갈리게 함

예:
```ts
isOwner
hasActiveSubscription
canDeleteProject
```
