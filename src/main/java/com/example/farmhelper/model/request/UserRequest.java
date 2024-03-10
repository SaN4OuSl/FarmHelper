package com.example.farmhelper.model.request;

import com.example.farmhelper.config.UserRoles;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "Cannot be blank")
    @Size(max = 255, message = "Size cannot be longer then 255 symbols")
    @ApiModelProperty("Username")
    private String username;

    @NotNull(message = "Cannot be null")
    @ApiModelProperty("Role of user")
    private UserRoles role;
}
