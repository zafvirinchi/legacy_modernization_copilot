export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center">
      <div className="text-center">
        <h2 className="mb-4 text-4xl font-bold">404</h2>
        <p className="mb-6 text-lg text-muted-foreground">Page not found</p>
        <a
          href="/dashboard"
          className="inline-flex items-center justify-center rounded-md bg-primary px-8 py-2 text-white hover:bg-primary/90"
        >
          Back to Dashboard
        </a>
      </div>
    </div>
  );
}
