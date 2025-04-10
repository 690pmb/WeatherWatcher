package pmb.weatherwatcher.user.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pmb.weatherwatcher.TestUtils;
import pmb.weatherwatcher.common.exception.AlreadyExistException;
import pmb.weatherwatcher.common.exception.BadRequestException;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.dto.EditUserDto;
import pmb.weatherwatcher.user.dto.JwtTokenDto;
import pmb.weatherwatcher.user.dto.PasswordDto;
import pmb.weatherwatcher.user.dto.UserDto;
import pmb.weatherwatcher.user.security.JwtTokenProvider;
import pmb.weatherwatcher.user.security.MyUserDetailsService;
import pmb.weatherwatcher.user.service.UserService;

@ActiveProfiles("test")
@Import(JwtTokenProvider.class)
@MockBean(MyUserDetailsService.class)
@WebMvcTest(controllers = UserController.class)
@DisplayNameGeneration(value = ReplaceUnderscores.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private UserService userService;
  private static final UserDto DUMMY_USER =
      new UserDto("test", "password", "lyon", Language.FRENCH, "Europe/Paris");
  private static final PasswordDto DUMMY_PASSWORD = new PasswordDto("password", "password2");
  private static final EditUserDto DUMMY_EDIT_USER =
      new EditUserDto("lyon", Language.ARABIC, "Asia/Tokyo");
  private static final JwtTokenDto DUMMY_TOKEN = new JwtTokenDto("jwtToken");

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(userService);
  }

  @Nested
  class Signin {

    @ParameterizedTest(
        name =
            "Given user with login ''{0}'', password ''{1}'', lang ''{2}'' and timezone ''{3}'' when login then ok")
    @CsvSource({
      "test, password, fr,Europe/Paris",
      "o, password,,",
      "test, p, nl,Asia/Rangoon",
      "01234567891011121314151617181920, password, en,America/Phoenix",
      "test, 01234567891011121314151617181920,,Africa/Accra"
    })
    void ok(String login, String password, String lang, String timezone) throws Exception {
      ArgumentCaptor<UserDto> user = ArgumentCaptor.forClass(UserDto.class);

      when(userService.login(any())).thenReturn(DUMMY_TOKEN);

      assertEquals(
          DUMMY_TOKEN.getToken(),
          objectMapper
              .readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              post("/users/signin")
                                  .content(buildUserJson(login, password, lang, timezone))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  JwtTokenDto.class)
              .getToken());

      verify(userService).login(user.capture());

      UserDto signin = user.getValue();
      assertAll(
          () -> assertEquals(login, signin.getUsername()),
          () -> assertEquals(password, signin.getPassword()),
          () -> assertEquals("lyon", signin.getFavouriteLocation()),
          () -> assertEquals(timezone, signin.getTimezone()));
    }

    @ParameterizedTest(
        name =
            "Given user with login ''{0}'', password ''{1}'', lang ''{2}'' and timezone ''{3}'' when login then bad request")
    @CsvSource({", password,fr,", "test,,fr,Pacific/Fiji"})
    void when_failed_validation_then_bad_request(
        String login, String password, String lang, String timezone) throws Exception {
      mockMvc
          .perform(
              post("/users/signin")
                  .content(buildUserJson(login, password, lang, timezone))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(userService, never()).login(any());
    }

    @Test
    void when_incorrect_password_then_unauthorized() throws Exception {
      when(userService.login(any())).thenThrow(BadCredentialsException.class);

      mockMvc
          .perform(
              post("/users/signin")
                  .content(buildUserJson(DUMMY_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized());

      verify(userService).login(any());
    }
  }

  @Nested
  class Signup {

    @Test
    void ok() throws Exception {
      ArgumentCaptor<UserDto> capture = ArgumentCaptor.forClass(UserDto.class);
      when(userService.save(any())).thenAnswer(a -> a.getArgument(0));

      assertThat(DUMMY_USER)
          .usingRecursiveComparison()
          .isEqualTo(
              objectMapper.readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              post("/users/signup")
                                  .content(buildUserJson(DUMMY_USER))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isCreated())),
                  UserDto.class));

      verify(userService).save(capture.capture());
      assertThat(capture.getValue()).usingRecursiveComparison().isEqualTo(DUMMY_USER);
    }

    @Test
    void when_already_exist_then_conflict() throws Exception {
      when(userService.save(any())).thenThrow(AlreadyExistException.class);

      mockMvc
          .perform(
              post("/users/signup")
                  .content(buildUserJson(DUMMY_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isConflict());

      verify(userService).save(any());
    }

    @Test
    void when_bad_req_exception_then_bad_request() throws Exception {
      when(userService.save(any())).thenThrow(BadRequestException.class);

      mockMvc
          .perform(
              post("/users/signup")
                  .content(buildUserJson(DUMMY_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(userService).save(any());
    }

    @ParameterizedTest(
        name =
            "Given user with signup ''{0}'', password ''{1}'' and lang ''{2}'' when signup then bad request")
    @CsvSource({
      ", password,fr,Pacific/Fiji",
      "o, password,fr,Pacific/Fiji",
      "test,,fr,Pacific/Fiji",
      "test, p,fr,Pacific/Fiji",
      ",,fr,Pacific/Fiji",
      "01234567891011121314151617181920, password,fr,Pacific/Fiji",
      "test, 01234567891011121314151617181920,fr,Pacific/Fiji",
      "test, password,,Pacific/Fiji",
      "test, password,fra,Pacific/Fiji",
      "test, password,'',Pacific/Fiji",
      "test, password,fr,",
      "test, password,fr,''",
    })
    void when_invalid_then_bad_request(String login, String password, String lang, String timezone)
        throws Exception {
      mockMvc
          .perform(
              post("/users/signup")
                  .content(buildUserJson(login, password, lang, timezone))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(userService, never()).save(any());
    }

    @Test
    void when_exception_then_internal_server_error() throws Exception {
      when(userService.save(any())).thenThrow(new ArithmeticException());

      mockMvc
          .perform(
              post("/users/signup")
                  .content(buildUserJson(DUMMY_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isInternalServerError());

      verify(userService).save(any());
    }
  }

  @Nested
  class UpdatePassword {

    @Test
    @WithMockUser
    void ok() throws Exception {
      ArgumentCaptor<PasswordDto> capture = ArgumentCaptor.forClass(PasswordDto.class);
      doNothing().when(userService).updatePassword(any());

      mockMvc
          .perform(
              put("/users/password")
                  .content(objectMapper.writeValueAsString(DUMMY_PASSWORD))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isNoContent());

      verify(userService).updatePassword(capture.capture());
      assertThat(capture.getValue()).usingRecursiveComparison().isEqualTo(DUMMY_PASSWORD);
    }

    @WithMockUser
    @ParameterizedTest(
        name =
            "Given new password ''{0}'' and old password ''{1}'' when updates password then bad request")
    @CsvSource({
      ", password",
      "o, password",
      "01234567891011121314151617181920, password",
      "password,",
      "password, o",
      "password, 01234567891011121314151617181920",
    })
    void when_failed_validation_then_bad_request(String newPassword, String oldPassword)
        throws Exception {
      mockMvc
          .perform(
              put("/users/password")
                  .content(
                      objectMapper.writeValueAsString(new PasswordDto(oldPassword, newPassword)))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(userService, never()).updatePassword(any());
    }

    @Test
    void not_authenticated_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              put("/users/password")
                  .content(objectMapper.writeValueAsString(DUMMY_PASSWORD))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized());

      verify(userService, never()).updatePassword(any());
    }
  }

  @Nested
  class Edit {

    @Test
    void not_authenticated_then_unauthorized() throws Exception {
      mockMvc
          .perform(
              put("/users")
                  .content(objectMapper.writeValueAsString(DUMMY_EDIT_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isUnauthorized());

      verify(userService, never()).edit(any());
    }

    @Test
    @WithMockUser
    void when_failed_validation_then_bad_request() throws Exception {
      when(userService.edit(any())).thenThrow(BadRequestException.class);

      mockMvc
          .perform(
              put("/users")
                  .content(objectMapper.writeValueAsString(DUMMY_EDIT_USER))
                  .contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(status().isBadRequest());

      verify(userService).edit(any());
    }

    @Test
    @WithMockUser
    void ok() throws Exception {
      JwtTokenDto expected = new JwtTokenDto("jwtToken");
      ArgumentCaptor<EditUserDto> capture = ArgumentCaptor.forClass(EditUserDto.class);
      when(userService.edit(any())).thenReturn(expected);

      assertEquals(
          DUMMY_TOKEN.getToken(),
          objectMapper
              .readValue(
                  TestUtils.readResponse.apply(
                      mockMvc
                          .perform(
                              put("/users")
                                  .content(objectMapper.writeValueAsString(DUMMY_EDIT_USER))
                                  .contentType(MediaType.APPLICATION_JSON_VALUE))
                          .andExpect(status().isOk())),
                  JwtTokenDto.class)
              .getToken());

      verify(userService).edit(capture.capture());
      assertThat(capture.getValue()).usingRecursiveComparison().isEqualTo(DUMMY_EDIT_USER);
    }
  }

  public static String buildUserJson(UserDto user) {
    return buildUserJson(
        user.getUsername(), user.getPassword(), user.getLang().getCode(), user.getTimezone());
  }

  public static String buildUserJson(String login, String password, String lang, String timezone) {
    return "{\"username\": "
        + buildField(login)
        + ",\"password\": "
        + buildField(password)
        + ",\"favouriteLocation\": \"lyon\",\"lang\": "
        + buildField(lang)
        + ",\"timezone\": "
        + buildField(timezone)
        + "}";
  }

  private static String buildField(String field) {
    return Optional.ofNullable(field).map(f -> "\"" + f + "\"").orElse(null);
  }
}
