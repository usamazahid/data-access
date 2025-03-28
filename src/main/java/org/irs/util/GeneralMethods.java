package org.irs.util;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class GeneralMethods {
    public String saveBase64ToFile(String base64Data, String prefix, String extension) throws Exception {
        String BASE_DIR=ConstantValues.BASE_DIR;
         // Ensure the base directory exists
         
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Data);
        String fileName = prefix + "_" + System.currentTimeMillis() + extension;
        Path filePath = Paths.get(BASE_DIR, fileName);

        // Save the file to the server
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(decodedBytes);
        }

        return filePath.toString();
    }

    public String readFileAsBase64(String filePath) throws Exception {
            File file = new File(filePath);

            // Check if the file exists
            if (!file.exists()) {
                Log.info("File not found at path: " + filePath);
                return null;
            }

            // Read file content and encode in Base64
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] fileBytes = fis.readAllBytes();
                return java.util.Base64.getEncoder().encodeToString(fileBytes);
            } catch (IOException ex) {
                Log.info("Error reading file and converting to Base64: " + ex.getMessage(), ex);
            }
            return null;
    }

    public Response getFileResponse(String filePath, String mediaType) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream inputStream = new FileInputStream(file)) {
                // Read the file and return the InputStream as part of the response
                return Response.ok(inputStream)
                        .type(mediaType) // Set the media type dynamically based on file type
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") // Optional: for file download
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Error reading file", e);
            }
        } else {
            throw new NotFoundException("File not found");
        }
    }


     
}