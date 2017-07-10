package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.Notificator;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.Request;

import java.util.stream.Collectors;

public class RecipeValidator extends Validator {
    public RecipeValidator(Notificator notificator) {
        super(notificator);
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
