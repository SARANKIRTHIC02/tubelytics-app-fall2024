package view;

import org.junit.jupiter.api.Test;
import play.twirl.api.Content;
import play.twirl.api.Html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static play.test.Helpers.contentAsString;

/**
 * Unit tests for the main template, verifying the correct rendering of the main layout.
 * Ensures that the main template correctly incorporates dynamic title and content,
 * and includes necessary static resources like CSS and JavaScript files.
 */
public class mainTemplateTest {
    /**
     * Tests the rendering of the main template by verifying content type,
     * dynamic title and content, and the inclusion of static resources.
     * @author saran
     */
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
