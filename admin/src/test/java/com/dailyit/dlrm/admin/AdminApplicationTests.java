package com.dailyit.dlrm.admin;

import com.dailyit.dlrm.core.testsupport.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(PostgresTestContainer.class)
@SpringBootTest
class AdminApplicationTests {

  @Test
  void contextLoads() {}
}
