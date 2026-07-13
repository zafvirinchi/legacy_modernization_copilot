'use client';

import { useRouter } from 'next/navigation';
import { LogOut, Moon, Sun } from 'lucide-react';
import { useAuth } from '@/context/auth-context';
import { useTheme } from '@/context/theme-context';
import { cn } from '@/utils';

const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Admin',
  ARCHITECT: 'Architect',
  DEVELOPER: 'Developer',
};

export default function SettingsPage() {
  const { user, logout } = useAuth();
  const { theme, setTheme } = useTheme();
  const router = useRouter();

  const handleLogout = async () => {
    await logout();
    router.push('/login');
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Settings</h1>
        <p className="mt-1 text-sm text-muted-foreground">Application preferences and account actions</p>
      </div>

      <div className="max-w-md rounded-lg border border-border bg-card p-6">
        <h2 className="font-semibold">Appearance</h2>
        <p className="mt-1 text-sm text-muted-foreground">Choose how the dashboard looks on this device</p>

        <div className="mt-4 grid grid-cols-2 gap-3">
          <button
            type="button"
            onClick={() => setTheme('light')}
            className={cn(
              'flex items-center justify-center gap-2 rounded-md border px-4 py-3 text-sm font-medium transition-colors',
              theme === 'light'
                ? 'border-primary bg-accent text-accent-foreground'
                : 'border-border hover:bg-muted'
            )}
          >
            <Sun className="h-4 w-4" />
            Light
          </button>
          <button
            type="button"
            onClick={() => setTheme('dark')}
            className={cn(
              'flex items-center justify-center gap-2 rounded-md border px-4 py-3 text-sm font-medium transition-colors',
              theme === 'dark'
                ? 'border-primary bg-accent text-accent-foreground'
                : 'border-border hover:bg-muted'
            )}
          >
            <Moon className="h-4 w-4" />
            Dark
          </button>
        </div>
      </div>

      {user && (
        <div className="max-w-md rounded-lg border border-border bg-card p-6">
          <h2 className="font-semibold">Account</h2>
          <dl className="mt-4 grid grid-cols-2 gap-4 text-sm">
            <div>
              <dt className="text-muted-foreground">Email</dt>
              <dd className="mt-1 font-medium">{user.email}</dd>
            </div>
            <div>
              <dt className="text-muted-foreground">Role</dt>
              <dd className="mt-1 font-medium">{ROLE_LABELS[user.role] ?? user.role}</dd>
            </div>
          </dl>

          <button
            type="button"
            onClick={handleLogout}
            className="mt-6 inline-flex items-center gap-2 rounded-md border border-border px-4 py-2 text-sm font-medium text-destructive hover:bg-destructive/10"
          >
            <LogOut className="h-4 w-4" />
            Log out
          </button>
        </div>
      )}
    </div>
  );
}
