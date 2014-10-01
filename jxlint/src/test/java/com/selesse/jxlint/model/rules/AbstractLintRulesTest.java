package com.selesse.jxlint.model.rules;

import com.google.common.collect.Lists;
import com.selesse.jxlint.samplerules.xml.XmlLintRulesTestImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AbstractLintRulesTest {
    private LintRules lintRules = new XmlLintRulesTestImpl();

    @Test
    public void testLintRulesGetsAllRuleSizeProperly() {
        assertEquals(4, lintRules.getAllRules().size());
    }

    @Test
    public void testLintRulesGetsAllEnabledRulesSizeProperly() {
        assertEquals(3, lintRules.getAllEnabledRules().size());
    }

    @Test
    public void testLintRulesGetsAllDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("XML version specified");
        assertEquals(3, lintRules.getAllRulesExcept(disabledRules).size());
    }

    @Test
    public void testLintRulesGetsAllEnabledMinusDisabledRulesSizeProperly() {
        List<String> disabledRules = Lists.newArrayList("Unique attribute");
        assertEquals(2, lintRules.getAllEnabledRulesExcept(disabledRules).size());
    }

    @Test
    public void testLintRulesGetsAllEnabledPlusEnabledRulesSizeProperly() {
        List<String> enabledRules = Lists.newArrayList("XML version specified");
        assertEquals(4, lintRules.getAllEnabledRulesAsWellAs(enabledRules).size());
    }

    @Test
    public void testGetAllRulesBySeverity() {
        List<LintRule> emptyErrorRules = lintRules.getAllRulesWithSeverity(Severity.ERROR);
        assertEquals(1, emptyErrorRules.size());

        List<LintRule> fatalRules = lintRules.getAllRulesWithSeverity(Severity.FATAL);
        assertEquals(1, fatalRules.size());

        List<LintRule> warningRules = lintRules.getAllRulesWithSeverity(Severity.WARNING);
        assertEquals(2, warningRules.size());
    }

    @Test
    public void testLintRulesGetsCheckRuleProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified");
        assertEquals(1, lintRules.getOnlyRules(checkRules).size());
        assertEquals("XML version specified", lintRules.getOnlyRules(checkRules).get(0).getName());
    }

    @Test
    public void testLintRulesGetsMultipleCheckRulesProperly() {
        List<String> checkRules = Lists.newArrayList("XML version specified", "Unique attribute");
        assertEquals(2, lintRules.getOnlyRules(checkRules).size());
    }

    @Test
    public void testInvalidLintRulesGetsIgnored() {
        List<String> badRules = Lists.newArrayList("foo", "foobar", "bar");

        assertEquals(0, lintRules.getOnlyRules(badRules).size());
        assertEquals(lintRules.getAllEnabledRules().size(), lintRules.getAllEnabledRulesAsWellAs(badRules).size());
    }

    @Test
    public void testBadCategoriesGetIgnored() {
        List<String> badCategories = Lists.newArrayList("hoo", "haw");

        assertEquals(0, lintRules.getRulesWithCategoryNames(badCategories).size());
    }

    @Test
    public void testGetCategoriesReturnsRightAmountOfRules() {
        List<String> categories = Lists.newArrayList("LINT");

        assertEquals(2, lintRules.getRulesWithCategoryNames(categories).size());
    }
}