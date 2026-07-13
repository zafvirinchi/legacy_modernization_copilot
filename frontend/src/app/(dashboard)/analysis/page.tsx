'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { CheckCircle2, Circle, FolderOpen } from 'lucide-react';
import { projectService } from '@/services';
import { Project } from '@/types';
import { useProjectAnalysisStages } from '@/hooks';

type LoadState = 'loading' | 'loaded' | 'error';

const STAGE_LABELS = ['Technology', 'Business', 'Architecture', 'Security', 'Performance', 'Plan', 'Spring Boot'];

function AnalysisRow({ project }: { project: Project }) {
  const stages = useProjectAnalysisStages(project.id);

  return (
    <tr className="border-b border-border last:border-0">
      <td className="whitespace-nowrap py-3 pr-4">
        <Link href={`/projects/${project.id}`} className="font-medium hover:underline">
          {project.name}
        </Link>
      </td>
      {stages ? (
        stages.map((stage) => (
          <td key={stage.key} className="px-2 py-3 text-center" title={stage.label}>
            {stage.completedAt ? (
              <CheckCircle2 className="mx-auto h-4 w-4 text-emerald-500" />
            ) : (
              <Circle className="mx-auto h-4 w-4 text-muted-foreground/40" />
            )}
          </td>
        ))
      ) : (
        <td colSpan={STAGE_LABELS.length} className="py-3 text-center text-xs text-muted-foreground">
          Loading...
        </td>
      )}
    </tr>
  );
}

export default function AnalysisPage() {
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
        <h1 className="text-2xl font-semibold">Analysis</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Which analyses have been run across all of your projects
        </p>
      </div>

      {state === 'loading' && <p className="text-sm text-muted-foreground">Loading projects...</p>}
      {state === 'error' && <p className="text-sm text-destructive">Failed to load projects</p>}

      {state === 'loaded' && projects.length === 0 && (
        <div className="flex flex-col items-center gap-3 rounded-lg border border-dashed border-border p-10 text-center">
          <FolderOpen className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No projects uploaded yet. <Link href="/upload">Upload your first project</Link> to run analyses on it.
          </p>
        </div>
      )}

      {state === 'loaded' && projects.length > 0 && (
        <div className="overflow-x-auto rounded-lg border border-border bg-card">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border text-left text-xs font-medium uppercase tracking-wide text-muted-foreground">
                <th className="py-3 pl-4 pr-4">Project</th>
                {STAGE_LABELS.map((label) => (
                  <th key={label} className="px-2 py-3 text-center font-medium">
                    {label}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="px-4">
              {projects.map((project) => (
                <AnalysisRow key={project.id} project={project} />
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
