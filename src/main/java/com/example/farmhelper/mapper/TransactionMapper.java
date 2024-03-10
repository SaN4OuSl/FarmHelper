package com.example.farmhelper.mapper;

import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.entity.Transaction;
import com.example.farmhelper.entity.User;
import com.example.farmhelper.model.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "cropName", source = "harvest", qualifiedByName = "getCropName")
    @Mapping(target = "monthAndYearOfCollection", source = "harvest",
        qualifiedByName = "getMonthYearOfCollection")
    @Mapping(target = "userInfo", source = "user", qualifiedByName = "getUserInfo")
    @Mapping(source = "description", target = "explanation")
    TransactionResponse toTransactionsResponse(Transaction transaction);

    @Named("getCropName")
    default String getCropName(Harvest harvest) {
        return harvest.getCrop().getName();
    }

    @Named("getMonthYearOfCollection")
    default String getMonthAndYearOfCollection(Harvest harvest) {
        return harvest.getMonthYearOfCollection();
    }

    @Named("getUserInfo")
    default String getUserInfo(User user) {
        return user.getFirstName() + " " + user.getLastName() + ", " + user.getEmail() + ", "
            + user.getRole();
    }
}
