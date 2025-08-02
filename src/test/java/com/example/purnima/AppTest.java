package com.example.purnima;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the App class.
 */
public class AppTest {
    
    /**
     * Test the getGreeting method.
     */
    @Test
    public void testGetGreeting() {
        String result = App.getGreeting("World");
        assertEquals("Hello, World! Welcome to Purnima.", result);
    }
    
    /**
     * Test the getGreeting method with empty string.
     */
    @Test
    public void testGetGreetingWithEmptyString() {
        String result = App.getGreeting("");
        assertEquals("Hello, ! Welcome to Purnima.", result);
    }
    
    /**
     * Test the getGreeting method with null.
     */
    @Test
    public void testGetGreetingWithNull() {
        String result = App.getGreeting(null);
        assertEquals("Hello, null! Welcome to Purnima.", result);
    }
} 