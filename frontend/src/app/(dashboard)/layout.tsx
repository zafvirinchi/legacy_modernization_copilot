'use client';

import { ReactNode, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Header } from '@/components/common/header/header';
import { Sidebar } from '@/components/common/sidebar/sidebar';
import { useAuth } from '@/context/auth-context';

export default function DashboardLayout({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();
  const [isMobileNavOpen, setIsMobileNavOpen] = useState(false);

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.replace('/login');
    }
  }, [isLoading, isAuthenticated, router]);

  if (isLoading || !isAuthenticated) {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-muted-foreground">
        Loading...
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Header onMenuClick={() => setIsMobileNavOpen(true)} />
      <div className="flex flex-1">
        <Sidebar isOpen={isMobileNavOpen} onClose={() => setIsMobileNavOpen(false)} />
        <main className="flex-1 overflow-auto bg-background">
          <div className="container p-4 sm:p-6">{children}</div>
        </main>
      </div>
    </div>
  );
}
