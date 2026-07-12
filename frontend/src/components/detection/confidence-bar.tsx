interface ConfidenceBarProps {
  label: string;
  score: number;
}

export function ConfidenceBar({ label, score }: ConfidenceBarProps) {
  return (
    <div>
      <div className="flex items-center justify-between text-sm">
        <span className="font-medium">{label}</span>
        <span className="text-muted-foreground">{score}%</span>
      </div>
      <div className="mt-1.5 h-2 w-full overflow-hidden rounded-full bg-muted">
        <div className="h-full rounded-full bg-primary" style={{ width: `${score}%` }} />
      </div>
    </div>
  );
}
