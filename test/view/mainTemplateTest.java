package view;

import org.junit.jupiter.api.Test;
import play.test.WithApplication;
import play.twirl.api.Content;
import play.twirl.api.Html;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static play.test.Helpers.*;

public class mainTemplateTest extends WithApplication {

    @Test
    public void renderMain()
    {
        Html htmlText = Html.apply("<h1>PLAY</h1>");
        Content html = views.html.main.render("Hello play",htmlText);
        assertEquals("text/html", html.contentType());
        String htmlBody = contentAsString(html);
        assertTrue(htmlBody.contains("<title>Hello play</title>"));
        assertTrue(htmlBody.contains("<h1>PLAY</h1>"));

    }

}
