package com.casaflow.subscription.dto;

import com.casaflow.subscription.PlanCode;
import jakarta.validation.constraints.NotNull;

public record ChangePlanRequest(@NotNull PlanCode planCode) {
}
