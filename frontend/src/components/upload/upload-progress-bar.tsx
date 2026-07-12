interface UploadProgressBarProps {
  progress: number;
  fileName: string;
}

export function UploadProgressBar({ progress, fileName }: UploadProgressBarProps) {
  return (
    <div className="rounded-lg border border-border bg-card p-4">
      <div className="flex items-center justify-between text-sm">
        <span className="truncate font-medium">{fileName}</span>
        <span className="text-muted-foreground">{progress}%</span>
      </div>
      <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-muted">
        <div
          className="h-full rounded-full bg-primary transition-all"
          style={{ width: `${progress}%` }}
        />
      </div>
    </div>
  );
}
