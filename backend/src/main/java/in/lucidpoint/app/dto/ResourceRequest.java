package in.lucidpoint.app.dto;

import in.lucidpoint.app.entity.Resource.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceRequest {
    @NotBlank
    private String title;

    @NotNull
    private ResourceType type;

    @NotBlank
    private String summary;

    @NotBlank
    private String body;

    private String externalUrl; // optional

    private Integer grade; // optional
    private String subject; // optional
    private Integer sourceYear; // optional
}
