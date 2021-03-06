package com.selesse.jxlint.settings;

import java.io.File;

/**
 * An application's program settings. Contains information that should be customized for every implementation of
 * jxlint, like the version and the program name.
 */
public interface ProgramSettings {
    /**
     * The program's version. Used with the version command line switch.
     */
    String getProgramVersion();
    /**
     * The program's name. Used with the version command line output.
     */
    String getProgramName();

    void initializeForWeb(File projectRoot);
}
