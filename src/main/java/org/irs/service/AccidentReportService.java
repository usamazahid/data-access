package org.irs.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.irs.QueryStore.Report;
import org.irs.database.Datasources;
import org.irs.dto.AccidentReportRequestDTO;
import org.irs.dto.AccidentReportResponseDTO;
import org.irs.dto.AccidentStatisticsDTO;
import org.irs.dto.DriverDTO;
import org.irs.dto.ImageDTO;
import org.irs.dto.PassengerDTO;
import org.irs.dto.RequestDto;
import org.irs.dto.StatisticsRequestDTO;
import org.irs.dto.VehicleDTO;
import org.irs.dto.VehicleFitnessDTO;
import org.irs.dto.WitnessDTO;
import org.irs.util.ConstantValues;
import org.irs.util.GeneralMethods;
import org.irs.util.GeoUtils;
import org.irs.util.HaversineDistance;

import smile.clustering.DBSCAN;
import smile.math.distance.EuclideanDistance;

 



@RequestScoped
public class AccidentReportService {

    @Inject
    Datasources datasource;

    @Inject
    Report queryStore;

    @Inject
    GeneralMethods generalMethods;

    @Transactional
    public AccidentReportResponseDTO saveAccidentReport(AccidentReportRequestDTO reportDTO) {
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
        int totalRowsInserted = 0;
    
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement()) {
            // Handle image & audio files
            if (reportDTO.imageData != null && !reportDTO.imageData.isEmpty()) {
                String imagePath = generalMethods.saveBase64ToFile(reportDTO.imageData, "image", ".jpg");
                reportDTO.imageUri = imagePath;
            }
            if (reportDTO.audioData != null && !reportDTO.audioData.isEmpty()) {
                String audioPath = generalMethods.saveBase64ToFile(reportDTO.audioData, "audio", ".mp3");
                reportDTO.audioUri = audioPath;
            }
    
            // Save the main accident report
            String reportQuery = queryStore.getInsertAccidentReportQuery(reportDTO);
            int rowsAffected = stmt.executeUpdate(reportQuery, Statement.RETURN_GENERATED_KEYS);
            totalRowsInserted += rowsAffected;
    
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long reportId = rs.getLong(1);
                    responseDTO.id = String.valueOf(reportId);
    
                    // Insert related records and count rows
                    totalRowsInserted += insertRelatedRecords(reportId, reportDTO, stmt);
                    totalRowsInserted += insertReportImages(reportId, reportDTO, stmt);
                }
            }
    
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error saving accident report: " + ex.getMessage();
            throw new RuntimeException(ex); // Let Quarkus handle rollback
        }
    
        responseDTO.rowsInserted = String.valueOf(totalRowsInserted);
        return responseDTO;
    }
    
   
    private int insertRelatedRecords(Long reportId, AccidentReportRequestDTO reportDTO, Statement stmt) throws SQLException {
        int rowsInserted = 0;

        if (reportDTO.vehicles != null && !reportDTO.vehicles.isEmpty()) {
            for (VehicleDTO vehicle : reportDTO.vehicles) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertVehicleQuery(vehicle, reportId));
            }
        }
        if (reportDTO.drivers != null && !reportDTO.drivers.isEmpty()) {
            for (DriverDTO driver : reportDTO.drivers) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertDriverQuery(driver, reportId));
            }
        }
        if (reportDTO.casualties != null && !reportDTO.casualties.isEmpty()) {
            for (PassengerDTO passenger : reportDTO.casualties) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertPassengerQuery(passenger, reportId));
            }
        }
        if (reportDTO.witnesses != null && !reportDTO.witnesses.isEmpty()) {
            for (WitnessDTO witness : reportDTO.witnesses) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertWitnessQuery(witness, reportId));
            }
        }

        if (reportDTO.vehicleFitnessDetails != null && !reportDTO.vehicleFitnessDetails.isEmpty()) {
            for (VehicleFitnessDTO fitness : reportDTO.vehicleFitnessDetails) {
                String fitnessQuery = queryStore.getInsertVehicleFitnessQuery(fitness, reportId);
                rowsInserted += stmt.executeUpdate(fitnessQuery);
            }
        }
        if (reportDTO.followUp != null) {
            rowsInserted += stmt.executeUpdate(queryStore.getInsertFollowUpQuery(reportDTO.followUp, reportId));
        }
        if (reportDTO.evidence != null) {
            rowsInserted += stmt.executeUpdate(queryStore.getInsertEvidenceQuery(reportDTO.evidence, reportId));
        }

        return rowsInserted;
    }


    private int insertReportImages(Long reportId, AccidentReportRequestDTO reportDTO, Statement stmt) throws Exception {
        int rowsInserted = 0;

        if (reportDTO.imageDTOs != null && !reportDTO.imageDTOs.isEmpty()) {
            for (ImageDTO imageDTO : reportDTO.imageDTOs) {
                if (imageDTO.imageData != null && !imageDTO.imageData.isEmpty()) {
                    String imagePath = generalMethods.saveBase64ToFile(imageDTO.imageData, "image", ".jpg");
                    String imageQuery = queryStore.getInsertImageQuery(imagePath, reportId);
                    rowsInserted += stmt.executeUpdate(imageQuery);
                }
            }
        }

        return rowsInserted;
    }
    

    public AccidentReportResponseDTO getAccidentReportById(String reportId) {
        String query = queryStore.getSelectByReportId(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");  
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
    
        return responseDTO;
    }


    public List<AccidentReportResponseDTO> getAccidentHeatmapData(RequestDto requestDto) {
        String query =null;
        String interval =generalMethods.parseRangeToInterval(requestDto.range);
        if (requestDto.vehicleType != null || requestDto.accidentType != null || requestDto.startDate!=null || requestDto.endDate!=null
        || requestDto.severity!=null || (requestDto.swLat!=null && requestDto.swLng!=null && requestDto.neLat!=null && requestDto.neLng!=null)) {
                if(requestDto.range==null){
                    interval=null;
                }
                query = queryStore.getFilteredHeatMapDataWithLimit(
                    interval,
                    requestDto.limit,
                    requestDto.vehicleType,
                    requestDto.accidentType,
                    requestDto.startDate,
                    requestDto.endDate,
                    requestDto.severity,
                    requestDto.swLat,
                    requestDto.swLng,
                    requestDto.neLat,
                    requestDto.neLng
                );
            
        }
        else if(requestDto.range!=null && requestDto.limit!=null){
            query= queryStore.getHeatMapDataRangeLimit(interval, requestDto.limit);
        }else if(requestDto.limit!=null){
            query= queryStore.getHeatMapDataWithLimit(requestDto.limit);
        }else{
            query= queryStore.getHeatMapData(interval);
        }
      

        List<AccidentReportResponseDTO> accidentData = new ArrayList<>();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                AccidentReportResponseDTO accidentReportResponseDTO=new AccidentReportResponseDTO();
                accidentReportResponseDTO.id = String.valueOf(rs.getLong("report_id")); 
                accidentReportResponseDTO.latitude = rs.getDouble("latitude");
                accidentReportResponseDTO.longitude = rs.getDouble("longitude");
                accidentReportResponseDTO.severity = rs.getInt("severity");
                accidentData.add(accidentReportResponseDTO);
           
            } 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
        return accidentData;
    }


   

    public List<AccidentReportResponseDTO> getAccidentReportsByUserId(String userId) {
        String query = queryStore.getSelectByUserId(userId);
        List<AccidentReportResponseDTO> reports = new ArrayList<>();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.id = String.valueOf(rs.getLong("report_id")); 
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at"); 
            
                reports.add(responseDTO);
            }
    
            // If no reports found
            if (reports.isEmpty()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.error = "No reports found for the provided user ID.";
                responseDTO.rowsInserted = "0";
                reports.add(responseDTO);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exception and add an error response
            AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
            responseDTO.error = "Error fetching reports: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
            reports.add(responseDTO);
        }
    
        return reports;
    }

    public AccidentReportResponseDTO getJoinedAccidentReportById(String reportId) {
        String query = queryStore.getJoinedAccidentReportsById(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.vehicleLabel = rs.getString("vehicle_label");
                responseDTO.vehicleDescription = rs.getString("vehicle_description");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.victimLabel = rs.getString("victim_label");
                responseDTO.victimDescription = rs.getString("victim_description");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.accidentTypeLabel = rs.getString("accident_type_label");
                responseDTO.accidentTypeDescription = rs.getString("accident_type_description");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.reportedBy = rs.getString("reported_by");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
        System.out.println(responseDTO);
        return responseDTO;
    }
    public Map<String, Object> getJoinedAccidentReportsByUserId(String userId, Integer pageNumber, Integer recordsPerPage) {
        String query=null;
        if(pageNumber!=null && recordsPerPage!=null){
            int offset = (pageNumber - 1) * recordsPerPage;
            int limit = recordsPerPage + 1; // Fetch one extra record to check for more data
             query= queryStore.getJoinedAccidentReportsByUserIdWithPagination(userId, offset, limit);
        }else{
            query=queryStore.getJoinedAccidentReportsByUserId(userId);
        }   
       
    
        List<AccidentReportResponseDTO> reports = new ArrayList<>();
        boolean hasMoreData = false;
    
        try (Connection con = datasource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                if(rs.isLast()){
                    if(recordsPerPage!=null){
                        hasMoreData = rs.getRow() > recordsPerPage; // Check if there are more records
                        break;
                    }
                }
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.vehicleLabel = rs.getString("vehicle_label");
                responseDTO.vehicleDescription = rs.getString("vehicle_description");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.victimLabel = rs.getString("victim_label");
                responseDTO.victimDescription = rs.getString("victim_description");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.accidentTypeLabel = rs.getString("accident_type_label");
                responseDTO.accidentTypeDescription = rs.getString("accident_type_description");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.reportedBy = rs.getString("reported_by");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");
    
                reports.add(responseDTO);
            }
    
            // If no reports found
            if (reports.isEmpty()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.error = "No reports found for the provided user ID.";
                responseDTO.rowsInserted = "0";
                reports.add(responseDTO);
            }
    
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exception and add an error response
            AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
            responseDTO.error = "Error fetching reports: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
            reports.add(responseDTO);
        }
    
        // Prepare the response with reports and hasMoreData flag
        Map<String, Object> response = new HashMap<>();
        response.put("reports", reports);
        response.put("hasMoreData", hasMoreData);
    
        return response;
    }

    public String getFileBase64Response(String fileName){
        try {
            String filePath=ConstantValues.BASE_DIR+fileName;
            return  generalMethods.readFileAsBase64(filePath);
        } catch (Exception e) {
            Log.info(e);
            return null;
        }
    }

    public AccidentReportResponseDTO getReportFiles(String reportId) {
        String query = queryStore.getSelectByReportId(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));

                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri"); 
                    // Convert files to Base64 if they exist
                if (responseDTO.imageUri != null && !responseDTO.imageUri.isEmpty()) {
                    responseDTO.imageData = generalMethods.readFileAsBase64(responseDTO.imageUri);
                }
                if (responseDTO.audioUri != null && !responseDTO.audioUri.isEmpty()) {
                    responseDTO.audioData = generalMethods.readFileAsBase64(responseDTO.audioUri);
                }
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
    
        return responseDTO;
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
    
    private boolean isBlackSpot(int pointsCount, double totalSeverity, double dynamicThreshold) {
        double avgSeverity = totalSeverity / pointsCount;
    
        // Dynamic density threshold based on data
        if (pointsCount >= dynamicThreshold) return true;
    
        // Severity-based fallback logic
        if (pointsCount >= 3 && avgSeverity > 2.0) return true;
        if (pointsCount >= 2 && totalSeverity > 4.0) return true;
    
        return false;
    }

    private double calculateDensityThreshold(List<Integer> pointsPerCluster) {
        if (pointsPerCluster.isEmpty()) return 5;
    
        Collections.sort(pointsPerCluster);
        int index = (int) (0.75 * pointsPerCluster.size());
        return pointsPerCluster.get(index);
    }
    
    

    public List<Map<String, Object>> getClusteredAccidentsDBSCAN(RequestDto requestDto) {
        List<AccidentReportResponseDTO> accidents = getAccidentHeatmapData(requestDto);
        System.out.println("Total records fetched: " + accidents.size());

        if (accidents.isEmpty()) return Collections.emptyList();

        // Convert to double[][] for SMILE
        double[][] dataPoints = accidents.stream()
            .map(a -> new double[]{a.latitude, a.longitude})
            .toArray(double[][]::new);

        double epsilon = calculateEpsilon(accidents.size());
        int minPoints = calculateMinPoints(accidents.size());

        System.out.println("DBSCAN Parameters - Epsilon: " + epsilon + ", MinPoints: " + minPoints);

        DBSCAN<double[]> dbscan = DBSCAN.fit(dataPoints, new HaversineDistance(), minPoints, epsilon);

        // Group indices by cluster label
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        Map<Integer, Integer> clusterSizes = new HashMap<>();  // Keep track of size per cluster

        for (int i = 0; i < dbscan.y.length; i++) {
            int label = dbscan.y[i];
            if (label == -1) continue; // skip noise
            clusters.computeIfAbsent(label, k -> new ArrayList<>()).add(i);
            clusterSizes.put(label, clusterSizes.getOrDefault(label, 0) + 1);
        }

        // Now get list of cluster sizes directly:
        List<Integer> pointsPerCluster = new ArrayList<>(clusterSizes.values());

        double dynamicThreshold = calculateDensityThreshold(pointsPerCluster);


        System.out.println("Total clusters formed: " + clusters.size());

        return clusters.values().parallelStream()
            .map(indices -> {
                Map<String, Object> clusterData = new HashMap<>();
                double sumLat = 0, sumLon = 0, totalSeverity = 0;
                List<Map<String, Object>> clusterPoints = new ArrayList<>();

                for (int index : indices) {
                    AccidentReportResponseDTO acc = accidents.get(index);
                    sumLat += acc.latitude;
                    sumLon += acc.longitude;
                    totalSeverity += acc.severity;

                    clusterPoints.add(Map.of(
                        "id", acc.id,
                        "latitude", acc.latitude,
                        "longitude", acc.longitude,
                        "severity", acc.severity
                    ));
                }

                double centerLat = sumLat / indices.size();
                double centerLon = sumLon / indices.size();

                double radius = clusterPoints.stream()
                    .mapToDouble(p -> GeoUtils.calculateDistance(
                        centerLat, centerLon,
                        (double) p.get("latitude"), (double) p.get("longitude"))
                    ).max().orElse(0);

                clusterData.put("center", Map.of("latitude", centerLat, "longitude", centerLon));
                clusterData.put("points", clusterPoints);
                clusterData.put("pointsLength", clusterPoints.size());
                clusterData.put("totalSeverity", totalSeverity);
                clusterData.put("radius", radius);
                clusterData.put("isBlackSpot", isBlackSpot(clusterPoints.size(), totalSeverity,dynamicThreshold));

                return clusterData;
            }).collect(Collectors.toList());
    }

    
    // Helper methods
    public List<Cluster<DoublePoint>> getClusteredAccidents(RequestDto requestDto) {
      List<AccidentReportResponseDTO> accidents = getAccidentHeatmapData(requestDto);

    // Convert accidents to points
    List<DoublePoint> points = accidents.stream()
        .map(accident -> new DoublePoint(new double[]{accident.latitude, accident.longitude}))
        .collect(Collectors.toList());

    // Apply DBSCAN clustering
    DBSCANClusterer<DoublePoint> clusterer = new DBSCANClusterer<>(0.01, 3); // Adjust epsilon and min points
    return clusterer.cluster(points);
    }

    public AccidentStatisticsDTO getAccidentStatistics(StatisticsRequestDTO request) {
        AccidentStatisticsDTO statistics = new AccidentStatisticsDTO();
        
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement()) {
            if(request.range!=null){
                request.range=generalMethods.parseRangeToInterval(request.range);
            }
            // Get accident type distribution
            String accidentTypeQuery = queryStore.getAccidentTypeDistribution(
                request.startDate, request.endDate, request.range);
            try (ResultSet rs = stmt.executeQuery(accidentTypeQuery)) {
                List<AccidentStatisticsDTO.ChartDataPoint> accidentTypes = new ArrayList<>();
                while (rs.next()) {
                    AccidentStatisticsDTO.ChartDataPoint point = new AccidentStatisticsDTO.ChartDataPoint();
                    point.label = rs.getString("type_label");
                    point.count = rs.getInt("count");
                    point.avgSeverity = rs.getDouble("avg_severity");
                    accidentTypes.add(point);
                }
                statistics.accidentTypeDistribution = accidentTypes;
            }
            
            // Get vehicle type distribution
            String vehicleTypeQuery = queryStore.getVehicleTypeDistribution(
                request.startDate, request.endDate, request.range);
            try (ResultSet rs = stmt.executeQuery(vehicleTypeQuery)) {
                List<AccidentStatisticsDTO.ChartDataPoint> vehicleTypes = new ArrayList<>();
                while (rs.next()) {
                    AccidentStatisticsDTO.ChartDataPoint point = new AccidentStatisticsDTO.ChartDataPoint();
                    point.label = rs.getString("type_label");
                    point.count = rs.getInt("count");
                    point.avgSeverity = rs.getDouble("avg_severity");
                    vehicleTypes.add(point);
                }
                statistics.vehicleTypeDistribution = vehicleTypes;
            }
            
            // Get time-based trends
            String trendsQuery = queryStore.getAccidentTrends(
                request.interval != null ? request.interval : "month",
                request.startDate, request.endDate, request.range);
            System.out.println(trendsQuery);
            try (ResultSet rs = stmt.executeQuery(trendsQuery)) {
                List<AccidentStatisticsDTO.TimeSeriesDataPoint> trends = new ArrayList<>();
                while (rs.next()) {
                    AccidentStatisticsDTO.TimeSeriesDataPoint point = new AccidentStatisticsDTO.TimeSeriesDataPoint();
                    point.timePeriod = rs.getString("time_period");
                    point.totalCount = rs.getInt("total_count");
                    point.fatalCount = rs.getInt("fatal_count");
                    point.avgSeverity = rs.getDouble("avg_severity");
                    point.weatherRelatedCount = rs.getInt("weather_related_count");
                    point.roadConditionRelatedCount = rs.getInt("road_condition_related_count");
                    trends.add(point);
                }
                statistics.trends = trends;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error fetching accident statistics: " + ex.getMessage());
        }
        
        return statistics;
    }
}
