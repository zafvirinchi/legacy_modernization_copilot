'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { isAxiosError } from 'axios';
import { ScanSearch, Loader2 } from 'lucide-react';
import { ProjectSummaryCard } from '@/components/upload';
import { TechnologyDetectionPanel } from '@/components/detection';
import { projectService, technologyDetectionService } from '@/services';
import { Project, TechnologyDetectionResult } from '@/types';

type LoadState = 'loading' | 'loaded' | 'error';

export default function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();

  const [project, setProject] = useState<Project | null>(null);
  const [projectState, setProjectState] = useState<LoadState>('loading');

  const [detection, setDetection] = useState<TechnologyDetectionResult | null>(null);
  const [isDetecting, setIsDetecting] = useState(false);
  const [detectionError, setDetectionError] = useState<string | null>(null);

  useEffect(() => {
    projectService
      .get(id)
      .then((data) => {
        setProject(data);
        setProjectState('loaded');
      })
      .catch(() => setProjectState('error'));

    technologyDetectionService
      .get(id)
      .then(setDetection)
      .catch(() => setDetection(null));
  }, [id]);

  const handleRunDetection = async () => {
    setIsDetecting(true);
    setDetectionError(null);
    try {
      const result = await technologyDetectionService.run(id);
      setDetection(result);
    } catch (error: unknown) {
      const message = isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : undefined;
      setDetectionError(message ?? 'Failed to run technology detection');
    } finally {
      setIsDetecting(false);
    }
  };

  if (projectState === 'loading') {
    return <p className="text-sm text-muted-foreground">Loading project...</p>;
  }

  if (projectState === 'error' || !project) {
    return <p className="text-sm text-destructive">Failed to load project</p>;
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">{project.name}</h1>
        <p className="mt-1 text-sm text-muted-foreground">Project details and technology detection</p>
      </div>

      <div className="max-w-xl">
        <ProjectSummaryCard project={project} />
      </div>

      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={handleRunDetection}
          disabled={isDetecting}
          className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-50"
        >
          {isDetecting ? <Loader2 className="h-4 w-4 animate-spin" /> : <ScanSearch className="h-4 w-4" />}
          {isDetecting ? 'Detecting...' : detection ? 'Re-run Technology Detection' : 'Run Technology Detection'}
        </button>
        {detectionError && <p className="text-sm text-destructive">{detectionError}</p>}
      </div>

      {detection ? (
        <TechnologyDetectionPanel result={detection} />
      ) : (
        <p className="text-sm text-muted-foreground">
          This project hasn&apos;t been analyzed yet. Run technology detection to see results.
        </p>
      )}
    </div>
  );
}
