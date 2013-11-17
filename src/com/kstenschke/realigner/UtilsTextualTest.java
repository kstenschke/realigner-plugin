package com.kstenschke.realigner;

import org.junit.*;

import static org.junit.Assert.*;

public class UtilsTextualTest {

	/**
	 * Tests for testContainsHtml
	 */

	@Test
	public void testContainsHtmlTag() throws Exception {
		assertTrue(UtilsTextual.containsHtmlTag("<div>"));
	}

	@Test
	public void testContainsHtmlTagTagWithStyle() throws Exception {
		assertTrue(UtilsTextual.containsHtmlTag("<td style=\"font-weight:bold;\">"));
	}

	@Test
	public void testContainsHtmlTagMultipleTags() throws Exception {
		assertTrue(UtilsTextual.containsHtmlTag("<table><tr><td>"));
	}

	@Test
	public void testContainsHtmlNoTag() throws Exception {
		assertTrue(!UtilsTextual.containsHtmlTag("abc"));
	}

	@Test
	public void testContainsHtmlJustLtGt() throws Exception {
		assertTrue(!UtilsTextual.containsHtmlTag("<>"));
	}

	/**
	 * Tests for getClosingTagPendent
	 */

	@Test
	public void getClosingTagPendentDiv() throws Exception {
		assertTrue(UtilsTextual.getClosingTagPendent("<div>").equals("</div>"));
	}

	@Test
	public void getClosingTagPendentDivWithStyle() throws Exception {
		assertTrue(UtilsTextual.getClosingTagPendent("<div style=\"font-weight:bold;\">").equals("</div>"));
	}

	@Test
	public void getClosingTagPendentNoDivJustText() throws Exception {
		// @note UtilsTextual.getClosingTagPendent itself does not contain any check whether this actually is an HTML tag
		assertTrue(UtilsTextual.getClosingTagPendent("div").equals("</div>"));
	}

}
