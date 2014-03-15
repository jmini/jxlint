package com.selesse.jxlint.model.rules;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.selesse.jxlint.model.EnumUtils;

import java.io.File;
import java.util.List;

public abstract class LintRule {
    private String name;
    private String summary;
    private String detailedDescription;
    private Severity severity;
    private Category category;
    private boolean enabled = true;
    protected List<LintError> failedRules;

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category) {
        this.name = name;
        this.summary = summary;
        this.detailedDescription = detailedDescription;
        this.severity = severity;
        this.category = category;
        this.failedRules = Lists.newArrayList();
    }

    public LintRule(String name, String summary, String detailedDescription, Severity severity, Category category,
                    boolean isEnabledByDefault) {
        this(name, summary, detailedDescription, severity, category);
        this.enabled = isEnabledByDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSummaryOutput() {
        return String.format("\"%s\"%s : %s", getName(), isEnabled() ? "" : "*", getSummary());
    }

    public String getDetailedOutput() {
        List<String> detailedOutput = Lists.newArrayList(
                getName(),
                new String(new char[getName().length()]).replace("\0", "-"),
                "Summary: " + getSummary(),
                isEnabled() ? "" : "\n** Disabled by default **\n",
                "Severity: " + EnumUtils.toHappyString(getSeverity()),
                "Category: " + EnumUtils.toHappyString(getCategory()),
                "",
                getDetailedDescription()
        );

        return Joiner.on("\n").join(detailedOutput);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (detailedDescription != null ? detailedDescription.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (failedRules != null ? failedRules.hashCode() : 0);
        return result;
    }

    /**
     * Ad-hoc <code>equals</code>: we return true as long as the {@link String}, or {@link #getName()} is * the same.
     * This means that <code>"RuleName".equals(new LintRule("RuleName", ...)) == true</code>
     *
     * <p>
     *     Any object that isn't an <code>instanceof</code> {@link String} or {@link LintRule} will return false.
     * </p>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LintRule) {
            return ((LintRule) obj).getName().equalsIgnoreCase(getName());
        }
        return super.equals(obj);
    }

    /**
     * Returns true if this rule's name is a member of this list of strings.
     * It's like {@link List#contains(Object)}, but on the {@link com.selesse.jxlint.model.rules.LintRule}
     * rather than the list. Does a case-insensitive string comparison.
     */
    public boolean hasNameInList(List<String> ruleStrings) {
        for (String string : ruleStrings) {
            if (string.equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    public void validate() {
        for (File file : getFilesToValidate()) {
            Optional<LintError> errorIfLintFailed = getLintError(file);
            if (errorIfLintFailed.isPresent()) {
                failedRules.add(errorIfLintFailed.get());
            }
        }
    }

    public List<LintError> getFailedRules() {
        return failedRules;
    }

    public File getSourceDirectory() {
        return LintRulesImpl.getInstance().getSourceDirectory();
    }

    public abstract List<File> getFilesToValidate();

    public abstract Optional<LintError> getLintError(File file);

    public boolean passesValidation(File file) {
        Optional<LintError> errorIfLintFailed = getLintError(file);
        return !errorIfLintFailed.isPresent();
    }
}
