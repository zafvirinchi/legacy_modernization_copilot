package com.ailegacy.modernization.copilot.infrastructure.analysis;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfidenceScorerTest {

    @Test
    void noSignalsMatchedScoresZero() {
        assertThat(ConfidenceScorer.score(0, 0)).isZero();
    }

    @Test
    void oneDistinctSignalScoresBaseWeight() {
        assertThat(ConfidenceScorer.score(1, 1)).isEqualTo(40);
    }

    @Test
    void twoDistinctSignalsScoreDouble() {
        assertThat(ConfidenceScorer.score(2, 2)).isEqualTo(80);
    }

    @Test
    void threeOrMoreDistinctSignalsCapAt100() {
        assertThat(ConfidenceScorer.score(3, 3)).isEqualTo(100);
        assertThat(ConfidenceScorer.score(5, 5)).isEqualTo(100);
    }

    @Test
    void extraOccurrencesOfSameSignalAddSmallBonusCappedAt30() {
        // 1 distinct signal matched 20 times: base 40 + capped extra bonus 30 = 70
        assertThat(ConfidenceScorer.score(1, 20)).isEqualTo(70);
    }

}
