package pmb.weatherwatcher.user.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import pmb.weatherwatcher.common.model.Language;
import pmb.weatherwatcher.user.model.User;

@DataJpaTest
class UserRepositoryTest {
  @Autowired UserRepository userRepository;

  @Test
  void save() {
    User user = new User("login", "pwd", "Lyon", Language.ARABIC, "Asia/Tokyo");
    User saved = userRepository.save(user);
    assertAll(
        () -> assertEquals("login", saved.getLogin()),
        () -> assertEquals("pwd", saved.getPassword()),
        () -> assertEquals("Lyon", saved.getFavouriteLocation()),
        () -> assertEquals("ar", saved.getLang().getCode()),
        () -> assertEquals("Asia/Tokyo", saved.getTimezone()));
  }

  @Test
  @Sql(statements = "insert into user values ('login2', 'pwd2', 'Paris', 'fr', 'Europe/Paris')")
  void findById() {
    Optional<User> user = userRepository.findById("login2");
    assertAll(
        () -> assertTrue(user.isPresent()),
        () -> assertEquals("login2", user.get().getLogin()),
        () -> assertEquals("pwd2", user.get().getPassword()),
        () -> assertEquals("Paris", user.get().getFavouriteLocation()),
        () -> assertEquals("fr", user.get().getLang().getCode()),
        () -> assertEquals("Europe/Paris", user.get().getTimezone()));
  }
}
