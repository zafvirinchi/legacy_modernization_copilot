'use client';

import Link from 'next/link';

/**
 * Top navigation bar for the dashboard shell
 */
export function Header() {
  return (
    <header className="flex h-14 items-center border-b border-border bg-card px-6">
      <Link href="/dashboard" className="text-sm font-semibold no-underline">
        AI Legacy Modernization Copilot
      </Link>
    </header>
  );
}
