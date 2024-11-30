package org.irs.database;
import java.sql.Connection;
import java.sql.SQLException;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Datasources {

    

    @Inject
    @DataSource("postgrel-db")
    AgroalDataSource postgrelDatabase;
 
 
    public Connection getConnection( ) {
        try {
          
            return postgrelDatabase.getConnection();
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   
}
