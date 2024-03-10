package com.example.farmhelper.model.request;

import com.example.farmhelper.entity.ActionType;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportTransactionRequest {

    @ApiModelProperty("Action type")
    private ActionType actionType;

    @ApiModelProperty("Start date")
    private Timestamp startDate;

    @ApiModelProperty("End date")
    private Timestamp endDate;
}
