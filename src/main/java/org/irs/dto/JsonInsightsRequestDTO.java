package org.irs.dto;

import java.util.Map;

public class JsonInsightsRequestDTO {
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
} 