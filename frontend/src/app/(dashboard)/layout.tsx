'use client';

import { ReactNode } from 'react';
import { Header } from '@/components/common/header/header';
import { Sidebar } from '@/components/common/sidebar/sidebar';

export default function DashboardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 overflow-auto bg-background">
          <div className="container p-6">{children}</div>
        </main>
      </div>
    </div>
  );
}
