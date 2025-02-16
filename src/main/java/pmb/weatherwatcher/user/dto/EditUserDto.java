package pmb.weatherwatcher.user.dto;

import pmb.weatherwatcher.common.model.Language;

/** Dto used for editing user's properties. */
public class EditUserDto {

  private String favouriteLocation;

  private Language lang;

  private String timezone;

  public EditUserDto() {
    super();
  }

  public EditUserDto(String favouriteLocation, Language lang, String timezone) {
    this.favouriteLocation = favouriteLocation;
    this.lang = lang;
    this.timezone = timezone;
  }

  public String getFavouriteLocation() {
    return favouriteLocation;
  }

  public void setFavouriteLocation(String favouriteLocation) {
    this.favouriteLocation = favouriteLocation;
  }

  public Language getLang() {
    return lang;
  }

  public void setLang(Language lang) {
    this.lang = lang;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
}
