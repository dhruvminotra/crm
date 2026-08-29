package com.veyora.crm.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Stores RoleType as the integer roletype column, as tf-main does. */
@Converter(autoApply = false)
public class RoleTypeConverter implements AttributeConverter<RoleType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleType attribute) {
        return attribute != null ? attribute.getId() : null;
    }

    @Override
    public RoleType convertToEntityAttribute(Integer dbData) {
        return RoleType.fromId(dbData);
    }
}
