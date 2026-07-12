'use client';

import { useState } from 'react';
import Link from 'next/link';
import { isAxiosError } from 'axios';
import { ProjectDropzone, UploadProgressBar, ProjectSummaryCard } from '@/components/upload';
import { projectService } from '@/services';
import { Project } from '@/types';

type UploadState = 'idle' | 'uploading' | 'success' | 'error';

export default function UploadPage() {
  const [state, setState] = useState<UploadState>('idle');
  const [fileName, setFileName] = useState('');
  const [progress, setProgress] = useState(0);
  const [project, setProject] = useState<Project | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFileSelected = async (file: File) => {
    if (!file.name.toLowerCase().endsWith('.zip')) {
      setError('Only ZIP archives are supported');
      return;
    }

    setError(null);
    setProject(null);
    setFileName(file.name);
    setProgress(0);
    setState('uploading');

    try {
      const uploaded = await projectService.upload(file, (progressEvent) => {
        if (progressEvent.total) {
          setProgress(Math.round((progressEvent.loaded / progressEvent.total) * 100));
        }
      });
      setProject(uploaded);
      setState('success');
    } catch (uploadError: unknown) {
      const message = isAxiosError<{ message?: string }>(uploadError)
        ? uploadError.response?.data?.message
        : undefined;
      setError(message ?? 'Failed to upload project');
      setState('error');
    }
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Upload Project</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Upload a ZIP archive of your legacy project for analysis
        </p>
      </div>

      <div className="max-w-xl">
        <ProjectDropzone onFileSelected={handleFileSelected} disabled={state === 'uploading'} error={error} />
      </div>

      {state === 'uploading' && (
        <div className="max-w-xl">
          <UploadProgressBar progress={progress} fileName={fileName} />
        </div>
      )}

      {state === 'success' && project && (
        <div className="max-w-xl">
          <ProjectSummaryCard project={project} />
          <p className="mt-3 text-sm">
            <Link href={`/projects/${project.id}`}>Run technology detection on this project &rarr;</Link>
          </p>
        </div>
      )}
    </div>
  );
}
