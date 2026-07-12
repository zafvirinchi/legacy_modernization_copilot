import Link from 'next/link';
import { FolderKanban, Files, HardDrive } from 'lucide-react';
import { Project } from '@/types';
import { formatBytes, formatDateTime } from '@/utils';

interface ProjectCardProps {
  project: Project;
}

export function ProjectCard({ project }: ProjectCardProps) {
  return (
    <Link
      href={`/projects/${project.id}`}
      className="group flex flex-col gap-3 rounded-lg border border-border bg-card p-4 no-underline transition-shadow hover:shadow-md"
    >
      <div className="flex items-start gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-muted">
          <FolderKanban className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="min-w-0">
          <p className="truncate font-medium text-foreground group-hover:underline">{project.name}</p>
          <p className="text-xs text-muted-foreground">{formatDateTime(project.createdAt)}</p>
        </div>
      </div>

      <div className="flex items-center gap-4 text-xs text-muted-foreground">
        <span className="flex items-center gap-1">
          <Files className="h-3.5 w-3.5" />
          {project.totalFiles} files
        </span>
        <span className="flex items-center gap-1">
          <HardDrive className="h-3.5 w-3.5" />
          {formatBytes(project.totalSizeBytes)}
        </span>
      </div>
    </Link>
  );
}
