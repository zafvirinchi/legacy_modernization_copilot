'use client';

import { useEffect, useState } from 'react';
import { FolderKanban, ScanSearch, AlertTriangle, FileText } from 'lucide-react';
import { StatCard, SystemStatusCard } from '@/components/dashboard';
import { projectService } from '@/services';

export default function DashboardPage() {
  const [projectCount, setProjectCount] = useState(0);

  useEffect(() => {
    projectService
      .list()
      .then((projects) => setProjectCount(projects.length))
      .catch(() => setProjectCount(0));
  }, []);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold">Dashboard</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Overview of your legacy modernization projects
        </p>
      </div>

      <SystemStatusCard />

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Projects" value={projectCount} icon={FolderKanban} />
        <StatCard label="Scans" value={0} icon={ScanSearch} />
        <StatCard label="Open Issues" value={0} icon={AlertTriangle} />
        <StatCard label="Reports" value={0} icon={FileText} />
      </div>
    </div>
  );
}
