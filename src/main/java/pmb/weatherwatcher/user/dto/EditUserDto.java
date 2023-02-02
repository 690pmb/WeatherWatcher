package pmb.weatherwatcher.user.dto;

import pmb.weatherwatcher.common.model.Language;

/** Dto used for editing user's properties. */
public class EditUserDto {

  private String favouriteLocation;

  private Language lang;

  public EditUserDto() {
    super();
  }

  public EditUserDto(String favouriteLocation, Language lang) {
    this.favouriteLocation = favouriteLocation;
    this.lang = lang;
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
}
