export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center p-24">
      <div className="z-10 w-full max-w-5xl items-center justify-between font-mono text-sm">
        <h1 className="mb-8 text-4xl font-bold">AI Legacy Modernization Copilot</h1>
        <p className="mb-12 text-lg text-muted-foreground">
          Analyze and modernize your legacy enterprise applications
        </p>
        <a
          href="/dashboard"
          className="inline-flex items-center justify-center rounded-md bg-primary px-8 py-2 text-white hover:bg-primary/90"
        >
          Get Started
        </a>
      </div>
    </main>
  );
}
