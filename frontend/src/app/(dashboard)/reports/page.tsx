'use client';

import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Download, FileText, FolderOpen, Loader2 } from 'lucide-react';
import { modernizationReportService, projectService } from '@/services';
import { Project } from '@/types';
import { formatDateTime, triggerBlobDownload } from '@/utils';
import Link from 'next/link';

type LoadState = 'loading' | 'loaded' | 'error';

function ReportRow({ project }: { project: Project }) {
  const [isDownloading, setIsDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDownload = async () => {
    setIsDownloading(true);
    setError(null);
    try {
      const blob = await modernizationReportService.downloadPdf(project.id);
      triggerBlobDownload(blob, `${project.name}-modernization-report.pdf`);
    } catch (downloadError: unknown) {
      const message = isAxiosError<{ message?: string }>(downloadError)
        ? downloadError.response?.data?.message
        : undefined;
      setError(message ?? 'Failed to download report');
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <div className="flex items-center justify-between gap-4 rounded-lg border border-border bg-card p-4">
      <div className="flex items-center gap-3 min-w-0">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-muted">
          <FileText className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="min-w-0">
          <Link href={`/projects/${project.id}`} className="truncate font-medium hover:underline">
            {project.name}
          </Link>
          <p className="text-xs text-muted-foreground">Uploaded {formatDateTime(project.createdAt)}</p>
          {error && <p className="text-xs text-destructive">{error}</p>}
        </div>
      </div>
      <button
        type="button"
        onClick={handleDownload}
        disabled={isDownloading}
        className="inline-flex shrink-0 items-center gap-2 rounded-md border border-border bg-background px-3 py-2 text-sm font-medium hover:bg-muted disabled:opacity-50"
      >
        {isDownloading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Download className="h-4 w-4" />}
        {isDownloading ? 'Preparing...' : 'Download PDF'}
      </button>
    </div>
  );
}

export default function ReportsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [state, setState] = useState<LoadState>('loading');

  useEffect(() => {
    projectService
      .list()
      .then((data) => {
        setProjects(data);
        setState('loaded');
      })
      .catch(() => setState('error'));
  }, []);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Reports</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Download a modernization report PDF for any of your projects. Sections you haven&apos;t analyzed yet are
          included as placeholders.
        </p>
      </div>

      {state === 'loading' && <p className="text-sm text-muted-foreground">Loading projects...</p>}
      {state === 'error' && <p className="text-sm text-destructive">Failed to load projects</p>}

      {state === 'loaded' && projects.length === 0 && (
        <div className="flex flex-col items-center gap-3 rounded-lg border border-dashed border-border p-10 text-center">
          <FolderOpen className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No projects uploaded yet. <Link href="/upload">Upload your first project</Link> to generate a report.
          </p>
        </div>
      )}

      {state === 'loaded' && projects.length > 0 && (
        <div className="flex flex-col gap-3">
          {projects.map((project) => (
            <ReportRow key={project.id} project={project} />
          ))}
        </div>
      )}
    </div>
  );
}
