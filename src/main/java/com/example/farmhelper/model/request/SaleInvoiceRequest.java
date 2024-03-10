package com.example.farmhelper.model.request;

import io.swagger.annotations.ApiModelProperty;
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
public class SaleInvoiceRequest {

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Harvest")
    private Long harvestId;

    @NotNull(message = "Cannot be null")
    @Positive
    @ApiModelProperty("Amount of harvest")
    private Double amount;

    @NotNull(message = "Cannot be null")
    @Positive
    @ApiModelProperty("Unit price")
    private Double unitPrice;

    @ApiModelProperty("Description")
    private String description;
}
