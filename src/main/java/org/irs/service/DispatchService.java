
package org.irs.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.irs.QueryStore.Dispatch;
import org.irs.database.Datasources;
import org.irs.dto.DispatchRequestDto;
import org.irs.dto.DispatchResponseDto;
import org.irs.util.GeneralMethods;

import io.quarkus.logging.Log;

@RequestScoped
public class DispatchService {

    @Inject
    Datasources datasource;

    @Inject
    GeneralMethods generalMethods;

    // 1. Insert Dispatch and Update Statuses
    public Response createDispatch(DispatchRequestDto dispatchDTO) {
        Connection connection = null;

        try {
            connection = datasource.getConnection();
            connection.setAutoCommit(false);

            // Update Ambulance Status
            try (PreparedStatement updateAmbulanceStmt = connection.prepareStatement(Dispatch.UPDATE_AMBULANCE_STATUS)) {
                updateAmbulanceStmt.setString(1, "PROGRESS");
                updateAmbulanceStmt.setInt(2, dispatchDTO.getAmbulanceId());
                updateAmbulanceStmt.executeUpdate();
            }

            // Insert Dispatch Record
            try (PreparedStatement insertDispatchStmt = connection.prepareStatement(Dispatch.INSERT_DISPATCH)) {
                // insertDispatchStmt.setInt(1, dispatchDTO.getDispatchId());
                insertDispatchStmt.setInt(1, dispatchDTO.getReportId());
                insertDispatchStmt.setInt(2, dispatchDTO.getAmbulanceId());
                insertDispatchStmt.setInt(3, dispatchDTO.getDriverId());
                insertDispatchStmt.setInt(4, dispatchDTO.getAssignedBy());
                insertDispatchStmt.setTimestamp(5, dispatchDTO.getPickupTime()!=null?Timestamp.valueOf(dispatchDTO.getPickupTime()):null);
                insertDispatchStmt.setTimestamp(6,dispatchDTO.getDropTime()!=null? Timestamp.valueOf(dispatchDTO.getDropTime()):null);
                 
                // Handle latitude
                if (dispatchDTO.getLatitude() != null) {
                    insertDispatchStmt.setDouble(7, dispatchDTO.getLatitude());
                } else {
                    insertDispatchStmt.setNull(7, Types.DOUBLE);
                }

                // Handle longitude
                if (dispatchDTO.getLongitude() != null) {
                    insertDispatchStmt.setDouble(8, dispatchDTO.getLongitude());
                } else {
                    insertDispatchStmt.setNull(8, Types.DOUBLE);
                }
                insertDispatchStmt.setString(9, dispatchDTO.getDropLocation());
                insertDispatchStmt.setInt(10, dispatchDTO.getHospitalId());
                insertDispatchStmt.setString(11, "PROGRESS");
                insertDispatchStmt.executeUpdate();
            }

            // Update Accident Report Status
            try (PreparedStatement updateReportStmt = connection.prepareStatement(Dispatch.UPDATE_ACCIDENT_REPORT_STATUS)) {
                updateReportStmt.setString(1, "PROGRESS");
                updateReportStmt.setInt(2, dispatchDTO.getReportId());
                updateReportStmt.executeUpdate();
            }   

            connection.commit();
            return Response.ok("Dispatch created successfully").build();

        } catch (SQLException e) {
            Log.info(e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Rollback failed: " + rollbackEx.getMessage())
                            .build();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating dispatch: " + e.getMessage())
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error closing connection: " + closeEx.getMessage())
                            .build();
                }
            }
        }
    }

    // 2. Select Dispatches for a Driver
    public Response getDispatchesByDriver(Integer driverId) {
        List<DispatchResponseDto> dispatches = new ArrayList<>();
        try (Connection connection = datasource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Dispatch.SELECT_DISPATCH_BY_DRIVER)) {

            stmt.setInt(1, driverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Log.info("data found");
                    DispatchResponseDto dispatch = new DispatchResponseDto();
                    dispatch.setDispatchId(rs.getString("dispatch_id"));
                    dispatch.setReportId(rs.getString("report_id"));
                    dispatch.setAmbulanceId(rs.getString("ambulance_id"));
                    dispatch.setDriverId(rs.getString("driver_id"));
                    dispatch.setAssignedBy(rs.getString("assigned_by"));
                    dispatch.setPickupTime(rs.getString("pickup_time"));
                    dispatch.setDropTime(rs.getString("drop_time"));
                    dispatch.setLatitude(rs.getString("latitude"));
                    dispatch.setLongitude(rs.getString("longitude"));
                    dispatch.setDropLocation(rs.getString("drop_location"));
                    dispatch.setHospitalId(rs.getString("hospital_id"));
                    dispatch.setStatus(rs.getString("status"));
                    dispatches.add(dispatch);
                }
            }
            return Response.ok(dispatches).build();
        } catch (SQLException e) {
            Log.info(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving dispatches: " + e.getMessage())
                    .build();
        }
    }

    // 3. Update Dispatch Acceptance
    public Response acceptDispatch(Integer dispatchId, Integer ambulanceId, Integer reportId) {
        Connection connection = null;

        try {
            connection = datasource.getConnection();
            connection.setAutoCommit(false);

            // Update Ambulance Status
            try (PreparedStatement updateAmbulanceStmt = connection.prepareStatement(Dispatch.UPDATE_AMBULANCE_STATUS)) {
                updateAmbulanceStmt.setString(1, "ASSIGNED");
                updateAmbulanceStmt.setInt(2, ambulanceId);
                updateAmbulanceStmt.executeUpdate();
            }

            // Update Dispatch Status
            try (PreparedStatement updateDispatchStmt = connection.prepareStatement(Dispatch.UPDATE_DISPATCH_STATUS)) {
                updateDispatchStmt.setString(1, "ASSIGNED");
                updateDispatchStmt.setInt(2, dispatchId);
                updateDispatchStmt.executeUpdate();
            }

            // Update Accident Report Status
            try (PreparedStatement updateReportStmt = connection.prepareStatement(Dispatch.UPDATE_ACCIDENT_REPORT_STATUS)) {
                updateReportStmt.setString(1, "assigned");
                updateReportStmt.setInt(2, reportId);
                updateReportStmt.executeUpdate();
            }

            connection.commit();
            return Response.ok("Dispatch accepted successfully").build();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Rollback failed: " + rollbackEx.getMessage())
                            .build();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error accepting dispatch: " + e.getMessage())
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error closing connection: " + closeEx.getMessage())
                            .build();
                }
            }
        }
    }

    // 4. Handle Pickup
    public Response updatePickup(Integer dispatchId) {
        try (Connection connection = datasource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(Dispatch.UPDATE_DISPATCH_PICKUP)) {
            stmt.setInt(1, dispatchId);
            stmt.executeUpdate();
            return Response.ok("Pickup updated successfully").build();
        } catch (SQLException e) {
            Log.info(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating pickup: " + e.getMessage())
                    .build();
        }
    }

    // 5. Handle Drop
    public Response updateDrop(DispatchRequestDto dispatchRequestDto) {
        Connection connection = null;

        try {
            connection = datasource.getConnection();
            connection.setAutoCommit(false);

            // Update Dispatch Drop Details
            try (PreparedStatement stmt = connection.prepareStatement(Dispatch.UPDATE_DISPATCH_DROP)) {
    
                stmt.setString(1, dispatchRequestDto.dropLocation);
                 // Handle latitude
                 if (dispatchRequestDto.getLatitude() != null) {
                    stmt.setDouble(2, dispatchRequestDto.getLatitude());
                } else {
                    stmt.setNull(2, Types.DOUBLE);
                }

                // Handle longitude
                if (dispatchRequestDto.getLongitude() != null) {
                    stmt.setDouble(3, dispatchRequestDto.getLongitude());
                } else {
                    stmt.setNull(3, Types.DOUBLE);
                }
                stmt.setInt(4, dispatchRequestDto.hospitalId);
                stmt.setInt(5, dispatchRequestDto.dispatchId);
                stmt.executeUpdate();
            }

            // Update Ambulance Status
            try (PreparedStatement stmt = connection.prepareStatement(Dispatch.UPDATE_AMBULANCE_STATUS)) {
                stmt.setString(1, "AVAILABLE");
                stmt.setInt(2, dispatchRequestDto.dispatchId);
                stmt.executeUpdate();
            }

             // Update Accident Report Status
             try (PreparedStatement updateReportStmt = connection.prepareStatement(Dispatch.UPDATE_ACCIDENT_REPORT_STATUS)) {
                updateReportStmt.setString(1, "DISPATCHED");
                updateReportStmt.setInt(2, dispatchRequestDto.reportId);
                updateReportStmt.executeUpdate();
            }

            connection.commit();
            return Response.ok("Drop updated successfully").build();

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Rollback failed: " + rollbackEx.getMessage())
                            .build();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating drop: " + e.getMessage())
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error closing connection: " + closeEx.getMessage())
                            .build();
                }
            }
        }
    }
}