package com.kolip.findiksepeti.common;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DeleteResponse {
    Map<Long, String> couldNotDeleteResults = new HashMap<>();
    String generalError;

    public DeleteResponse() {
    }

    public DeleteResponse(String generalError) {
        this.generalError = generalError;
    }

    public Map<Long, String> getCouldNotDeleteResults() {
        return couldNotDeleteResults;
    }

    public void setCouldNotDeleteResults(Map<Long, String> couldNotDeleteResults) {
        this.couldNotDeleteResults = couldNotDeleteResults;
    }

    public String getGeneralError() {
        return generalError;
    }

    public void setGeneralError(String generalError) {
        this.generalError = generalError;
    }

    public Map<Long, String> addNotDeletedResponse(Long id, String reason) {
        couldNotDeleteResults.put(id, reason);
        return couldNotDeleteResults;
    }

    public boolean isSuccessful() {
        return couldNotDeleteResults.size() == 0 && !StringUtils.hasText(generalError);
    }
}
