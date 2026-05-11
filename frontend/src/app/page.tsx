import PriceChart from "@/components/PriceChart";

export default function HomePage() {
  return (
    <main className="mx-auto max-w-6xl px-6 py-10">
      <header className="mb-6">
        <h1 className="text-2xl font-semibold">Trader Bot</h1>
        <p className="text-sm text-neutral-400">KIS 기반 실시간 트레이딩 대시보드</p>
      </header>

      <section className="rounded-xl border border-neutral-800 bg-neutral-900/40 p-4">
        <h2 className="mb-3 text-sm font-medium text-neutral-300">실시간 차트</h2>
        <PriceChart />
      </section>
    </main>
  );
}
