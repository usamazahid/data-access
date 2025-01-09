package org.irs.QueryStore;

public class Dispatch {

    // Ambulance Queries
    public static final String UPDATE_AMBULANCE_STATUS = "UPDATE public.ambulance SET status = ? WHERE ambulance_id = ?";

    // Dispatch Queries
    public static final String INSERT_DISPATCH = """
        INSERT INTO public.dispatch 
        ( report_id, ambulance_id, driver_id, assigned_by, pickup_time, drop_time, latitude, longitude, drop_location, hospital_id, status) 
        VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    public static final String SELECT_DISPATCH_BY_DRIVER = """
        SELECT dispatch_id, report_id, ambulance_id, driver_id, assigned_by, pickup_time, drop_time, latitude, longitude, drop_location, hospital_id, status
        FROM public.dispatch 
        WHERE driver_id = ? AND status != 'DISPATCHED'
    """;

    public static final String UPDATE_DISPATCH_STATUS = "UPDATE public.dispatch SET status = ? WHERE dispatch_id = ?";
    public static final String UPDATE_DISPATCH_PICKUP = "UPDATE public.dispatch SET status = 'PICKED',pickup_time = CURRENT_TIMESTAMP  WHERE dispatch_id = ?";
    public static final String UPDATE_DISPATCH_DROP = """
        UPDATE public.dispatch 
        SET drop_time = CURRENT_TIMESTAMP, status = 'DISPATCHED' ,drop_location = ?, latitude=?, longitude=?, hospital_id = ? 
        WHERE dispatch_id = ?
    """;

    // Accident Reports Queries
    public static final String UPDATE_ACCIDENT_REPORT_STATUS = "UPDATE public.accident_reports SET status = ? WHERE report_id = ?";
}
