package view;

import org.junit.Test;

import play.test.WithApplication;
import play.twirl.api.Content;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentAsString;

/**
 * Unit tests for the wordStats template, verifying the correct rendering of word frequency statistics.
 * Tests both the scenarios where word frequency data is provided and where no data is available.
 */
public class WordStatsTemplateTest extends WithApplication {

    /**
     * @author saran
     * @author sushanth
     * Tests the rendering of the wordStats template with provided word frequency data.
     * Verifies that the word frequencies are correctly displayed in the HTML table.
     *
     */
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

    /**
     * @author saran
     * @author sushanth
     * Tests the rendering of the wordStats template when no word frequency data is provided.
     * Verifies that the empty table body is correctly displayed.
     *
     */
    @Test
    public void testWordFrequencyTemplateWithoutData() {
        Map<String, Long> wordFrequency = new HashMap<>();

        Content html = views.html.wordStats.render(wordFrequency);

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Word Frequency Statistics"));
        assertTrue(renderedContent.contains("<tbody>")); // Expecting an empty table body
    }



}
