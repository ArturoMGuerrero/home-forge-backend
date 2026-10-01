package com.homeforge.subscription.dto;

import com.homeforge.subscription.PlanCode;
import jakarta.validation.constraints.NotNull;

public record ChangePlanRequest(@NotNull PlanCode planCode) {
}
