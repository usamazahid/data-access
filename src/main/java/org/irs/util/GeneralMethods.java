package org.irs.util;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

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
    public String parseRangeToInterval(String range) {
        if (range != null && range.matches("^[0-9]+[dwm ysMh]$")) { // Added 's' for seconds and 'M' for months
            char unit = range.charAt(range.length() - 1);
            String value = range.substring(0, range.length() - 1);

            switch (unit) {
                case 'd': return value + " days";
                case 'w': return value + " weeks";
                case 'm': return value + " minutes";
                case 'M': return value + " months"; // Added case for months
                case 's': return value + " seconds"; // Added case for seconds
                case 'y': return value + " years";
                case 'h': return value + " years";
                default:  return "1 month"; // Default fallback
            }
        }
        return "1 month"; // Default fallback
    }

    public static boolean isMatchingQuestion(String key, String question) {
        if (key == null || question == null) {
            return false;
        }

        // 1) Normalize: lowercase, strip punctuation, collapse whitespace
        String normKey = normalize(key);
        String normQuestion = normalize(question);

        // 2) Exact match?
        if (normKey.equals(normQuestion)) {
            return true;
        }

        // 3) Fuzzy match?
        JaroWinklerSimilarity jw = new JaroWinklerSimilarity();
        double score = jw.apply(normKey, normQuestion);
        return score >= ConstantValues.FUZZY_THRESHOLD;
    }

    private static String normalize(String s) {
        // Remove any non-alphanumeric characters, turn to single spaces, lowercase
        return s
            .toLowerCase()
            .replaceAll("[^a-z0-9 ]+", " ")
            .trim()
            .replaceAll("\\s+", " ");
    }

    public String routeQuery(Map<String, String> questionSqlMap, String userQuestion) {
        if(userQuestion!=null){
            for (Map.Entry<String, String> entry : questionSqlMap.entrySet()) {
                String key = entry.getKey();
                if (isMatchingQuestion(key, userQuestion)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
     
}