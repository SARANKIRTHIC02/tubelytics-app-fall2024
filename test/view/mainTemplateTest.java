package view;

import org.junit.jupiter.api.Test;
import play.twirl.api.Content;
import play.twirl.api.Html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static play.test.Helpers.contentAsString;

public class mainTemplateTest {
    @Test
    public void testMainTemplate() {
        Content html = views.html.main.render("Test Title", Html.apply("<h1>Hello, World!</h1>"));
        assertEquals("text/html", html.contentType());
        String htmlBody = contentAsString(html);
        assertTrue(htmlBody.contains("<title>Test Title</title>"));
        assertTrue(htmlBody.contains("<h1>Hello, World!</h1>"));
        assertTrue(htmlBody.contains("<link rel=\"stylesheet\" media=\"screen\" href=\"/assets/stylesheets/main.css\">"));
        assertTrue(htmlBody.contains("<script src=\"/assets/javascripts/main.js\" type=\"text/javascript\"></script>"));
    }
}
