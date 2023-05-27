package pmb.weatherwatcher.notification.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import pmb.weatherwatcher.notification.model.Subscription;

@DataJpaTest(showSql = true)
public class SubscriptionRepositoryTest {

  @Autowired SubscriptionRepository subscriptionRepository;

  @Test
  @Sql(
      statements = {
        "insert into user values ('hnewling1', 'pwd1', 'Lyon', 'fr');",
        "insert into user values ('rvakhonin0', 'pwd2', 'Paris', 'en');",
        "insert into subscription (user_agent, user, endpoint, public_key, private_key) values ('Mozilla/5.0 (Windows NT 6.1; rv:21.0) Gecko/20100101 Firefox/21.0', 'rvakhonin0', 'https://hud.gov/lorem/ipsum/dolor.html', 'iYw3p2B8sUp', 'oRjWm9gBLU2');",
        "insert into subscription (user_agent, user, endpoint, public_key, private_key) values ('Mozilla/5.0 (Windows; U; Windows NT 6.1; fr-FR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27', 'hnewling1', 'http://dion.ne.jp/platea.json', '9BeFPZ2', 'vAMLFGO6qj');",
        "insert into subscription (user_agent, user, endpoint, public_key, private_key) values ('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_8) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.801.0 Safari/535.1', 'hnewling1', 'https://cpanel.net/id/justo/sit/amet.js', 'cPJEfyGv', 'MlcgbI6hI2Y1');"
      })
  void delete() {
    String userLoggued = "hnewling1";
    String otherUser = "rvakhonin0";

    subscriptionRepository.deleteOthersByUserId(
        userLoggued,
        "Mozilla/5.0 (Windows; U; Windows NT 6.1; fr-FR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27");

    Map<String, List<Subscription>> subscriptions =
        subscriptionRepository.findByUserLoginIn(Set.of(userLoggued, otherUser)).stream()
            .collect(Collectors.groupingBy(s -> s.getId().getUser()));

    assertAll(
        () -> assertEquals(2, subscriptions.size()),
        () -> assertEquals(1, subscriptions.get(userLoggued).size()),
        () -> assertEquals("9BeFPZ2", subscriptions.get(userLoggued).get(0).getPublicKey()),
        () -> assertEquals(1, subscriptions.get(otherUser).size()),
        () -> assertEquals("iYw3p2B8sUp", subscriptions.get(otherUser).get(0).getPublicKey()));
  }
}
