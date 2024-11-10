package view;

import org.junit.jupiter.api.Test;
import play.twirl.api.Content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static play.test.Helpers.contentAsString;

public class indexTemplateTest {
    @Test
    public void renderWelcomeView() {
        Content html = views.html.index.render();

        assertEquals("text/html", html.contentType());

        String htmlContent = contentAsString(html);
        assertTrue(htmlContent.contains("<title>Welcome to Play</title>"));
        assertTrue(htmlContent.contains("<h1>Welcome to Play!</h1>"));
        System.out.println();
    }
}
