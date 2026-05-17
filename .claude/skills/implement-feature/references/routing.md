# 상세 라우팅

## Auth / Permission

검색 키워드:
- auth
- middleware
- session
- token
- role
- permission
- policy

확인:
- 인증 여부만 체크하는지, 권한까지 체크하는지 구분
- user id scope가 지켜지는지
- admin 기능이면 self-action 제한이 필요한지

## Database

검색 키워드:
- schema
- migration
- model
- repository
- query
- transaction

확인:
- nullable mismatch
- migration 필요 여부
- transaction 필요 여부
- user data isolation

## API

검색 키워드:
- route
- controller
- handler
- validator
- schema
- contract

확인:
- request validation
- response shape
- error shape
- client compatibility

## UI

검색 키워드:
- page
- component
- hook
- form
- loading
- error

확인:
- 기존 shared component 사용 여부
- loading / empty / error 상태
- accessibility 기본 속성
- 불필요한 global state 여부
