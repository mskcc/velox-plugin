package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;

import java.util.stream.Collectors;

public class RecipeValidator implements Validator {
    private BulkNotificator notificator;

    public RecipeValidator(BulkNotificator notificator) {
        this.notificator = notificator;
    }

    @Override
    public boolean isValid(Request request) {
        return allSamplesHaveRecipeSet(request) && isRecipeUnambiguous(request);
    }

    private boolean allSamplesHaveRecipeSet(Request request) {
        return request.getSamples().values().stream()
                .allMatch(s -> s.getRecipe() != null);
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
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
                getRecipes(request));
    }

    private String getRecipes(Request request) {
        return StringUtils.join(request.getSamples().values().stream()
                .map(s -> s.getRecipe())
                .distinct()
                .collect(Collectors.toSet()), ",");
    }

    @Override
    public String getName() {
        return "Recipe validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return true;
    }
}
