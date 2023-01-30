package pmb.weatherwatcher.user.dto;

import javax.validation.constraints.NotNull;

/** Dto used for editing user's properties. */
public class EditUserDto {

  @NotNull private String favouriteLocation;

  public EditUserDto() {
    super();
  }

  public EditUserDto(String favouriteLocation) {
    this.favouriteLocation = favouriteLocation;
  }

  public String getFavouriteLocation() {
    return favouriteLocation;
  }

  public void setFavouriteLocation(String favouriteLocation) {
    this.favouriteLocation = favouriteLocation;
  }
}
