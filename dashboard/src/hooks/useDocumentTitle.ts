import { useEffect } from 'react';

/**
 * Custom hook to set document title dynamically.
 * Automatically appends " | IdaWhats" suffix.
 */
export function useDocumentTitle(title: string) {
  useEffect(() => {
    const previousTitle = document.title;
    document.title = `${title} | IdaWhats`;

    return () => {
      document.title = previousTitle;
    };
  }, [title]);
}
