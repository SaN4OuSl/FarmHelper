package com.example.farmhelper.model.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WriteOffHarvestRequest {

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Amount of harvest")
    private Long id;

    @NotNull(message = "Cannot be null")
    @Positive
    @ApiModelProperty("Amount of harvest")
    private Double amount;

    @NotBlank(message = "Cannot be blank")
    @Size(max = 1024, message = "Size cannot be longer then 1024 symbols")
    @ApiModelProperty("Explanation")
    private String explanation;
}
