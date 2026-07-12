'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import * as Icons from 'lucide-react';
import { NAVIGATION_ITEMS } from '@/constants';
import { cn } from '@/utils';

/**
 * Sidebar navigation for the dashboard shell
 */
export function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="hidden w-60 shrink-0 border-r border-border bg-card md:block">
      <nav className="flex flex-col gap-1 p-4">
        {NAVIGATION_ITEMS.map((item) => {
          const Icon = Icons[item.icon as keyof typeof Icons] as Icons.LucideIcon | undefined;
          const isActive = pathname?.startsWith(item.href);

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-accent text-accent-foreground'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground'
              )}
            >
              {Icon && <Icon className="h-4 w-4" />}
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
