package view;

import org.junit.Test;

import play.test.WithApplication;
import play.twirl.api.Content;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentAsString;
public class wordStatsTemplateTest extends WithApplication {
    @Test
    public void testWordFrequencyTemplateWithData() {
        Map<String, Long> wordFrequency = new HashMap<>();
        wordFrequency.put("sample", 10L);
        wordFrequency.put("test", 5L);
        wordFrequency.put("frequency", 3L);

        Content html = views.html.wordStats.render(wordFrequency);

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Word Frequency Statistics"));
        assertTrue(renderedContent.contains("<td>sample</td>"));
        assertTrue(renderedContent.contains("<td>10</td>"));
        assertTrue(renderedContent.contains("<td>test</td>"));
        assertTrue(renderedContent.contains("<td>5</td>"));
        assertTrue(renderedContent.contains("<td>frequency</td>"));
        assertTrue(renderedContent.contains("<td>3</td>"));
    }

    @Test
    public void testWordFrequencyTemplateWithoutData() {
        Map<String, Long> wordFrequency = new HashMap<>();

        Content html = views.html.wordStats.render(wordFrequency);

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Word Frequency Statistics"));
        assertTrue(renderedContent.contains("<tbody></tbody>")); // Expecting an empty table body
    }
}
