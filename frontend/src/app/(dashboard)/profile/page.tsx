'use client';

import { useAuth } from '@/context/auth-context';
import { formatDateTime } from '@/utils';

const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Admin',
  ARCHITECT: 'Architect',
  DEVELOPER: 'Developer',
};

export default function ProfilePage() {
  const { user } = useAuth();

  if (!user) {
    return null;
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Profile</h1>
        <p className="mt-1 text-sm text-muted-foreground">Your account details</p>
      </div>

      <div className="max-w-md rounded-lg border border-border bg-card p-6">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-full bg-muted text-lg font-semibold">
            {user.name.charAt(0).toUpperCase()}
          </div>
          <div>
            <p className="text-lg font-semibold">{user.name}</p>
            <p className="text-sm text-muted-foreground">{user.email}</p>
          </div>
        </div>

        <dl className="mt-6 grid grid-cols-2 gap-4 text-sm">
          <div>
            <dt className="text-muted-foreground">Role</dt>
            <dd className="mt-1 font-medium">{ROLE_LABELS[user.role] ?? user.role}</dd>
          </div>
          <div>
            <dt className="text-muted-foreground">Member since</dt>
            <dd className="mt-1 font-medium">{user.createdAt ? formatDateTime(user.createdAt) : '—'}</dd>
          </div>
        </dl>
      </div>
    </div>
  );
}
