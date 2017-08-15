package com.voidaspect.public24.service.agent.format;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.junit.Assert.*;

public class CurrencyFormatServiceTest {

    private Function<BigDecimal, String> numberFormat = new CurrencyFormatService();

    @Test
    public void testZeroStripping() throws Exception {
        assertEquals("0.10", numberFormat.apply(new BigDecimal("0.100000")));
        assertEquals("0.11", numberFormat.apply(new BigDecimal("0.110000")));
        assertEquals("0.111", numberFormat.apply(new BigDecimal("0.111000")));
    }

    @Test
    public void testFormatPositiveInteger() throws Exception {
        assertEquals("1.00", numberFormat.apply(BigDecimal.ONE));
        assertEquals("15.00", numberFormat.apply(new BigDecimal("15")));
        assertEquals("12000.00", numberFormat.apply(new BigDecimal("12000")));
        assertEquals("1330647474776585876896989679879797797977797779771313.00", numberFormat.apply(new BigDecimal("1330647474776585876896989679879797797977797779771313")));
    }

    @Test
    public void testFormatPositiveFloat() throws Exception {
        assertEquals("1.10", numberFormat.apply(new BigDecimal("1.1")));
        assertEquals("1.103242", numberFormat.apply(new BigDecimal("1.103242")));
        assertEquals("1330647474776585876896989679.879797797977797779771313", numberFormat.apply(new BigDecimal("1330647474776585876896989679.879797797977797779771313")));
    }

    @Test
    public void testFormatZero() throws Exception {
        assertEquals("0.00", numberFormat.apply(BigDecimal.ZERO));
    }
}