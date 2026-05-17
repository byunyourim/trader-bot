# Frontend — Claude 작업 지침

---

## 기술 스택

| 분류 | 기술 | 버전 |
|---|---|---|
| Framework | Next.js (App Router) | 15.3.9 |
| Language | TypeScript | 5.7 |
| UI | React | 19.0.0 |
| Styling | Tailwind CSS | 3.4 |
| 차트 | lightweight-charts | 4.2 |
| 상태 관리 | Zustand | 5.x |
| API 통신 | SWR | 2.x |

---

## 디렉토리 구조

```
frontend/
└── src/
    ├── app/                  App Router — 페이지·레이아웃·라우팅
    │   ├── layout.tsx        루트 레이아웃
    │   ├── page.tsx          홈 (대시보드)
    │   └── globals.css
    ├── components/           재사용 UI 컴포넌트
    ├── lib/
    │   ├── api/              SWR fetcher, API 클라이언트 함수
    │   └── store/            Zustand store
    └── types/                공유 타입 정의
```

---

## 아키텍처 원칙

### Server Component 우선

- 기본은 **Server Component** — 데이터 페칭, 레이아웃, 정적 UI
- 아래 경우에만 `'use client'` 추가:
  - `useState` / `useEffect` / `useRef` 사용
  - 브라우저 API 사용 (WebSocket, localStorage 등)
  - 이벤트 핸들러 (onClick 등)
  - lightweight-charts처럼 DOM에 직접 접근하는 라이브러리

### API 통신 — SWR

- REST API 호출은 `src/lib/api/` 아래에서 중앙 관리
- 컴포넌트에서 직접 `fetch` 금지 — SWR hook 또는 api 함수 사용
- 실시간 갱신이 필요한 시세·잔고 데이터는 SWR `refreshInterval` 활용

```typescript
// lib/api/account.ts
export const fetcher = (url: string) => fetch(url).then(r => r.json());

// 컴포넌트에서
const { data, error } = useSWR('/api/accounts/balance', fetcher, {
  refreshInterval: 3000,
});
```

### 상태 관리 — Zustand

- 전역 상태(선택된 종목, 웹소켓 연결 상태 등)는 `src/lib/store/` 아래 store로 관리
- 컴포넌트 로컬 상태(`useState`)로 충분하면 Zustand 사용 금지
- store 파일 1개 = 도메인 1개 (예: `marketStore.ts`, `accountStore.ts`)

```typescript
// lib/store/marketStore.ts
import { create } from 'zustand';

interface MarketStore {
  selectedTicker: string;
  setSelectedTicker: (ticker: string) => void;
}

export const useMarketStore = create<MarketStore>((set) => ({
  selectedTicker: '005930',
  setSelectedTicker: (ticker) => set({ selectedTicker: ticker }),
}));
```

---

## 코드 스타일

- 타입은 `types/` 에 정의 — `any` 금지, `unknown` 사용 후 타입 가드
- 컴포넌트 파일명은 PascalCase (`PriceChart.tsx`)
- hook 파일명은 camelCase, `use` 접두사 (`useBalance.ts`)
- Tailwind 클래스는 컴포넌트 안에서만 사용, 인라인 style 금지

---

## 개발 원칙

- **작업 범위 엄수**: 요청한 컴포넌트·파일만 수정한다. 요청 없이 다른 파일 건드리지 않는다.
- **YAGNI**: 현재 화면에 필요한 컴포넌트·훅만 만든다. 미래 확장을 위한 추상화 금지.
- **커밋·푸시**: 명시적 요청이 있을 때만 수행한다.
- **커밋 전 검증**: `npm run build`로 빌드 성공 확인 후 커밋한다.

---

## 개발 명령어

| 작업 | 명령 |
|---|---|
| 개발 서버 | `npm run dev` |
| 빌드 | `npm run build` |
| 린트 | `npm run lint` |
