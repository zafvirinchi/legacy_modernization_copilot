'use client';

import { useEffect, useRef, useState, useId } from 'react';
import mermaid from 'mermaid';

let mermaidInitialized = false;

interface MermaidDiagramProps {
  diagram: string;
}

/**
 * Renders a Mermaid diagram string to SVG client-side. LLM-generated Mermaid
 * syntax isn't guaranteed valid, so rendering failures fall back to showing
 * the raw diagram source with an error message rather than a blank panel.
 */
export function MermaidDiagram({ diagram }: MermaidDiagramProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [error, setError] = useState<string | null>(null);
  const id = useId().replace(/[^a-zA-Z0-9]/g, '');

  useEffect(() => {
    let cancelled = false;

    if (!mermaidInitialized) {
      mermaid.initialize({ startOnLoad: false, theme: 'neutral', securityLevel: 'strict' });
      mermaidInitialized = true;
    }

    setError(null);

    mermaid
      .render(`mermaid-diagram-${id}`, diagram)
      .then(({ svg }) => {
        if (!cancelled && containerRef.current) {
          containerRef.current.innerHTML = svg;
        }
      })
      .catch((renderError: unknown) => {
        if (!cancelled) {
          setError(renderError instanceof Error ? renderError.message : 'Failed to render diagram');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [diagram, id]);

  if (error) {
    return (
      <div className="rounded-md border border-destructive/30 bg-destructive/5 p-4">
        <p className="text-sm text-destructive">Could not render diagram: {error}</p>
        <pre className="mt-2 overflow-x-auto whitespace-pre-wrap text-xs text-muted-foreground">{diagram}</pre>
      </div>
    );
  }

  return <div ref={containerRef} className="overflow-x-auto rounded-md border border-border bg-card p-4" />;
}
