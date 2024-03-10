package com.example.farmhelper.model.request;

import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddHarvestRequest {

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Amount of harvest")
    private Long id;

    @NotNull(message = "Cannot be null")
    @Positive
    @ApiModelProperty("Amount of harvest")
    private Double amount;

    @NotNull(message = "Cannot be null")
    @NotEmpty(message = "Cannot be empty")
    @ApiModelProperty("Field IDs")
    private Set<Long> fieldIds;
}
