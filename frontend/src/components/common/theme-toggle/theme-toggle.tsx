'use client';

import { Moon, Sun } from 'lucide-react';
import { useTheme } from '@/context/theme-context';

export function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
      className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground hover:bg-muted hover:text-foreground"
    >
      {theme === 'dark' ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
    </button>
  );
}
