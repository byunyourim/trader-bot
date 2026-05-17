# Severity 기준

## Critical

즉시 막아야 함:
- 인증 우회
- 권한 상승
- 데이터 손실
- secret 노출
- 결제/청구 오류

## High

merge 전 수정 권장:
- 주요 기능 깨짐
- API contract 깨짐
- migration 위험
- 심각한 edge case 누락

## Medium

수정하면 좋음:
- 일부 사용자 조건에서 버그
- 테스트 누락
- 성능 저하 가능성

## Low

선택:
- 가독성 개선
- 작은 유지보수성 개선
