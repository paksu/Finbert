package paksu.finbert;

import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public final class DilbertDate {
	private final LocalDate date;

	private DilbertDate(LocalDate date) {
		this.date = date;
	}

	public DilbertDate next() {
		LocalDate next = date.plusDays(1);
		while (isWeekend(next)) {
			next = next.plusDays(1);
		}

		return new DilbertDate(next);
	}

	public DilbertDate previous() {
		LocalDate previous = date.minusDays(1);
		while (isWeekend(previous)) {
			previous = previous.minusDays(1);
		}

		return new DilbertDate(previous);
	}

	private static boolean isWeekend(LocalDate date) {
		return date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;
	}

	@Override
	public String toString() {
		/* TODO better formatting */
		return date.toString();
	}

	public static DilbertDate newest() {
		LocalDate newest = LocalDate.now();
		while (isWeekend(newest)) {
			newest = newest.minusDays(1);
		}
		return new DilbertDate(newest);
	}

	public static DilbertDate exactlyForDate(DateTime dt) {
		throw new UnsupportedOperationException("not implemented");
	}

	public static DilbertDate nearestToDate(DateTime dt) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || !(obj instanceof DilbertDate)) {
			return false;
		}

		DilbertDate other = (DilbertDate) obj;
		return other.date.equals(date);
	}
}
