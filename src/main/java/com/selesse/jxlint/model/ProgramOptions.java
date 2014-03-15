package com.selesse.jxlint.model;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.jxlint.Main;
import com.selesse.jxlint.model.rules.LintError;
import com.selesse.jxlint.model.rules.LintRulesImpl;
import com.selesse.jxlint.model.rules.NonExistentLintRuleException;
import com.selesse.jxlint.report.*;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ProgramOptions {
    private Map<String, String> options;
    private String sourceDirectory;

    public ProgramOptions() {
        this.options = Maps.newHashMap();
    }

    /**
     * Used for options that don't have any associated information (i.e. option == true).
     */
    public void addOption(String optionName) {
        options.put(optionName, "true");
    }

    public void addOption(String optionName, String value) {
        options.put(optionName, value);
    }

    public boolean hasOption(String optionName) {
        return options.containsKey(optionName);
    }

    public String getOption(String show) {
        return options.get(show);
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public Reporter createReporterFor(List<LintError> lintErrors) throws UnableToCreateReportException {
        String outputType = getOption("outputType");
        String outputPath = getOption("outputTypePath");
        Reporter reporter = new DefaultReporter(System.out, lintErrors);

        if (outputType == null) {
            return reporter;
        }

        if (outputType.equals("quiet")) {
            try {
                reporter = new DefaultReporter(new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        // my own little /dev/null
                    }
                }, false, Charsets.UTF_8.displayName()), lintErrors);
            } catch (UnsupportedEncodingException e) {
                // nothing to do here, why wouldn't UTF-8 be available?
            }
        }
        else if (outputType.equals("html")) {
            PrintStream out = System.out;
            if (outputPath != null) {
                try {
                    out = new PrintStream(new FileOutputStream(outputPath), true, Charsets.UTF_8.displayName());
                } catch (FileNotFoundException e) {
                    throw new UnableToCreateReportException(new File(outputPath));
                } catch (UnsupportedEncodingException e) {
                    // nothing to do here, why wouldn't UTF-8 be available?
                }
            }
            reporter = new HtmlReporter(out, lintErrors);
        }
        else if (outputType.equals("xml")) {
            PrintStream out = System.out;
            if (outputPath != null) {
                try {
                    out = new PrintStream(new FileOutputStream(outputPath), true, Charsets.UTF_8.displayName());
                } catch (FileNotFoundException e) {
                    throw new UnableToCreateReportException(new File(outputPath));
                } catch (UnsupportedEncodingException e) {
                    // nothing to do here, why wouldn't UTF-8 be available?
                }
            }
            reporter = new XmlReporter(out, lintErrors);
        }

        return reporter;
    }

    /**
     * Returns a list of strings from the raw option string. In other words,
     * given a raw option string "Rule1, Rule2" (i.e. parsed from <code>--disable "Rule1, Rule2"</code>), this returns
     * a list with { "Rule1", "Rule2" }. If any of the rules passed as strings do not exist, this will function
     * will exit the program.
     */
    public static List<String> getListFromRawOptionStringOrDie(String disabledRules) {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        List<String> disabledRulesStringList = Lists.newArrayList(splitter.split(disabledRules));
        for (String disabledRuleString : disabledRulesStringList) {
            try {
                LintRulesImpl.getInstance().getLintRule(disabledRuleString);
            } catch (NonExistentLintRuleException e) {
                Main.exitProgramWithMessage(e.getMessage(), ExitType.COMMAND_LINE_ERROR);
            }
        }

        return Lists.newArrayList(disabledRulesStringList);
    }
}
