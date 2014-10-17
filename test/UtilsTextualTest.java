package com.kstenschke.realigner;

import com.kstenschke.realigner.utils.UtilsTextual;
import org.junit.*;

import static org.junit.Assert.*;

public class UtilsTextualTest {

	/**
	 * Tests for testContainsHtml
	 */

	@Test
	public void testContainsHtmlTag() throws Exception {
		assertTrue(UtilsTextual.isHtmlTag("<div>"));
	}

	@Test
	public void testContainsHtmlTagTagWithStyle() throws Exception {
		assertTrue(UtilsTextual.isHtmlTag("<td style=\"font-weight:bold;\">"));
	}

	@Test
	public void testContainsHtmlTagMultipleTags() throws Exception {
		assertTrue(UtilsTextual.isHtmlTag("<table><tr><td>"));
	}

	@Test
	public void testContainsHtmlNoTag() throws Exception {
		assertTrue(!UtilsTextual.isHtmlTag("abc"));
	}

	@Test
	public void testContainsHtmlJustLtGt() throws Exception {
		assertTrue(!UtilsTextual.isHtmlTag("<>"));
	}

	/**
	 * Tests for getHtmlTagCounterpart
	 */

	@Test
	public void getClosingTagPendentDiv() throws Exception {
		assertTrue(UtilsTextual.getHtmlTagCounterpart("<div>").equals("</div>"));
	}

	@Test
	public void getClosingTagPendentDivWithStyle() throws Exception {
		assertTrue(UtilsTextual.getHtmlTagCounterpart("<div style=\"font-weight:bold;\">").equals("</div>"));
	}

	@Test
	public void getClosingTagPendentNoDivJustText() throws Exception {
		// @note UtilsTextual.getHtmlTagCounterpart itself does not contain any check whether this actually is an HTML tag
		assertTrue(UtilsTextual.getHtmlTagCounterpart("div").equals("</div>"));
	}

}
