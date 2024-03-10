package com.example.farmhelper.model.request;

import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HarvestRequest {

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Crop")
    private Long cropId;

    @NotNull(message = "Cannot be null")
    @Positive
    @ApiModelProperty("Amount of harvest")
    private Double amount;

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Amount of harvest")
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[0-2])$", message = "Must be in format YYYY-MM")
    private String monthAndYearOfCollection;

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @ApiModelProperty("Field IDs")
    private Set<Long> fieldIds;
}
