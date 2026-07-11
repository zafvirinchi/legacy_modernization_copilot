'use client';

export default function Error({ error, reset }: { error: Error & { digest?: string }; reset: () => void }) {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center">
      <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-6">
        <h2 className="mb-4 text-2xl font-bold text-destructive">Something went wrong!</h2>
        <p className="mb-6 text-destructive/80">{error.message}</p>
        <button
          onClick={() => reset()}
          className="inline-flex items-center justify-center rounded-md bg-destructive px-4 py-2 text-white hover:bg-destructive/90"
        >
          Try again
        </button>
      </div>
    </div>
  );
}
