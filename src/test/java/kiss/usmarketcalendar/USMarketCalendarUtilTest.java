package kiss.usmarketcalendar;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;

public class USMarketCalendarUtilTest {

    @Test
    public void testEquities() {
        test(USMarketCalendarUtil.ProductType.EQUITIES);
    }

    @Test
    public void testFutures() {
        test(USMarketCalendarUtil.ProductType.FUTURES);
    }

    private void test(USMarketCalendarUtil.ProductType productType) {
        int currentYear = LocalDate.now().getYear();
        int year = currentYear - 50;

        while (year <= currentYear) {

            LocalDateTime newYearsDay = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
            assertTrue("newYearsDay isUSHoliday() :: year " + year + ", product type = " + productType,
                    USMarketCalendarUtil.isUSHoliday(newYearsDay));

            if (newYearsDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                // No holiday observed, pursuant to NYSE Rule 7.2, NYSE American Rule 7.2E, NYSE Arca Rules 7.2-O and
                // 7.2-E, NYSE Chicago Rule 7.2, and NYSE National Rule 7.2.
                assertFalse("Friday before newYearsDay Saturday, year " + (year - 1),
                        USMarketCalendarUtil.isClosed(productType, newYearsDay.minusDays(1)));
            }

            if (newYearsDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                assertTrue("Monday after newYearsDay Sunday, year " + year,
                        USMarketCalendarUtil.isClosed(productType, newYearsDay.plusDays(1)));
            }

            year++;
        }
    }

    @Test
    public void endTimeGLOBEX() {
        LocalDate christmasDay = USMarketCalendarUtil.christmasDay(2021);
        LocalDateTime endTime = USMarketCalendarUtil.endTimeGLOBEX(christmasDay);
        assertNull(endTime);

        LocalDate sunday = christmasDay.plusDays(1);
        endTime = USMarketCalendarUtil.endTimeGLOBEX(sunday);
        assertNotNull(endTime);
        assertEquals(endTime.getDayOfWeek(), DayOfWeek.MONDAY);

        LocalDate friday = christmasDay.minusDays(1);
        endTime = USMarketCalendarUtil.endTimeGLOBEX(friday);
        assertNull(endTime);
    }

    @Test
    public void rollForward() {
        LocalDate christmasDay = USMarketCalendarUtil.christmasDay(2021);
        LocalDate endTime = USMarketCalendarUtil.rollForward(USMarketCalendarUtil.ProductType.FUTURES, christmasDay, 1);
        assertNotNull(endTime);
        assertEquals(endTime.getDayOfWeek(), DayOfWeek.MONDAY);
    }

    @Test
    public void rollBackward() {
        LocalDate christmasDay = USMarketCalendarUtil.christmasDay(2021);
        LocalDate endTime = USMarketCalendarUtil.rollBackward(USMarketCalendarUtil.ProductType.FUTURES, christmasDay, 1);
        assertNotNull(endTime);
        assertEquals(endTime.getDayOfWeek(), DayOfWeek.THURSDAY);
    }

}