package pmb.weatherwatcher.common.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum Language {
  ARABIC("ar"),
  BENGALI("bn"),
  BULGARIAN("bg"),
  CHINESE("zh"),
  CZECH("cs"),
  DANISH("da"),
  DUTCH("nl"),
  ENGLISH("en"),
  FINNISH("fi"),
  FRENCH("fr"),
  GERMAN("de"),
  GREEK("el"),
  HINDI("hi"),
  HUNGARIAN("hu"),
  ITALIAN("it"),
  JAPANESE("ja"),
  JAVANESE("jv"),
  KOREAN("ko"),
  MANDARIN("zh_cmn"),
  MARATHI("mr"),
  POLISH("pl"),
  PORTUGUESE("pt"),
  PUNJABI("pa"),
  ROMANIAN("ro"),
  RUSSIAN("ru"),
  SERBIAN("sr"),
  SINHALESE("si"),
  SLOVAK("sk"),
  SPANISH("es"),
  SWEDISH("sv"),
  TAMIL("ta"),
  TELUGU("te"),
  TURKISH("tr"),
  UKRAINIAN("uk"),
  URDU("ur"),
  VIETNAMESE("vi"),
  SHANGHAINESE("zh_wuu"),
  XIANG("zh_hsn"),
  CANTONESE("zh_yue"),
  ZULU("zu");

  private final String code;

  private Language(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return code;
  }

  public static Optional<Language> fromCode(String code) {
    return Arrays.stream(Language.values())
        .filter(lang -> StringUtils.equalsIgnoreCase(lang.getCode(), code))
        .findFirst();
  }
}
