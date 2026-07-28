package in.lucidpoint.app.dto;

import in.lucidpoint.app.entity.SubscriptionTier;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTierRequest {
    @NotNull
    private SubscriptionTier tier;
}
