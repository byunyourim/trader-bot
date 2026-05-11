"use client";

import { useEffect, useRef } from "react";
import { createChart, ColorType, IChartApi, ISeriesApi } from "lightweight-charts";

export default function PriceChart() {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    const chart: IChartApi = createChart(containerRef.current, {
      layout: {
        background: { type: ColorType.Solid, color: "transparent" },
        textColor: "#cbd5e1",
      },
      grid: {
        vertLines: { color: "rgba(148, 163, 184, 0.1)" },
        horzLines: { color: "rgba(148, 163, 184, 0.1)" },
      },
      width: containerRef.current.clientWidth,
      height: 400,
    });

    const series: ISeriesApi<"Candlestick"> = chart.addCandlestickSeries();
    series.setData([
      { time: "2026-05-04", open: 100, high: 105, low: 98, close: 102 },
      { time: "2026-05-05", open: 102, high: 108, low: 101, close: 107 },
      { time: "2026-05-06", open: 107, high: 110, low: 104, close: 105 },
      { time: "2026-05-07", open: 105, high: 109, low: 103, close: 108 },
      { time: "2026-05-08", open: 108, high: 112, low: 107, close: 111 },
    ]);

    const handleResize = () => {
      if (containerRef.current) {
        chart.applyOptions({ width: containerRef.current.clientWidth });
      }
    };
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
      chart.remove();
    };
  }, []);

  return <div ref={containerRef} className="w-full" />;
}
