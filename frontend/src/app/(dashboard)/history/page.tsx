'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { FolderOpen } from 'lucide-react';
import { projectService } from '@/services';
import { Project } from '@/types';
import { formatBytes, formatDateTime } from '@/utils';

type LoadState = 'loading' | 'loaded' | 'error';

export default function HistoryPage() {
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
        <h1 className="text-2xl font-semibold">Project History</h1>
        <p className="mt-1 text-sm text-muted-foreground">Projects you&apos;ve previously uploaded</p>
      </div>

      {state === 'loading' && <p className="text-sm text-muted-foreground">Loading projects...</p>}
      {state === 'error' && <p className="text-sm text-destructive">Failed to load project history</p>}

      {state === 'loaded' && projects.length === 0 && (
        <div className="flex flex-col items-center gap-3 rounded-lg border border-dashed border-border p-10 text-center">
          <FolderOpen className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No projects uploaded yet. <Link href="/upload">Upload your first project</Link>.
          </p>
        </div>
      )}

      {state === 'loaded' && projects.length > 0 && (
        <div className="overflow-hidden rounded-lg border border-border">
          <table className="w-full text-sm">
            <thead className="bg-muted/50 text-left text-muted-foreground">
              <tr>
                <th className="px-4 py-3 font-medium">Name</th>
                <th className="px-4 py-3 font-medium">Files</th>
                <th className="px-4 py-3 font-medium">Size</th>
                <th className="px-4 py-3 font-medium">Uploaded</th>
              </tr>
            </thead>
            <tbody>
              {projects.map((project) => (
                <tr key={project.id} className="border-t border-border">
                  <td className="px-4 py-3 font-medium">{project.name}</td>
                  <td className="px-4 py-3">{project.totalFiles}</td>
                  <td className="px-4 py-3">{formatBytes(project.totalSizeBytes)}</td>
                  <td className="px-4 py-3 text-muted-foreground">{formatDateTime(project.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
