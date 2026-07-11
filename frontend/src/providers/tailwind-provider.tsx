/**
 * TailwindCSS Provider
 * Handles theme persistence and dark mode
 */

'use client';

import { ReactNode, useEffect, useState } from 'react';

export function TailwindProvider({ children }: { children: ReactNode }) {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);

    // Check for saved theme preference or default to 'light'
    const theme = localStorage.getItem('theme') || 'light';
    const isDark = theme === 'dark';

    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, []);

  if (!mounted) {
    return <>{children}</>;
  }

  return <>{children}</>;
}
