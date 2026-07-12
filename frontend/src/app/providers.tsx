'use client';

import React, { ReactNode } from 'react';
import { ThemeProvider } from '@/context/theme-context';
import { AuthProvider } from '@/context/auth-context';

export function Providers({ children }: { children: ReactNode }) {
  return (
    <ThemeProvider>
      <AuthProvider>{children}</AuthProvider>
    </ThemeProvider>
  );
}
