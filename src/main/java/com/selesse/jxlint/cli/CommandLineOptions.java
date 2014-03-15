package com.selesse.jxlint.cli;

import com.google.common.base.Charsets;
import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.settings.ProgramSettings;
import org.apache.commons.cli.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;

@SuppressWarnings("AccessStaticViaInstance")
/**
 * Generator of {@link org.apache.commons.cli.Options} and its Comparator. These options
 * are jxlint's defaults, but can be extended.
 */
public class CommandLineOptions {
    public static Options generateJxlintOptions() {
        Options options = new Options();

        // The sequence in which these options are added represents the order in which we want them to be displayed.
        // If this order is modified, make sure to modify "CommandLineOptions.getOptionsOrder".
        options.addOption("h", "help", false, "Usage information, help message.");
        options.addOption("v", "version", false, "Output version information.");
        options.addOption("l", "list", false, "Lists lint rules with a short, summary explanation.");
        options.addOption(OptionBuilder.withLongOpt("show").
                withDescription("Lists a verbose rule explanation.").
                hasOptionalArg().
                withArgName("RULE[s]").create('s')
        );
        options.addOption(OptionBuilder.withLongOpt("check").
                withDescription("Only check for these rules.").
                hasArg().
                withArgName("RULE[s]").create('c')
        );
        options.addOption(OptionBuilder.withLongOpt("disable").
                withDescription("Disable the list of rules.").
                hasArg().
                withArgName("RULE[s]").create('d')
        );
        options.addOption(OptionBuilder.withLongOpt("enable").
                withDescription("Enable the list of rules.").
                hasArg().
                withArgName("RULE[s]").create('e')
        );
        options.addOption("w", "nowarn", false, "Only check for errors; ignore warnings.");
        options.addOption("Wall", "Wall", false, "Check all warnings, including those off by default.");
        options.addOption("Werror", "Werror", false, "Treat all warnings as errors.");

        OptionGroup outputOptionGroup = new OptionGroup();
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("quiet").
                withDescription("Don't output any progress or reports.").
                create('q')
        );
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("html").
                withDescription("Create an HTML report.").
                hasOptionalArg().
                withArgName("filename").
                create('t')
        );
        outputOptionGroup.addOption(OptionBuilder.withLongOpt("xml").
                withDescription("Create an XML (!!) report.").
                hasOptionalArg().
                withArgName("filename").
                create('x')
        );

        options.addOptionGroup(outputOptionGroup);

        return options;
    }

    public static String getHelpMessage() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(new OptionComparator());

        StringBuilder exitStatusFooter = new StringBuilder("<RULE[s]> should be comma separated, without spaces.\n");
        exitStatusFooter.append("Exit Status:\n");
        for (ExitType exitType : ExitType.values()) {
            exitStatusFooter.append(String.format("%-21d %-30s%n", exitType.getErrorCode(), exitType.getExplanation()));
        }

        String outputString = "";

        // redirect stdout to a temporary stream to capture HelpFormatter.printHelp()
        PrintStream previousOut = System.out;
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            System.setOut(new PrintStream(output, true, Charsets.UTF_8.displayName()));

            helpFormatter.printHelp(ProgramSettings.getProgramName() + " [flags] <directory>", "",
                    generateJxlintOptions(), "\n" + exitStatusFooter.toString().trim());

            outputString = output.toString(Charsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            // nothing to do here, why wouldn't UTF-8 be available?
        } finally {
            System.setOut(previousOut);
        }

        return outputString;
    }

    private static String getOptionsOrder() {
        return "hvlscdewWallWerrorqtx";
    }

    private static class OptionComparator implements Comparator<Option>, Serializable {
        @Override
        public int compare(Option o1, Option o2) {
            return getOptionsOrder().indexOf(o1.getOpt()) - getOptionsOrder().indexOf(o2.getOpt());
        }
    }
}
