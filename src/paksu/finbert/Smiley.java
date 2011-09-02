package paksu.finbert;

import java.util.ArrayList;
import java.util.List;

public class Smiley {
	private static final List<Smiley> smileys = new ArrayList<Smiley>();

	private final int drawableId;
	private final int stringId;
	private final String presentation;

	static {
		smileys.add(new Smiley(R.drawable.monkey, R.string.monkey, ":(|)"));
		smileys.add(new Smiley(R.drawable.pig, R.string.pig, ":(:)"));
		smileys.add(new Smiley(R.drawable.dogbert, R.string.dogbert, "38]"));
		smileys.add(new Smiley(R.drawable.pacman, R.string.pacman, "'<"));
		smileys.add(new Smiley(R.drawable.catbert, R.string.catbert, ">^oo^<"));
		smileys.add(new Smiley(R.drawable.wc, R.string.toilet, "(wc)"));
	}

	private Smiley(int drawableId, int stringId, String presentation) {
		this.drawableId = drawableId;
		this.stringId = stringId;
		this.presentation = presentation;
	}

	public static List<Smiley> getAllSupported() {
		return new ArrayList<Smiley>(smileys);
	}

	public int getDrawableId() {
		return drawableId;
	}

	public int getStringId() {
		return stringId;
	}

	public String getPresentation() {
		return presentation;
	}
}
