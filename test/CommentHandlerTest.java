package paksu.finbert;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import paksu.finbert.CommentHandler;

public class CommentHandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetComment() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetComments() {
		System.out.println("Testing setComment()");
		ch = new CommentHandler();
		assertNotNull(ch.getComments("2011-08-18"));
	}

}
