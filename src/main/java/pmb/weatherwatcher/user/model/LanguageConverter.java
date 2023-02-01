package pmb.weatherwatcher.user.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import pmb.weatherwatcher.common.model.Language;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

  @Override
  public String convertToDatabaseColumn(Language Language) {
    return Language.getCode();
  }

  @Override
  public Language convertToEntityAttribute(String dbData) {
    return Language.fromCode(dbData).orElseThrow();
  }
}
