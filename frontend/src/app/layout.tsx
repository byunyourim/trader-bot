import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Trader Bot",
  description: "KIS-based trading dashboard",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className="min-h-screen">{children}</body>
    </html>
  );
}
