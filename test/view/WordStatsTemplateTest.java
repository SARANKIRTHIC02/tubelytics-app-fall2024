package view;

import org.junit.jupiter.api.Test;
import play.mvc.Result;
import play.test.WithApplication;
import views.html.wordStats;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static play.mvc.Results.ok;
import static play.test.Helpers.contentAsString;

public class WordStatsTemplateTest extends WithApplication {

    @Test
    public void testWordFrequencyRendering() {
        Map<String, Long> wordFrequency = new HashMap<>();
        wordFrequency.put("Java", 50L);
        wordFrequency.put("Scala", 30L);
        wordFrequency.put("Play", 20L);
        wordFrequency.put("Framework", 10L);

        Result result = ok(wordStats.render(wordFrequency));

        String html = contentAsString(result);

        assertTrue(html.contains("Java"));
        assertTrue(html.contains("50"));
        assertTrue(html.contains("Scala"));
        assertTrue(html.contains("30"));
        assertTrue(html.contains("Play"));
        assertTrue(html.contains("20"));
        assertTrue(html.contains("Framework"));
        assertTrue(html.contains("10"));

        assertTrue(html.contains("<table"));
        assertTrue(html.contains("</table>"));
    }

    @Test
    public void testEmptyWordFrequency() {
        Map<String, Long> wordFrequency = new HashMap<>();

        Result result = ok(wordStats.render(wordFrequency));

        String html = contentAsString(result);

        assertTrue(html.contains("<table"));
        assertTrue(html.contains("</table>"));
        assertFalse(html.contains("Java"));
        assertFalse(html.contains("50"));
    }


}
