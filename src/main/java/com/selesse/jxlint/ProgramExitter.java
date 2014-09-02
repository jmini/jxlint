package com.selesse.jxlint;

import com.selesse.jxlint.model.ExitType;
import com.selesse.jxlint.settings.Profiler;

/**
 * Exits the program with provided {@link ExitType}s.
 */
public class ProgramExitter {
    private static String outputMessage;

    /**
     * Exits the program without displaying anything. Calls System.exit on {@link com.selesse.jxlint.model.ExitType}'s
     * error code.
     */
    public static void exitProgram(ExitType exitType) {
        exitProgramWithMessage("", exitType);
    }

    /**
     * Exits the program, displaying the message. Calls System.exit on {@link com.selesse.jxlint.model.ExitType}'s
     * error code.
     */
    public static void exitProgramWithMessage(String outputMessage, ExitType exitType) {
        // This is done both here, and in Jxlint#parseArgumentsAndDispatch.
        // Why? Because we have two possible program flows: we exit with System.exit, or we don't.
        if (Profiler.isEnabled()) {
            Profiler.setStopTime(System.currentTimeMillis());
            outputMessage += Profiler.getGeneratedProfileReport();
        }

        ProgramExitter.outputMessage = outputMessage;

        if (outputMessage.trim().length() > 0) {
            System.out.println(outputMessage);
        }
        System.exit(exitType.getErrorCode());
    }

    public static String getOutputMessage() {
        return outputMessage;
    }
}
