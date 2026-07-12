import { Boxes, Target } from 'lucide-react';
import { StatCard } from '@/components/dashboard';
import { ConfidenceBar } from '@/components/detection';
import { MermaidDiagram } from './mermaid-diagram';
import { ArchitectureAnalysisResult, ArchitecturePattern } from '@/types';

const PATTERN_LABELS: Record<ArchitecturePattern, string> = {
  MONOLITH: 'Monolith',
  MVC: 'MVC',
  LAYERED: 'Layered Architecture',
  CLIENT_SERVER: 'Client-Server',
  MICROSERVICE: 'Microservice',
};

interface ArchitectureAnalysisPanelProps {
  result: ArchitectureAnalysisResult;
}

export function ArchitectureAnalysisPanel({ result }: ArchitectureAnalysisPanelProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <StatCard label="Detected Architecture" value={PATTERN_LABELS[result.detectedPattern]} icon={Boxes} />
        <StatCard label="Target Architecture" value={PATTERN_LABELS[result.targetArchitecturePattern]} icon={Target} />
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <ConfidenceBar label="Architecture Score" score={result.architectureScore} />
        <p className="mt-3 text-sm text-muted-foreground">{result.architectureScoreJustification}</p>
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Current Architecture</h3>
        <p className="mt-2 text-sm text-muted-foreground">{result.currentArchitectureDescription}</p>
        <div className="mt-4">
          <MermaidDiagram diagram={result.currentArchitectureDiagram} />
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Recommendations</h3>
        {result.recommendations.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">No recommendations were generated.</p>
        ) : (
          <ul className="mt-2 list-disc space-y-1 pl-5 text-sm">
            {result.recommendations.map((recommendation) => (
              <li key={recommendation}>{recommendation}</li>
            ))}
          </ul>
        )}
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Target Architecture</h3>
        <p className="mt-2 text-sm text-muted-foreground">{result.targetArchitectureDescription}</p>
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Migration Path</h3>
        <div className="mt-4">
          <MermaidDiagram diagram={result.migrationDiagram} />
        </div>
      </div>
    </div>
  );
}
