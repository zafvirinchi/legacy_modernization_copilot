'use client';

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Project } from '@/types';

interface ProjectFilesChartProps {
  projects: Project[];
  maxProjects?: number;
}

const TOOLTIP_STYLE = {
  background: 'hsl(var(--card))',
  border: '1px solid hsl(var(--border))',
  borderRadius: '6px',
  color: 'hsl(var(--foreground))',
  fontSize: '12px',
};

/**
 * Horizontal bar chart of file counts per project, most recent first.
 * Single-hue magnitude encoding - project names on the axis already carry
 * identity, so no categorical palette is needed.
 */
export function ProjectFilesChart({ projects, maxProjects = 8 }: ProjectFilesChartProps) {
  const data = projects.slice(0, maxProjects).map((project) => ({
    name: project.name.length > 22 ? `${project.name.slice(0, 22)}...` : project.name,
    files: project.totalFiles,
  }));

  if (data.length === 0) {
    return <p className="text-sm text-muted-foreground">No projects to chart yet.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={Math.max(160, data.length * 36)}>
      <BarChart data={data} layout="vertical" margin={{ top: 4, right: 24, bottom: 4, left: 4 }}>
        <CartesianGrid horizontal={false} stroke="hsl(var(--border))" />
        <XAxis type="number" allowDecimals={false} stroke="hsl(var(--muted-foreground))" fontSize={12} />
        <YAxis
          type="category"
          dataKey="name"
          width={140}
          stroke="hsl(var(--muted-foreground))"
          fontSize={12}
          tickLine={false}
        />
        <Tooltip
          cursor={{ fill: 'hsl(var(--muted))' }}
          contentStyle={TOOLTIP_STYLE}
          formatter={(value: number) => [`${value} files`, 'Files']}
        />
        <Bar dataKey="files" fill="hsl(var(--primary))" radius={[0, 4, 4, 0]} maxBarSize={24} />
      </BarChart>
    </ResponsiveContainer>
  );
}
