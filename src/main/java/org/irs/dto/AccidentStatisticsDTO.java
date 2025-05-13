package org.irs.dto;

import java.util.List;

public class AccidentStatisticsDTO {
    // For pie charts
    public static class ChartDataPoint {
        public String label;
        public int count;
        public double avgSeverity;
    }

    // For time series data
    public static class TimeSeriesDataPoint {
        public String timePeriod;
        public int totalCount;
        public int fatalCount;
        public double avgSeverity;
        public int weatherRelatedCount;
        public int roadConditionRelatedCount;
    }

    // For overall statistics
    public List<ChartDataPoint> accidentTypeDistribution;
    public List<ChartDataPoint> vehicleTypeDistribution;
    public List<TimeSeriesDataPoint> trends;
    public List<TimeSeriesDataPoint> fatalAccidents;
}