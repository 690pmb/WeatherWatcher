package pmb.weatherwatcher.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Used when updating password, holding new & old passwords.
 */
public class PasswordDto {

    @NotNull
    @Size(min = 6, max = 30)
    private String oldPassword;

    @NotNull
    @Size(min = 6, max = 30)
    private String newPassword;

    public PasswordDto() {
        super();
    }

    public PasswordDto(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
