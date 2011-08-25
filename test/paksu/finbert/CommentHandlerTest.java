package paksu.finbert;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.JsonParseException;

public class CommentHandlerTest {

	@Test
	public void testSetComment() {
		System.out.println("Testing setComment()");
		CommentHandler ch = new CommentHandler();
		boolean response;
		try {
			response = ch.setComment(new Comment("testComment", "joona", "2011-08-20"));
			System.out.println(response);
			assertNotNull(response);
		} catch (NetworkException e) {
			assertNotNull(e);
		}

	}

	@Test
	public void testGetComments() {
		System.out.println("Testing getComments()");
		CommentHandler ch = new CommentHandler();
		String response;
		try {
			response = ch.getComments("2011-08-20").toString();
			System.out.println(response);
			assertNotNull(response);
		} catch (NetworkException e) {
			assertNotNull(e);
		} catch (JsonParseException e) {
			assertNotNull(e);
		}

	}

	@Test
	public void testGetCommentCount() {
		System.out.println("Testing getComments()");
		CommentHandler ch = new CommentHandler();
		String response;
		try {
			response = ch.getCommentCount("2011-08-20").toString();
			System.out.println(response);
			assertNotNull(response);
		} catch (NetworkException e) {
			assertNotNull(e);
		} catch (JsonParseException e) {
			assertNotNull(e);
		}

	}

}
