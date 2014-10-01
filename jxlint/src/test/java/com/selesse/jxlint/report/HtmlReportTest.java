package com.selesse.jxlint.report;

import com.google.common.io.Files;
import com.selesse.jxlint.model.OutputType;
import com.selesse.jxlint.samplerules.xml.rules.AuthorTagRule;
import com.selesse.jxlint.samplerules.xml.rules.XmlEncodingRule;
import com.selesse.jxlint.settings.JxlintProgramSettings;
import com.selesse.jxlint.settings.ProgramSettings;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.fest.assertions.api.Assertions.assertThat;

public class HtmlReportTest extends AbstractReportTest {
    @Test
    public void makeSureHtmlReportGetsCreated() throws IOException {
        File createdFile = ensureReportGetsCreatedFor2Errors(OutputType.HTML);

        assertThat(createdFile).exists();

        String fileContents = new String(Files.toByteArray(createdFile), Charset.defaultCharset());

        ProgramSettings programSettings = new JxlintProgramSettings();

        assertThat(fileContents).contains(programSettings.getProgramName());
        assertThat(fileContents).contains(programSettings.getProgramVersion());

        assertThat(fileContents).contains("There are 2 warnings, 0 errors, and 0 fatal errors (2 total).");

        AuthorTagRule authorTagRule = new AuthorTagRule();
        XmlEncodingRule xmlEncodingRule = new XmlEncodingRule();

        // Ensure a summary report is printed with both rules
        assertThat(fileContents).contains(
                "                            <td> " + authorTagRule.getName() + " </td>\n" +
                "                            <td> " + authorTagRule.getCategory() + " </td>\n" +
                "                            <td> 1 </td>"
        );
        assertThat(fileContents).contains(
                "                            <td> " + xmlEncodingRule.getName() + " </td>\n" +
                "                            <td> " + xmlEncodingRule.getCategory() + " </td>\n" +
                "                            <td> 1 </td>"
        );

        assertThat(fileContents).contains(authorTagRule.getSeverity().toString());
        assertThat(fileContents).contains(authorTagRule.getCategory().toString());
        assertThat(fileContents).contains("<tr> <th> Enabled by default? </th> <td> No </tr>");
        assertThat(fileContents).contains("<tr> <th> Enabled by default? </th> <td> Yes </tr>");
    }
}
