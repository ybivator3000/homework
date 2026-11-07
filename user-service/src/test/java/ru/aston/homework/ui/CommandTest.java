package ru.aston.homework.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    void shouldReturnCreateWhenNumberIsOne() {
        assertEquals(Command.CREATE, Command.fromNumber(1));
    }

    @Test
    void shouldReturnFindByFieldWhenNumberIsThree() {
        assertEquals(Command.FIND_BY_FIELD, Command.fromNumber(3));
    }

    @Test
    void shouldReturnExitWhenNumberIsSix() {
        assertEquals(Command.EXIT, Command.fromNumber(6));
    }

    @Test
    void shouldReturnNullWhenNumberIsOutOfRange() {
        assertNull(Command.fromNumber(7));
    }

    @Test
    void shouldReturnNullWhenNumberIsZero() {
        assertNull(Command.fromNumber(0));
    }

    @Test
    void shouldReturnNullWhenNumberIsNegative() {
        assertNull(Command.fromNumber(-1));
    }

    @Test
    void shouldReturnShowAllWhenNumberIsTwo() {
        assertEquals(Command.SHOW_ALL, Command.fromNumber(2));
    }

    @Test
    void shouldReturnUpdateWhenNumberIsFour() {
        assertEquals(Command.UPDATE, Command.fromNumber(4));
    }

    @Test
    void shouldReturnDeleteWhenNumberIsFive() {
        assertEquals(Command.DELETE, Command.fromNumber(5));
    }
}