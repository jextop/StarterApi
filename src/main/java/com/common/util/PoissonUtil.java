package com.common.util;

import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 * @author ding
 */
public class PoissonUtil {
    public static int getVariable(double lambda) {
        int k = 0;
        double p = Math.random();

        PoissonDistribution distribution = new PoissonDistribution(lambda);
        while (distribution.cumulativeProbability(k) < p) {
            k++;
        }
        return k;
    }

    public static double getProbability(int k, double lambda) {
        PoissonDistribution distribution = new PoissonDistribution(lambda);
        return distribution.probability(k);
    }
}
