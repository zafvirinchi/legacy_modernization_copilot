'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { isAxiosError } from 'axios';
import { ScanSearch, Loader2, Network } from 'lucide-react';
import { ProjectSummaryCard } from '@/components/upload';
import { TechnologyDetectionPanel } from '@/components/detection';
import { ArchitectureAnalysisPanel } from '@/components/architecture';
import { projectService, technologyDetectionService, architectureAnalysisService } from '@/services';
import { Project, TechnologyDetectionResult, ArchitectureAnalysisResult } from '@/types';

type LoadState = 'loading' | 'loaded' | 'error';

export default function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();

  const [project, setProject] = useState<Project | null>(null);
  const [projectState, setProjectState] = useState<LoadState>('loading');

  const [detection, setDetection] = useState<TechnologyDetectionResult | null>(null);
  const [isDetecting, setIsDetecting] = useState(false);
  const [detectionError, setDetectionError] = useState<string | null>(null);

  const [architecture, setArchitecture] = useState<ArchitectureAnalysisResult | null>(null);
  const [isAnalyzingArchitecture, setIsAnalyzingArchitecture] = useState(false);
  const [architectureError, setArchitectureError] = useState<string | null>(null);

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

    architectureAnalysisService
      .get(id)
      .then(setArchitecture)
      .catch(() => setArchitecture(null));
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

  const handleRunArchitectureAnalysis = async () => {
    setIsAnalyzingArchitecture(true);
    setArchitectureError(null);
    try {
      const result = await architectureAnalysisService.run(id);
      setArchitecture(result);
    } catch (error: unknown) {
      const message = isAxiosError<{ message?: string }>(error) ? error.response?.data?.message : undefined;
      setArchitectureError(message ?? 'Failed to run architecture analysis');
    } finally {
      setIsAnalyzingArchitecture(false);
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
        <p className="mt-1 text-sm text-muted-foreground">Project details and analysis</p>
      </div>

      <div className="max-w-xl">
        <ProjectSummaryCard project={project} />
      </div>

      <div>
        <h2 className="text-lg font-semibold">Technology Detection</h2>
        <div className="mt-3 flex items-center gap-3">
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

        <div className="mt-4">
          {detection ? (
            <TechnologyDetectionPanel result={detection} />
          ) : (
            <p className="text-sm text-muted-foreground">
              This project hasn&apos;t been analyzed yet. Run technology detection to see results.
            </p>
          )}
        </div>
      </div>

      <div>
        <h2 className="text-lg font-semibold">Architecture Analysis</h2>
        <div className="mt-3 flex items-center gap-3">
          <button
            type="button"
            onClick={handleRunArchitectureAnalysis}
            disabled={isAnalyzingArchitecture}
            className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-50"
          >
            {isAnalyzingArchitecture ? <Loader2 className="h-4 w-4 animate-spin" /> : <Network className="h-4 w-4" />}
            {isAnalyzingArchitecture
              ? 'Analyzing...'
              : architecture
                ? 'Re-run Architecture Analysis'
                : 'Run Architecture Analysis'}
          </button>
          {architectureError && <p className="text-sm text-destructive">{architectureError}</p>}
        </div>

        <div className="mt-4">
          {architecture ? (
            <ArchitectureAnalysisPanel result={architecture} />
          ) : (
            <p className="text-sm text-muted-foreground">
              This project hasn&apos;t had an architecture analysis yet. Run it to see the current and target
              architecture diagrams.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
