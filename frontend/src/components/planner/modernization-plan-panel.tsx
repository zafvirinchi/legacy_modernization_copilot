import { Check, Clock, X } from 'lucide-react';
import { StatCard } from '@/components/dashboard';
import { LevelBadge } from './level-badge';
import { ModernizationPlan, ModernTechnology } from '@/types';

const TECHNOLOGY_LABELS: Record<ModernTechnology, string> = {
  SPRING_BOOT: 'Spring Boot',
  SPRING_SECURITY: 'Spring Security',
  DOCKER: 'Docker',
  KUBERNETES: 'Kubernetes',
  KAFKA: 'Kafka',
  REDIS: 'Redis',
  OPENAPI: 'OpenAPI',
  CLOUD_MIGRATION: 'Cloud Migration',
};

interface ModernizationPlanPanelProps {
  plan: ModernizationPlan;
}

export function ModernizationPlanPanel({ plan }: ModernizationPlanPanelProps) {
  return (
    <div className="flex flex-col gap-4">
      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Migration Strategy</h3>
        <p className="mt-2 text-sm text-muted-foreground">{plan.migrationStrategy}</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <StatCard label="Estimated Timeline" value={plan.estimatedTimeline} icon={Clock} />
        <div className="flex items-center justify-between rounded-lg border border-border bg-card p-4">
          <p className="text-sm text-muted-foreground">Migration Complexity</p>
          <LevelBadge level={plan.migrationComplexity} />
        </div>
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Priority Matrix</h3>
        {plan.priorityMatrix.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">No priority items were generated.</p>
        ) : (
          <div className="mt-4 overflow-hidden rounded-md border border-border">
            <table className="w-full text-sm">
              <thead className="bg-muted/50 text-left text-muted-foreground">
                <tr>
                  <th className="px-4 py-2 font-medium">Item</th>
                  <th className="px-4 py-2 font-medium">Impact</th>
                  <th className="px-4 py-2 font-medium">Effort</th>
                </tr>
              </thead>
              <tbody>
                {plan.priorityMatrix.map((row) => (
                  <tr key={row.item} className="border-t border-border">
                    <td className="px-4 py-2">{row.item}</td>
                    <td className="px-4 py-2">
                      <LevelBadge level={row.impact} />
                    </td>
                    <td className="px-4 py-2">
                      <LevelBadge level={row.effort} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Quick Wins</h3>
        {plan.quickWins.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">No quick wins were identified.</p>
        ) : (
          <ul className="mt-2 list-disc space-y-1 pl-5 text-sm">
            {plan.quickWins.map((win) => (
              <li key={win}>{win}</li>
            ))}
          </ul>
        )}
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Risks</h3>
        {plan.risks.length === 0 ? (
          <p className="mt-2 text-sm text-muted-foreground">No risks were identified.</p>
        ) : (
          <ul className="mt-3 flex flex-col gap-2">
            {plan.risks.map((risk) => (
              <li key={risk.description} className="flex items-center justify-between gap-3 text-sm">
                <span>{risk.description}</span>
                <LevelBadge level={risk.severity} />
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="rounded-lg border border-border bg-card p-6">
        <h3 className="font-semibold">Required Technologies</h3>
        <div className="mt-3 grid grid-cols-1 gap-3 sm:grid-cols-2">
          {plan.requiredTechnologies.map((tech) => (
            <div key={tech.technology} className="flex gap-3 rounded-md border border-border p-3">
              {tech.recommended ? (
                <Check className="mt-0.5 h-4 w-4 shrink-0 text-emerald-500" />
              ) : (
                <X className="mt-0.5 h-4 w-4 shrink-0 text-muted-foreground" />
              )}
              <div>
                <p className="text-sm font-medium">{TECHNOLOGY_LABELS[tech.technology]}</p>
                <p className="text-xs text-muted-foreground">{tech.reason}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
