package com.kstenschke.realigner;

import com.kstenschke.realigner.TextualHelper;

import org.junit.* ;
import static org.junit.Assert.* ;

public class TextualHelperTest {

	/**
	 * Tests for testContainsHtml
	 */

	@Test
	public void testContainsHtmlTag() throws Exception {
		assertTrue(TextualHelper.containsHtmlTag("<div>")) ;
	}

	@Test
	public void testContainsHtmlTagTagWithStyle() throws Exception {
		assertTrue(TextualHelper.containsHtmlTag("<td style=\"font-weight:bold;\">")) ;
	}

	@Test
	public void testContainsHtmlTagMultipleTags() throws Exception {
		assertTrue(TextualHelper.containsHtmlTag("<table><tr><td>")) ;
	}

	@Test
	public void testContainsHtmlNoTag() throws Exception {
		assertTrue(!TextualHelper.containsHtmlTag("abc")) ;
	}

	@Test
	public void testContainsHtmlJustLtGt() throws Exception {
		assertTrue(!TextualHelper.containsHtmlTag("<>")) ;
	}



	/**
	 * Tests for getClosingTagPendent
	 */

	@Test
	public void getClosingTagPendentDiv() throws Exception {
		assertTrue( TextualHelper.getClosingTagPendent("<div>").equals("</div>")) ;
	}

	@Test
	public void getClosingTagPendentDivWithStyle() throws Exception {
		assertTrue( TextualHelper.getClosingTagPendent("<div style=\"font-weight:bold;\">").equals("</div>")) ;
	}

	@Test
	public void getClosingTagPendentNoDivJustText() throws Exception {
		// @note TextualHelper.getClosingTagPendent itself does not contain any check whether this actually is an HTML tag
		assertTrue( TextualHelper.getClosingTagPendent("div").equals("</div>")) ;
	}

}
