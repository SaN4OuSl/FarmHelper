package com.example.farmhelper.mapper;

import com.example.farmhelper.entity.SaleInvoice;
import com.example.farmhelper.model.response.SaleInvoiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SaveInvoiceMapper {

    SaveInvoiceMapper INSTANCE = Mappers.getMapper(SaveInvoiceMapper.class);

    @Mapping(target = "harvestId", source = "saleInvoice", qualifiedByName = "getHarvestId")
    @Mapping(target = "harvestInfo", source = "saleInvoice", qualifiedByName = "getHarvestInfo")
    SaleInvoiceResponse toSaleInvoiceResponse(SaleInvoice saleInvoice);

    @Named("getHarvestInfo")
    default String getHarvestInfo(SaleInvoice saleInvoice) {
        return saleInvoice.getHarvest().getCrop().getName() + ", "
            + saleInvoice.getHarvest().getMonthYearOfCollection();
    }

    @Named("getHarvestId")
    default Long getHarvestId(SaleInvoice saleInvoice) {
        return saleInvoice.getHarvest().getId();
    }
}
