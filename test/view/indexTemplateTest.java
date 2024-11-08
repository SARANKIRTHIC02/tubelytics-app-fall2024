package view;

import org.junit.Test;
import play.test.WithApplication;
import play.twirl.api.Content;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static play.test.Helpers.contentAsString;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class indexTemplateTest extends WithApplication {

    @Test
    public void indexTest()
    {
       Content html = views.html.index.render();
        assertEquals("text/html", html.contentType());
        String htmlBody = contentAsString(html);
        assertTrue(htmlBody.contains("<title>Welcome to Play</title>"));
        assertTrue(htmlBody.contains("<h1>Welcome to Play!</h1>"));
    }

}
