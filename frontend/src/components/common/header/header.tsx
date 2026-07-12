'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { LogOut } from 'lucide-react';
import { useAuth } from '@/context/auth-context';

/**
 * Top navigation bar for the dashboard shell
 */
export function Header() {
  const router = useRouter();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    router.push('/login');
  };

  return (
    <header className="flex h-14 items-center justify-between border-b border-border bg-card px-6">
      <Link href="/dashboard" className="text-sm font-semibold no-underline">
        AI Legacy Modernization Copilot
      </Link>

      {user && (
        <div className="flex items-center gap-4 text-sm">
          <Link href="/profile" className="text-muted-foreground no-underline hover:text-foreground">
            {user.name}
          </Link>
          <button
            type="button"
            onClick={handleLogout}
            className="flex items-center gap-1.5 rounded-md px-2 py-1 text-muted-foreground hover:bg-muted hover:text-foreground"
          >
            <LogOut className="h-4 w-4" />
            Logout
          </button>
        </div>
      )}
    </header>
  );
}
