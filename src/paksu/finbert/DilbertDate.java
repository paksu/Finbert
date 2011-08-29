package paksu.finbert;

import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;

import org.joda.time.LocalDate;

public final class DilbertDate {
	private final LocalDate date;

	private DilbertDate(LocalDate date) {
		this.date = date;
	}

	public int getDay() {
		return date.getDayOfMonth();
	}

	public int getMonth() {
		return date.getMonthOfYear();
	}

	public int getYear() {
		return date.getYear();
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

	private static boolean isValid(LocalDate date) {
		return !(isWeekend(date) || isInFuture(date) || isOlderThanYear(date));
	}

	private static boolean isWeekend(LocalDate date) {
		return date.getDayOfWeek() == SATURDAY || date.getDayOfWeek() == SUNDAY;
	}

	private static boolean isInFuture(LocalDate date) {
		return date.isAfter(LocalDate.now());
	}

	private static boolean isOlderThanYear(LocalDate date) {
		return LocalDate.now().minusYears(1).isAfter(date);
	}

	public String toUriString() {
		return String.format("%d-%d-%d", date.getYear(), date.getMonthOfYear(), date.getDayOfWeek());
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

	public static DilbertDate exactlyForDate(int year, int month, int day) {
		LocalDate date = new LocalDate(year, month, day);
		if (!isValid(date)) {
			throw new IllegalArgumentException("not valid date " + date);
		}

		return new DilbertDate(date);
	}

	public static DilbertDate nearestToDate(int year, int month, int day) {
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
