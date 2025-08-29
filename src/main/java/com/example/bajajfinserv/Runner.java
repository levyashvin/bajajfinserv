package com.example.bajajfinserv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class Runner implements CommandLineRunner {
  // FILL THESE:
  private static final String NAME  = "Yuva Yashvin";
  private static final String REGNO = "22BDS0177";
  private static final String EMAIL = "yuvayashvin@gmail.com";

  // Paste your ODD question final SQL here (keep the triple quotes)
  private static final String FINAL_SQL = """
      SELECT
          p.amount AS salary,
          CONCAT(e.first_name, ' ', e.last_name) AS name,
          TIMESTAMPDIFF(YEAR, e.dob, p.payment_time) AS age,
          d.department_name
      FROM payments p
      JOIN employee e ON e.emp_id = p.emp_id
      JOIN department d ON d.department_id = e.department
      WHERE DAY(p.payment_time) <> 1
      ORDER BY p.amount DESC
      LIMIT 1;
      """;

  @Override public void run(String... args) {
    RestTemplate rt = new RestTemplate();

    // 1) generate webhook
    String genUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    HttpHeaders h1 = new HttpHeaders(); h1.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<WebhookResponse> gen = rt.exchange(
      genUrl, HttpMethod.POST, new HttpEntity<>(Map.of(
        "name", NAME, "regNo", REGNO, "email", EMAIL
      ), h1), WebhookResponse.class);

    WebhookResponse body = gen.getBody();
    if (body == null) throw new IllegalStateException("No webhook in response");

    // 2) submit final query (Authorization must be the raw JWT, no 'Bearer ')
    String submitUrl = (body.webhook() != null && !body.webhook().isBlank())
        ? body.webhook()
        : "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    HttpHeaders h2 = new HttpHeaders();
    h2.setContentType(MediaType.APPLICATION_JSON);
    h2.set("Authorization", body.accessToken());

    ResponseEntity<String> submit = rt.exchange(
      submitUrl, HttpMethod.POST, new HttpEntity<>(Map.of("finalQuery", FINAL_SQL), h2), String.class);

    System.out.println("Submitted. Server said: " + submit.getBody());
  }
}
