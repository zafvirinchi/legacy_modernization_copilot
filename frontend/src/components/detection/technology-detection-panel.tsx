import { Coffee, Database, Hammer, Server } from 'lucide-react';
import { StatCard } from '@/components/dashboard';
import { ConfidenceBar } from './confidence-bar';
import { TechnologyDetectionResult, TechnologyType } from '@/types';

const TECHNOLOGY_LABELS: Record<TechnologyType, string> = {
  SERVLET: 'Servlet',
  JSP: 'JSP',
  SPRING_MVC: 'Spring MVC',
  SPRING_XML: 'Spring XML',
  JDBC: 'JDBC',
  HIBERNATE: 'Hibernate',
  EJB: 'EJB',
  COBOL: 'COBOL',
  JCL: 'JCL',
  STRUTS: 'Struts',
};

interface TechnologyDetectionPanelProps {
  result: TechnologyDetectionResult;
}

export function TechnologyDetectionPanel({ result }: TechnologyDetectionPanelProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Java Version" value={result.javaVersion} icon={Coffee} />
        <StatCard label="Build Tool" value={result.buildTool} icon={Hammer} />
        <StatCard label="Application Server" value={result.applicationServer} icon={Server} />
        <StatCard
          label="Database"
          value={result.databases.length > 0 ? result.databases.join(', ') : 'Unknown'}
          icon={Database}
        />
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Detected Technologies</h3>
        {result.detectedTechnologies.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">
            No known legacy technologies were detected in this project.
          </p>
        ) : (
          <div className="mt-4 flex flex-col gap-4">
            {result.detectedTechnologies.map((detected) => (
              <ConfidenceBar
                key={detected.technology}
                label={TECHNOLOGY_LABELS[detected.technology]}
                score={detected.confidenceScore}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
