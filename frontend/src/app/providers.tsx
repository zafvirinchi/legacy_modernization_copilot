'use client';

import React, { ReactNode } from 'react';
import { TailwindProvider } from '@/providers/tailwind-provider';
import { AuthProvider } from '@/context/auth-context';

export function Providers({ children }: { children: ReactNode }) {
  return (
    <TailwindProvider>
      <AuthProvider>{children}</AuthProvider>
    </TailwindProvider>
  );
}
