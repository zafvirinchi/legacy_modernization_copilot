'use client';

import { useEffect, useState } from 'react';
import { CheckCircle2, XCircle, Loader2 } from 'lucide-react';
import { healthService } from '@/services';
import type { HealthStatus } from '@/types';

type ConnectionState = 'loading' | 'online' | 'offline';

export function SystemStatusCard() {
  const [state, setState] = useState<ConnectionState>('loading');
  const [health, setHealth] = useState<HealthStatus | null>(null);

  useEffect(() => {
    let cancelled = false;

    healthService
      .check()
      .then((data) => {
        if (cancelled) return;
        setHealth(data);
        setState('online');
      })
      .catch(() => {
        if (cancelled) return;
        setState('offline');
      });

    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="rounded-lg border border-border bg-card p-4">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-medium text-muted-foreground">Backend Status</h3>
        {state === 'loading' && <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />}
        {state === 'online' && <CheckCircle2 className="h-4 w-4 text-emerald-500" />}
        {state === 'offline' && <XCircle className="h-4 w-4 text-destructive" />}
      </div>

      <p className="mt-2 text-lg font-semibold">
        {state === 'loading' && 'Checking connection...'}
        {state === 'online' && `${health?.service ?? 'Service'} is online`}
        {state === 'offline' && 'Unable to reach backend'}
      </p>

      {state === 'online' && health && (
        <p className="mt-1 text-xs text-muted-foreground">
          v{health.version} · {health.environment} environment
        </p>
      )}
    </div>
  );
}
