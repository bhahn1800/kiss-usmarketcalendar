package kiss.usmarketcalendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public final class USMarketCalendarUtil {

    public enum ProductType {
        FUTURES, EQUITIES
    }

    public static boolean isClosed(ProductType productType, LocalDateTime localDateTime) {
        return isClosed(productType, localDateTime.toLocalDate());
    }

    public static boolean isClosed(ProductType productType, LocalDate localDate) {
        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) return true;
        else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return ProductType.EQUITIES.equals(productType);
        }

        return isUSHoliday(localDate);
    }

    public static boolean isUSHoliday(LocalDateTime localDateTime) {
        return isUSHoliday(localDateTime.toLocalDate());
    }

    public static boolean isUSHoliday(LocalDate localDate) {
        int year = localDate.getYear();
        if (newYearsDay(year).isEqual(localDate)) return true;
        if (newYearsDayWeekend(year).isEqual(localDate)) return true;
        if (mlkDay(year).isEqual(localDate)) return true;
        if (presidentsDay(year).isEqual(localDate)) return true;
        if (goodFriday(year).isEqual(localDate)) return true;
        if (memorialDay(year).isEqual(localDate)) return true;
        if (juneteenth(year).isEqual(localDate)) return true;
        if (juneteenthWeekday(year).isEqual(localDate)) return true;
        if (independenceDay(year).isEqual(localDate)) return true;
        if (independenceDayWeekday(year).isEqual(localDate)) return true;
        if (laborDay(year).isEqual(localDate)) return true;
        if (thanksgivingDay(year).isEqual(localDate)) return true;
        if (christmasDay(year).isEqual(localDate)) return true;
        return (christmasDayWeekend(year).isEqual(localDate));
    }

    public static LocalDate newYearsDay(int year) {
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    public static LocalDate newYearsDayWeekend(int year) {
        return adjustForWeekendHoliday(newYearsDay(year));
    }

    public static LocalDate mlkDay(int year) {
        return LocalDate.of(year, Month.JANUARY, 1).with(
                TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY));
    }

    public static LocalDate presidentsDay(int year) {
        return LocalDate.of(year, Month.FEBRUARY, 1).with(
                TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY));
    }

    public static LocalDate goodFriday(int year) {
        return easterSunday(year).minusDays(2);
    }

    public static LocalDate easterSunday(int year) {
        if (year < 1584) System.out.println("Easter calculation is only reliable after year 1583");

        int day;
        int month;

        int g = year % 19;
        int c = year / 100;
        int h = (c - (c / 4) - ((8 * c + 13) / 25) + 19 * g + 15) % 30;
        int i = h - (h / 28) * (1 - (h / 28) * (29 / (h + 1)) * ((21 - g) / 11));

        day = i - ((year + (year / 4) + i + 2 - c + (c / 4)) % 7) + 28;
        month = 3;

        if (day > 31) {
            month++;
            day -= 31;
        }

        return LocalDate.of(year, month, day);
    }

    public static LocalDate memorialDay(int year) {
        return LocalDate.of(year, Month.MAY, 1).with(
                TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY));
    }

    public static LocalDate juneteenth(int year) {
        return LocalDate.of(year, Month.JUNE, 19);
    }

    public static LocalDate juneteenthWeekday(int year) {
        return adjustForWeekendHoliday(juneteenth(year));
    }

    public static LocalDate independenceDay(int year) {
        return LocalDate.of(year, Month.JULY, 4);
    }

    public static LocalDate independenceDayWeekday(int year) {
        return adjustForWeekendHoliday(independenceDay(year));
    }

    public static LocalDate laborDay(int year) {
        return LocalDate.of(year, Month.SEPTEMBER, 1).with(
                TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    public static LocalDate thanksgivingDay(int year) {
        return LocalDate.of(year, Month.NOVEMBER, 1).with(
                TemporalAdjusters.dayOfWeekInMonth(4, DayOfWeek.THURSDAY));
    }

    public static LocalDate dayAfterThanksgivingDay(int year) {
        return thanksgivingDay(year).plusDays(1);
    }

    public static LocalDate christmasDay(int year) {
        return LocalDate.of(year, Month.DECEMBER, 25);
    }

    public static LocalDate christmasDayWeekend(int year) {
        return adjustForWeekendHoliday(christmasDay(year));
    }

    public static LocalDateTime endTimeGLOBEX(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return null;
        }

        if (date.getDayOfWeek() == DayOfWeek.FRIDAY && christmasDay(date.getYear()).minusDays(1).isEqual(date)) {
            return null;
        }

        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }

        LocalDateTime localDateTime = date.atStartOfDay();
        if (isUSHoliday(localDateTime)) {
            localDateTime = localDateTime.withHour(13);
        }

        return localDateTime;
    }
    
    public static LocalDate rollForward(ProductType productType, LocalDate localDate) {
        return rollForward(productType, localDate.atStartOfDay(), 1).toLocalDate();
    }

    public static LocalDate rollForward(ProductType productType, LocalDate localDate, final Integer numDays) {
        return rollForward(productType, localDate.atStartOfDay(), numDays).toLocalDate();
    }

    public static LocalDateTime rollForward(ProductType productType, LocalDateTime localDateTime, final Integer numDays) {
        localDateTime = localDateTime.plusDays(numDays);

        while (isClosed(productType, localDateTime)) {
            localDateTime = localDateTime.plusDays(1);
        }

        if (localDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            localDateTime = localDateTime.plusDays(1);
        }

        return localDateTime.withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    public static LocalDate rollBackward(ProductType productType, LocalDate localDate) {
        return rollBackward(productType, localDate.atStartOfDay(), 1).toLocalDate();
    }

    public static LocalDate rollBackward(ProductType productType, LocalDate localDate, final Integer numDays) {
        return rollBackward(productType, localDate.atStartOfDay(), numDays).toLocalDate();
    }

    public static LocalDateTime rollBackward(ProductType productType, LocalDateTime localDateTime, final Integer numDays) {
        localDateTime = localDateTime.minusDays(numDays);

        while (isClosed(productType, localDateTime)) {
            localDateTime = localDateTime.minusDays(1);
        }

        if (localDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            localDateTime = localDateTime.minusDays(1);
        }

        return localDateTime.withHour(18).withMinute(0).withSecond(0).withNano(0);
    }

    private static LocalDate adjustForWeekendHoliday(LocalDate localDate) {
        if (localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            localDate = localDate.minusDays(1);
        } else if (localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            localDate = localDate.plusDays(1);
        }

        return localDate;
    }

    private USMarketCalendarUtil() {
        super();
    }

}
