'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { ArchitectureAnalysisPanel } from '@/components/architecture';
import { architectureAnalysisService, projectService } from '@/services';
import { ArchitectureAnalysisResult, Project } from '@/types';

export default function ArchitectureViewerPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState('');
  const [architecture, setArchitecture] = useState<ArchitectureAnalysisResult | null>(null);
  const [status, setStatus] = useState<'idle' | 'loading' | 'loaded' | 'missing' | 'error'>('idle');

  useEffect(() => {
    projectService
      .list()
      .then((data) => {
        setProjects(data);
        if (data.length > 0) {
          setSelectedProjectId(data[0].id);
        }
      })
      .catch(() => setProjects([]));
  }, []);

  useEffect(() => {
    if (!selectedProjectId) {
      return;
    }
    setStatus('loading');
    setArchitecture(null);
    architectureAnalysisService
      .get(selectedProjectId)
      .then((result) => {
        setArchitecture(result);
        setStatus('loaded');
      })
      .catch(() => setStatus('missing'));
  }, [selectedProjectId]);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Architecture Viewer</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Review a project&apos;s current and target architecture diagrams
        </p>
      </div>

      {projects.length === 0 ? (
        <p className="text-sm text-muted-foreground">
          No projects uploaded yet. <Link href="/upload">Upload a project</Link> to get started.
        </p>
      ) : (
        <>
          <div className="max-w-sm">
            <label htmlFor="project-picker" className="text-sm font-medium">
              Project
            </label>
            <select
              id="project-picker"
              value={selectedProjectId}
              onChange={(event) => setSelectedProjectId(event.target.value)}
              className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            >
              {projects.map((project) => (
                <option key={project.id} value={project.id}>
                  {project.name}
                </option>
              ))}
            </select>
          </div>

          {status === 'loading' && <p className="text-sm text-muted-foreground">Loading architecture...</p>}
          {status === 'missing' && (
            <p className="text-sm text-muted-foreground">
              This project hasn&apos;t had an architecture analysis yet.{' '}
              <Link href={`/projects/${selectedProjectId}`}>Run it from the project page</Link>.
            </p>
          )}
          {status === 'loaded' && architecture && <ArchitectureAnalysisPanel result={architecture} />}
        </>
      )}
    </div>
  );
}
