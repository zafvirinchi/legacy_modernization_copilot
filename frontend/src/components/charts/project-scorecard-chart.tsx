'use client';

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

interface ProjectScorecardChartProps {
  architectureScore?: number;
  securityRiskScore?: number;
  performanceScore?: number;
}

const TOOLTIP_STYLE = {
  background: 'hsl(var(--card))',
  border: '1px solid hsl(var(--border))',
  borderRadius: '6px',
  color: 'hsl(var(--foreground))',
  fontSize: '12px',
};

/**
 * Compares Architecture / Security / Performance on a single 0-100 "higher is
 * better" scale. Security's risk score is inverted to a "health" score so all
 * three bars share the same direction of meaning. Only metrics that have
 * actually been analyzed are shown - categories carry identity via the axis,
 * so a single hue is used rather than a categorical palette.
 */
export function ProjectScorecardChart({
  architectureScore,
  securityRiskScore,
  performanceScore,
}: ProjectScorecardChartProps) {
  const data = [
    architectureScore !== undefined && { name: 'Architecture', score: architectureScore },
    securityRiskScore !== undefined && { name: 'Security', score: 100 - securityRiskScore },
    performanceScore !== undefined && { name: 'Performance', score: performanceScore },
  ].filter((entry): entry is { name: string; score: number } => Boolean(entry));

  if (data.length === 0) {
    return <p className="text-sm text-muted-foreground">Run an analysis to see the scorecard.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={220}>
      <BarChart data={data} margin={{ top: 8, right: 16, bottom: 4, left: 4 }}>
        <CartesianGrid vertical={false} stroke="hsl(var(--border))" />
        <XAxis dataKey="name" stroke="hsl(var(--muted-foreground))" fontSize={12} tickLine={false} />
        <YAxis domain={[0, 100]} stroke="hsl(var(--muted-foreground))" fontSize={12} />
        <Tooltip
          cursor={{ fill: 'hsl(var(--muted))' }}
          contentStyle={TOOLTIP_STYLE}
          formatter={(value: number) => [`${value}/100`, 'Score']}
        />
        <Bar dataKey="score" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} maxBarSize={56} />
      </BarChart>
    </ResponsiveContainer>
  );
}
