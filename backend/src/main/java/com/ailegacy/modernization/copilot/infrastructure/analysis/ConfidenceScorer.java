package com.ailegacy.modernization.copilot.infrastructure.analysis;

/**
 * Deterministic confidence scoring shared by every technology rule.
 *
 * Each distinct signal type that matched is strong, independent evidence and is
 * worth more than repeated matches of the same signal, so distinct signals are
 * weighted heavily (40 points each) while extra occurrences of already-matched
 * signals only add a small amount (5 points each, capped at 30) - this rewards
 * breadth of evidence over sheer file count without needing per-technology tuning.
 */
final class ConfidenceScorer {

    private static final int POINTS_PER_DISTINCT_SIGNAL = 40;
    private static final int POINTS_PER_EXTRA_OCCURRENCE = 5;
    private static final int MAX_EXTRA_OCCURRENCE_POINTS = 30;
    private static final int MAX_SCORE = 100;

    private ConfidenceScorer() {
    }

    static int score(int distinctSignalsMatched, int totalOccurrences) {
        if (distinctSignalsMatched == 0) {
            return 0;
        }
        int extraOccurrences = Math.max(0, totalOccurrences - distinctSignalsMatched);
        int extraPoints = Math.min(MAX_EXTRA_OCCURRENCE_POINTS, extraOccurrences * POINTS_PER_EXTRA_OCCURRENCE);
        return Math.min(MAX_SCORE, distinctSignalsMatched * POINTS_PER_DISTINCT_SIGNAL + extraPoints);
    }

}
