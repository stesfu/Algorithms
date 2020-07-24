package huffman;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

public class HuffmanTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
 // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    // =================================================
    // Unit Tests
    // =================================================
    
    // Compression Tests
    // -----------------------------------------------
    @Test
    public void comp_t0() {
        Huffman h = new Huffman("AB");
        // byte 0: 0000 0010 = 2 (message length = 2)
        // byte 1: 0100 0000 = 64 (0 = "A", 1 = "B")
        // [!] Only first 2 bits of byte 1 are meaningful
        byte[] compressed = {2, 64};
        assertArrayEquals(compressed, h.compress("AB"));
    }
    
    @Test
    public void comp_t1() {
        Huffman h = new Huffman("AB");
        // byte 0: 0000 0010 = 2 (message length = 2)
        // byte 1: 1000 0000 = -128 (0 = "A", 1 = "B")
        // [!] Only first 2 bits of byte 1 are meaningful
        byte[] compressed = {2, -128};
        assertArrayEquals(compressed, h.compress("BA"));
    }
    
    @Test
    public void comp_t2() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0000 0110 = 6 (message length = 6)
        // byte 1: 1000 0111 = -121 (10 = "A", 0 = "B", C = 11)
        // byte 2: 1000 0000 = -128
        // [!] Only first bit of byte 2 is meaningful
        byte[] compressed = {6, -121, -128};
        assertArrayEquals(compressed, h.compress("ABBBCC"));
    }
    
    @Test
    public void comp_t3() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0000 0110 = 6 (message length = 6)
        // byte 1: 0100 1101 = 77 (10 = "A", 0 = "B", C = 11)
        // byte 2: 1000 0000 = -128
        byte[] compressed = {6, 77, -128};
        assertArrayEquals(compressed, h.compress("BABCBC"));
    }
    
    @Test
    public void comp_t4() {
        Huffman h = new Huffman("ACADACBABE");
        // Encoding Map:
        // {A=0, B=110, C=10, D=1110, E=1111}
        byte[] compressed = {10, 78, 89, -68};
        assertArrayEquals(compressed, h.compress("ACADACBABE"));
    }
    
    @Test
    public void comp_t5() {
        Huffman h = new Huffman("ABCDEFGHIJ");
        // Encoding Map:
        // {A=1100, B=001, C=011, D=1110, E=101, F=010, G=100, H=1111, I=000, J=1101}
        byte[] compressed = {10, -62, -6, -87, -29, 64};
        assertArrayEquals(compressed, h.compress("ABCDEFGHIJ"));
    }
    
    @Test
    public void comp_t6() {
        Huffman h = new Huffman("ABCDEFGHIJ");
        // Encoding Map:
        // {A=1100, B=001, C=011, D=1110, E=101, F=010, G=100, H=1111, I=000, J=1101}
        byte[] compressed = {6, -52, -62, 72};
        // Not that we would always want to use this encoding map for this corpus, but
        // we *could*
        assertArrayEquals(compressed, h.compress("AAABBB"));
    }
    
    @Test
    public void comp_t7() {
        Huffman h = new Huffman("ABABBABA");
        // Encoding Map:
        // {A=0, B=1}
        byte[] compressed = {4, 96};
        assertArrayEquals(compressed, h.compress("ABBA"));
    }
    
    @Test
    public void comp_t8() {
        Huffman h = new Huffman("1223334444555556666667777777");
        // Encoding Map:
        // {1=0010, 2=0011, 3=000, 4=110, 5=111, 6=01, 7=10}
        byte[] compressed = {7, 35, 27, -80};
        assertArrayEquals(compressed, h.compress("1234567"));
    }
    
    @Test
    public void comp_t9() {
        Huffman h = new Huffman("This is a full sentence. How odd to see it in a test case! Punctuation and all. Wow.");
        // Encoding Map:
        // meh too lazy
        byte[] compressed = {
            84, 69, 56, -110, 36, -47, 43, -86, -109, -7, -33, -115, -6, 12, -67, 
            -93, -42, -89, 57, 63, -28, 113, 24, 105, -33, 60, 55, 103, -40, 
            16, -66, 55, -81, -67, 15, -122, -27, 77, 85, -95, 99, -37, 64
        };
        assertArrayEquals(compressed, h.compress("This is a full sentence. How odd to see it in a test case! Punctuation and all. Wow."));
    }
    
    
    // Decompression Tests
    // -----------------------------------------------
    @Test
    public void decomp_t0() {
        Huffman h = new Huffman("AB");
        // byte 0: 0000 0010 = 2 (message length = 2)
        // byte 1: 0100 0000 = 64 (0 = "A", 1 = "B")
        byte[] compressed = {2, 64};
        assertEquals("AB", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t1() {
        Huffman h = new Huffman("AB");
        // byte 0: 0000 0010 = 2 (message length = 2)
        // byte 1: 1000 0000 = -128 (0 = "A", 1 = "B")
        byte[] compressed = {2, -128};
        assertEquals("BA", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t2() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0000 0110 = 6 (message length = 6)
        // byte 1: 1000 0111 = -121 (10 = "A", 0 = "B", C = 11)
        // byte 2: 1000 0000 = -128
        byte[] compressed = {6, -121, -128};
        assertEquals("ABBBCC", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t3() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0000 0110 = 6 (message length = 6)
        // byte 1: 0100 1101 = 77 (10 = "A", 0 = "B", C = 11)
        // byte 2: 1000 0000 = -128
        byte[] compressed = {6, 77, -128};
        assertEquals("BABCBC", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t4() {
        Huffman h = new Huffman("ACADACBABE");
        // Encoding Map:
        // {A=0, B=110, C=10, D=1110, E=1111}
        byte[] compressed = {10, 78, 89, -68};
        assertEquals("ACADACBABE", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t5() {
        Huffman h = new Huffman("ABCDEFGHIJ");
        // Encoding Map:
        // {A=1100, B=001, C=011, D=1110, E=101, F=010, G=100, H=1111, I=000, J=1101}
        byte[] compressed = {10, -62, -6, -87, -29, 64};
        assertEquals("ABCDEFGHIJ", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t6() {
        Huffman h = new Huffman("ABCDEFGHIJ");
        // Encoding Map:
        // {A=1100, B=001, C=011, D=1110, E=101, F=010, G=100, H=1111, I=000, J=1101}
        byte[] compressed = {6, -52, -62, 72};
        assertEquals("AAABBB", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t7() {
        Huffman h = new Huffman("ABABBABA");
        // Encoding Map:
        // {A=0, B=1}
        byte[] compressed = {4, 96};
        assertEquals("ABBA", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t8() {
        Huffman h = new Huffman("1223334444555556666667777777");
        // Encoding Map:
        // {1=0010, 2=0011, 3=000, 4=110, 5=111, 6=01, 7=10}
        byte[] compressed = {7, 35, 27, -80};
        assertEquals("1234567", h.decompress(compressed));
    }
    
    @Test
    public void decomp_t9() {
        Huffman h = new Huffman("This is a full sentence. How odd to see it in a test case! Punctuation and all. Wow.");
        // Encoding Map:
        // Too lazy
        byte[] compressed = {
            84, 69, 56, -110, 36, -47, 43, -86, -109, -7, -33, -115, -6, 12, -67, 
            -93, -42, -89, 57, 63, -28, 113, 24, 105, -33, 60, 55, 103, -40, 
            16, -66, 55, -81, -67, 15, -122, -27, 77, 85, -95, 99, -37, 64
        };
        assertEquals("This is a full sentence. How odd to see it in a test case! Punctuation and all. Wow.", h.decompress(compressed));
    }
    
}
