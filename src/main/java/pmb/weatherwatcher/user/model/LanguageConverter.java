package pmb.weatherwatcher.user.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import pmb.weatherwatcher.common.exception.InternalServerErrorException;
import pmb.weatherwatcher.common.model.Language;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

  @Override
  public String convertToDatabaseColumn(Language language) {
    return language.getCode();
  }

  @Override
  public Language convertToEntityAttribute(String dbData) {
    return Language.fromCode(dbData)
        .orElseThrow(
            () ->
                new InternalServerErrorException(
                    "Can't convert code:'" + dbData + "' to Language"));
  }
}
