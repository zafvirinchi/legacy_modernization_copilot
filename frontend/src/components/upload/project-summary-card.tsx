import { CheckCircle2 } from 'lucide-react';
import { Project } from '@/types';
import { formatBytes, formatDateTime } from '@/utils';

interface ProjectSummaryCardProps {
  project: Project;
}

export function ProjectSummaryCard({ project }: ProjectSummaryCardProps) {
  const extensions = Object.entries(project.fileExtensionBreakdown).sort(([, a], [, b]) => b - a);

  return (
    <div className="rounded-lg border border-border bg-card p-6">
      <div className="flex items-start gap-3">
        <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-emerald-500" />
        <div>
          <h3 className="font-semibold">{project.name}</h3>
          <p className="text-sm text-muted-foreground">
            Uploaded {formatDateTime(project.createdAt)} from {project.originalFileName}
          </p>
        </div>
      </div>

      <div className="mt-4 grid grid-cols-2 gap-4 text-sm">
        <div>
          <p className="text-muted-foreground">Total files</p>
          <p className="text-lg font-semibold">{project.totalFiles}</p>
        </div>
        <div>
          <p className="text-muted-foreground">Project size</p>
          <p className="text-lg font-semibold">{formatBytes(project.totalSizeBytes)}</p>
        </div>
      </div>

      {extensions.length > 0 && (
        <div className="mt-4">
          <p className="text-sm text-muted-foreground">Files by extension</p>
          <div className="mt-2 flex flex-wrap gap-2">
            {extensions.map(([extension, count]) => (
              <span
                key={extension}
                className="rounded-full bg-muted px-3 py-1 text-xs font-medium text-muted-foreground"
              >
                .{extension} &middot; {count}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
