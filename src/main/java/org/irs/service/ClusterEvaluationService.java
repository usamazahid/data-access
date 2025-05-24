package org.irs.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.irs.dto.AccidentReportResponseDTO;
import org.irs.dto.RequestDto;

import smile.clustering.DBSCAN;

import java.util.*;
import java.util.stream.Collectors;

import org.irs.util.HaversineDistance;

@ApplicationScoped
public class ClusterEvaluationService {

    @Inject
    AccidentReportService accidentReportService;

    public Map<String, Object> evaluateClustering(RequestDto requestDto) {
        List<AccidentReportResponseDTO> accidents = accidentReportService.getAccidentHeatmapData(requestDto);
        if (accidents.isEmpty()) {
            return createErrorResponse("No accident data available for evaluation");
        }

        // Convert to double[][] for SMILE
        double[][] dataPoints = accidents.stream()
            .map(a -> new double[]{a.latitude, a.longitude})
            .toArray(double[][]::new);

        // Get DBSCAN parameters
        double epsilon = calculateEpsilon(accidents.size());
        int minPoints = calculateMinPoints(accidents.size());

        // Run DBSCAN
        DBSCAN<double[]> dbscan = DBSCAN.fit(dataPoints, new HaversineDistance(), minPoints, epsilon);

        // Calculate evaluation metrics
        Map<String, Object> evaluationResults = new HashMap<>();
        
        // 1. Basic Statistics
        evaluationResults.put("totalPoints", accidents.size());
        evaluationResults.put("epsilon", epsilon);
        evaluationResults.put("minPoints", minPoints);
        
        // 2. Cluster Statistics
        int numClusters = (int) Arrays.stream(dbscan.y).filter(label -> label != -1).distinct().count();
        long noisePoints = Arrays.stream(dbscan.y).filter(label -> label == -1).count();
        double noiseRatio = (double) noisePoints / accidents.size();
        
        evaluationResults.put("numClusters", numClusters);
        evaluationResults.put("noisePoints", noisePoints);
        evaluationResults.put("noiseRatio", noiseRatio);

        // 3. Calculate Silhouette Score
        double silhouetteScore = calculateSilhouetteScore(dataPoints, dbscan.y);
        evaluationResults.put("silhouetteScore", silhouetteScore);

        // 4. Calculate Davies-Bouldin Index
        double daviesBouldinIndex = calculateDaviesBouldinIndex(dataPoints, dbscan.y);
        evaluationResults.put("daviesBouldinIndex", daviesBouldinIndex);

        // 5. Cluster Quality Metrics
        Map<String, Object> clusterQuality = calculateClusterQualityMetrics(accidents, dbscan.y);
        evaluationResults.put("clusterQuality", clusterQuality);

        // 6. Parameter Sensitivity Analysis
        Map<String, Object> parameterSensitivity = analyzeParameterSensitivity(dataPoints);
        evaluationResults.put("parameterSensitivity", parameterSensitivity);

        return evaluationResults;
    }

    private double calculateSilhouetteScore(double[][] dataPoints, int[] labels) {
        double totalScore = 0;
        int validPoints = 0;

        for (int i = 0; i < dataPoints.length; i++) {
            if (labels[i] == -1) continue; // Skip noise points

            double a = calculateAverageDistance(dataPoints[i], dataPoints, labels, labels[i]);
            double b = calculateNearestClusterDistance(dataPoints[i], dataPoints, labels, labels[i]);
            
            if (b > a) {
                totalScore += (b - a) / Math.max(a, b);
                validPoints++;
            }
        }

        return validPoints > 0 ? totalScore / validPoints : 0;
    }

    private double calculateAverageDistance(double[] point, double[][] allPoints, int[] labels, int clusterLabel) {
        return Arrays.stream(allPoints)
            .filter(p -> !Arrays.equals(p, point))
            .filter(p -> labels[Arrays.asList(allPoints).indexOf(p)] == clusterLabel)
            .mapToDouble(p -> new HaversineDistance().d(point, p))
            .average()
            .orElse(0);
    }

    private double calculateNearestClusterDistance(double[] point, double[][] allPoints, int[] labels, int currentCluster) {
        return Arrays.stream(labels)
            .distinct()
            .filter(label -> label != -1 && label != currentCluster)
            .mapToDouble(label -> calculateAverageDistance(point, allPoints, labels, label))
            .min()
            .orElse(Double.MAX_VALUE);
    }

    private double calculateDaviesBouldinIndex(double[][] dataPoints, int[] labels) {
        Set<Integer> clusterLabels = Arrays.stream(labels)
            .filter(label -> label != -1)
            .boxed()
            .collect(Collectors.toSet());

        if (clusterLabels.size() <= 1) return 0;

        double totalScore = 0;
        for (int i : clusterLabels) {
            double maxRatio = 0;
            for (int j : clusterLabels) {
                if (i == j) continue;

                double ratio = (calculateClusterScatter(dataPoints, labels, i) + 
                              calculateClusterScatter(dataPoints, labels, j)) /
                             calculateClusterDistance(dataPoints, labels, i, j);
                maxRatio = Math.max(maxRatio, ratio);
            }
            totalScore += maxRatio;
        }

        return totalScore / clusterLabels.size();
    }

    private double calculateClusterScatter(double[][] dataPoints, int[] labels, int clusterLabel) {
        double[] centroid = calculateClusterCentroid(dataPoints, labels, clusterLabel);
        return Arrays.stream(dataPoints)
            .filter(p -> labels[Arrays.asList(dataPoints).indexOf(p)] == clusterLabel)
            .mapToDouble(p -> new HaversineDistance().d(p, centroid))
            .average()
            .orElse(0);
    }

    private double[] calculateClusterCentroid(double[][] dataPoints, int[] labels, int clusterLabel) {
        List<double[]> clusterPoints = Arrays.stream(dataPoints)
            .filter(p -> labels[Arrays.asList(dataPoints).indexOf(p)] == clusterLabel)
            .collect(Collectors.toList());

        if (clusterPoints.isEmpty()) return new double[]{0, 0};

        double[] centroid = new double[2];
        for (double[] point : clusterPoints) {
            centroid[0] += point[0];
            centroid[1] += point[1];
        }
        centroid[0] /= clusterPoints.size();
        centroid[1] /= clusterPoints.size();
        return centroid;
    }

    private double calculateClusterDistance(double[][] dataPoints, int[] labels, int cluster1, int cluster2) {
        double[] centroid1 = calculateClusterCentroid(dataPoints, labels, cluster1);
        double[] centroid2 = calculateClusterCentroid(dataPoints, labels, cluster2);
        return new HaversineDistance().d(centroid1, centroid2);
    }

    private Map<String, Object> calculateClusterQualityMetrics(List<AccidentReportResponseDTO> accidents, int[] labels) {
        Map<Integer, List<AccidentReportResponseDTO>> clusters = new HashMap<>();
        
        // Group accidents by cluster
        for (int i = 0; i < accidents.size(); i++) {
            if (labels[i] != -1) {
                clusters.computeIfAbsent(labels[i], k -> new ArrayList<>()).add(accidents.get(i));
            }
        }

        Map<String, Object> qualityMetrics = new HashMap<>();
        
        // Calculate metrics for each cluster
        List<Map<String, Object>> clusterMetrics = new ArrayList<>();
        for (Map.Entry<Integer, List<AccidentReportResponseDTO>> entry : clusters.entrySet()) {
            List<AccidentReportResponseDTO> clusterAccidents = entry.getValue();
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("clusterId", entry.getKey());
            metrics.put("size", clusterAccidents.size());
            
            // Calculate average severity
            double avgSeverity = clusterAccidents.stream()
                .mapToDouble(a -> a.severity)
                .average()
                .orElse(0);
            metrics.put("averageSeverity", avgSeverity);
            
            // Calculate cluster radius
            double[] centroid = calculateClusterCentroid(
                clusterAccidents.stream()
                    .map(a -> new double[]{a.latitude, a.longitude})
                    .toArray(double[][]::new),
                new int[clusterAccidents.size()],
                0
            );
            
            double radius = clusterAccidents.stream()
                .mapToDouble(a -> new HaversineDistance().d(
                    new double[]{a.latitude, a.longitude},
                    centroid
                ))
                .max()
                .orElse(0);
            metrics.put("radius", radius);
            
            clusterMetrics.add(metrics);
        }
        
        qualityMetrics.put("clusters", clusterMetrics);
        return qualityMetrics;
    }

    private Map<String, Object> analyzeParameterSensitivity(double[][] dataPoints) {
        Map<String, Object> sensitivity = new HashMap<>();
        List<Map<String, Object>> epsilonResults = new ArrayList<>();
        List<Map<String, Object>> minPointsResults = new ArrayList<>();

        // Test different epsilon values
        double[] epsilonValues = {0.1, 0.2, 0.3, 0.4, 0.5};
        for (double eps : epsilonValues) {
            DBSCAN<double[]> dbscan = DBSCAN.fit(dataPoints, new HaversineDistance(), 5, eps);
            Map<String, Object> result = new HashMap<>();
            result.put("epsilon", eps);
            result.put("numClusters", (int) Arrays.stream(dbscan.y).filter(label -> label != -1).distinct().count());
            result.put("noiseRatio", (double) Arrays.stream(dbscan.y).filter(label -> label == -1).count() / dataPoints.length);
            epsilonResults.add(result);
        }

        // Test different minPoints values
        int[] minPointsValues = {3, 5, 8, 10, 15};
        for (int minPts : minPointsValues) {
            DBSCAN<double[]> dbscan = DBSCAN.fit(dataPoints, new HaversineDistance(), minPts, 0.3);
            Map<String, Object> result = new HashMap<>();
            result.put("minPoints", minPts);
            result.put("numClusters", (int) Arrays.stream(dbscan.y).filter(label -> label != -1).distinct().count());
            result.put("noiseRatio", (double) Arrays.stream(dbscan.y).filter(label -> label == -1).count() / dataPoints.length);
            minPointsResults.add(result);
        }

        sensitivity.put("epsilonSensitivity", epsilonResults);
        sensitivity.put("minPointsSensitivity", minPointsResults);
        return sensitivity;
    }

    private double calculateEpsilon(int dataSize) {
        if (dataSize < 100) return 0.2;   // 200 meters
        else if (dataSize < 1000) return 0.3; // 300 meters
        else return 0.5;   // 500 meters
    }

    private int calculateMinPoints(int dataSize) {
        if (dataSize < 100) return 3;
        else if (dataSize < 1000) return 5;
        else return 8;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
} 