import { Level } from '@/types';
import { cn } from '@/utils';

const LEVEL_STYLES: Record<Level, string> = {
  LOW: 'bg-muted text-muted-foreground',
  MEDIUM: 'bg-muted text-foreground',
  HIGH: 'bg-destructive/10 text-destructive',
};

interface LevelBadgeProps {
  level: Level;
}

export function LevelBadge({ level }: LevelBadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
        LEVEL_STYLES[level]
      )}
    >
      {level.charAt(0) + level.slice(1).toLowerCase()}
    </span>
  );
}
