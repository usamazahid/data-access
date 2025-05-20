package org.irs.util;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConstantValues {
    public static final String BASE_DIR = "/external-storage/accident-reports/";
    public static final double FUZZY_THRESHOLD = 0.85;
    public static final Map<String, String> QUESTION_SQL_MAP;
    
    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put(
            "Total number of accidents",
            "SELECT COUNT(*) AS total_accidents FROM accident_reports"
        );
        m.put(
            "Count of accidents with more than 3 casualties",
            "SELECT COUNT(*) AS count_more_than_3 FROM accident_reports WHERE num_affecties > 3"
        );
        m.put(
            "Number of accidents involving pedestrians",
            "SELECT COUNT(*) AS pedestrian_accidents FROM accident_reports ar JOIN vehicle_involved vi ON ar.vehicle_involved_id = vi.id WHERE vi.label = 'Pedestrian'"
        );
        m.put(
            "Count accidents by weather condition",
            "SELECT wc.condition AS weather, COUNT(*) AS count FROM accident_reports ar JOIN weather_condition wc ON ar.weather_condition = wc.id GROUP BY wc.condition ORDER BY count DESC"
        );
        m.put(
            "What areas have the highest number of accidents?",
            "SELECT accident_location, COUNT(*) AS total FROM accident_reports GROUP BY accident_location ORDER BY total DESC LIMIT 5"
        );
        m.put(
            "Number of accidents that occurred during rainy weather",
            "SELECT COUNT(*) AS rainy_accidents FROM accident_reports ar JOIN weather_condition wc ON ar.weather_condition = wc.id WHERE wc.condition = 'Rain'"
        );
        m.put(
            "Vehicle type involved in the most accidents",
            "SELECT vi.label AS vehicle_type, COUNT(*) AS count FROM accident_reports ar JOIN vehicle_involved vi ON ar.vehicle_involved_id = vi.id GROUP BY vi.label ORDER BY count DESC LIMIT 1"
        );
        m.put(
            "Count of accidents on roads with poor visibility",
            "SELECT COUNT(*) AS poor_visibility_accidents FROM accident_reports ar JOIN visibility v ON ar.visibility = v.id WHERE v.level = 'Poor'"
        );
        m.put(
            "Monthly trend of accidents in the past year",
            "SELECT DATE_TRUNC('month', created_at) AS month, COUNT(*) AS count FROM accident_reports WHERE created_at >= NOW() - INTERVAL '1 year' GROUP BY month ORDER BY month"
        );
        m.put(
            "Average number of casualties per accident type",
            "SELECT at.label AS accident_type, AVG(ar.num_affecties) AS avg_casualties FROM accident_reports ar JOIN accident_types at ON ar.accident_type_id = at.id GROUP BY at.label ORDER BY avg_casualties DESC"
        );
        m.put(
            "Gender distribution of drivers in reported accidents",
            "SELECT gt.label AS gender, COUNT(*) AS count FROM accident_reports ar JOIN gender_types gt ON ar.gender = gt.id GROUP BY gt.label"
        );
        m.put(
            "Top 5 accident locations by frequency",
            "SELECT accident_location, COUNT(*) AS total FROM accident_reports GROUP BY accident_location ORDER BY total DESC LIMIT 5"
        );
        m.put(
            "Count of accidents by road surface condition",
            "SELECT rsc.condition, COUNT(*) AS count FROM accident_reports ar JOIN road_surface_condition rsc ON ar.road_surface_condition = rsc.id GROUP BY rsc.condition ORDER BY count DESC"
        );
        m.put(
            "Count of accidents where fitness certificate was expired",
            "SELECT COUNT(*) AS expired_fitness_accidents FROM accident_reports ar JOIN vehicle_details vd ON ar.report_id = vd.report_id WHERE vd.fitness_certificate_status = 'Expired'"
        );
        m.put(
            "Count of \"Hit and Run\" accidents",
            "SELECT COUNT(*) AS hit_and_run_accidents FROM accident_reports ar JOIN accident_types at ON ar.accident_type_id = at.id WHERE at.label = 'Hit and Run'"
        );
        
        QUESTION_SQL_MAP = Collections.unmodifiableMap(m);
    }
    
    private ConstantValues() {
        // prevent instantiation
    }
}
