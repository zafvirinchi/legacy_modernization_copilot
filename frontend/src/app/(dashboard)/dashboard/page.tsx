import { Metadata } from 'next';
import { FolderKanban, ScanSearch, AlertTriangle, FileText } from 'lucide-react';
import { StatCard, SystemStatusCard } from '@/components/dashboard';

export const metadata: Metadata = {
  title: 'Dashboard | AI Legacy Modernization Copilot',
  description: 'Dashboard for managing legacy modernization projects',
};

export default function DashboardPage() {
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
        <StatCard label="Projects" value={0} icon={FolderKanban} />
        <StatCard label="Scans" value={0} icon={ScanSearch} />
        <StatCard label="Open Issues" value={0} icon={AlertTriangle} />
        <StatCard label="Reports" value={0} icon={FileText} />
      </div>
    </div>
  );
}
