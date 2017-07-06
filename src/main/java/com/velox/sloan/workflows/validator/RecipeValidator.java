package com.velox.sloan.workflows.validator;

import com.velox.api.datarecord.DataRecord;
import com.velox.api.user.User;
import com.velox.sloan.workflows.notificator.Notificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Recipe;
import org.mskcc.domain.Request;
import org.mskcc.domain.Sample;
import org.mskcc.util.VeloxConstants;

import java.util.Map;
import java.util.stream.Collectors;

public class RecipeValidator extends Validator {
    private final User user;
    private final Map<String, DataRecord> sampleIgoIdToRecord;

    public RecipeValidator(Notificator notificator, User user, Map<String, DataRecord> sampleIgoIdToRecord) {
        super(notificator);
        this.user = user;
        this.sampleIgoIdToRecord = sampleIgoIdToRecord;
    }

    @Override
    public boolean isValid(Request request) {
        return isRecipeUnambiguous(request);
    }

    private boolean isRecipeUnambiguous(Request request) {
        return request.getSamples().values().stream()
                .map(s -> s.getRecipe())
                .distinct()
                .count() <= 1;
    }

    @Override
    public String getMessage(Request request) {
        return String.format("Request %s has ambiguous recipe: %s", request.getId(),
                StringUtils.join(request.getSamples().values().stream()
                        .map(s -> s.getRecipe())
                        .distinct()
                        .collect(Collectors.toSet()), ","));
    }

    @Override
    public String getName() {
        return "Recipe validator";
    }

    @Override
    public Map<String, Request> updateRequests(Map<String, Request> requests) throws Exception {
        for (Request request : requests.values())
            addRecipes(request);

        return requests;
    }

    private void addRecipes(Request request) throws Exception {
        for (Sample sample : request.getSamples().values())
            addRecipe(sample);
    }

    private void addRecipe(Sample sample) throws Exception {
        String igoId = sample.getIgoId();
        String recipeName = sampleIgoIdToRecord.get(igoId).getStringVal(VeloxConstants.RECIPE, user);

        try {
            Recipe recipe = Recipe.getRecipeByValue(recipeName);
            sample.setRecipe(recipe);
        } catch (Recipe.UnsupportedRecipeException e) {
            getNotificator().addMessage(sample.getRequestId(), String.format("Sample: %s - %s", igoId, e.getMessage()));
        }
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }
}
