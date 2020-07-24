package t3;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

/**
 * Unit tests for T3Player behavior.
 */
public class T3Tests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite + to test that your
    // alpha-beta pruning and memoization are working;
    // If they are, 8 seconds should be more than enough for
    // every test except the empty board as odds
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    @Rule
    public Timeout globalTimeout = Timeout.seconds(8);
    
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
    
    
    /**
     * Basic test to make sure the AI knows the
     * base cases: how to win when presented with the
     * opportunity
     */

    
    @Test
    public void T3PlayerTest_t0() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {2, 1, 0},
            {0, 0, 0},
            {0, 0, 6}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(1, 1, 5), a);
    }
    
    @Test
    public void T3PlayerTest_t1() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {2, 1, 0},
            {0, 5, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 2, 6), a);
    }
    
    @Test
    public void T3PlayerTest_t2() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 1, 0},
            {0, 5, 0},
            {0, 0, 6}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 0, 2), a);
    }
    
    /**
     * Tests to make sure the move-quality-tiebreaking
     * is properly implemented, wherein multiple moves
     * may be available to the agent
     */
    
    @Test
    public void T3PlayerTest_t3() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 0},
            {0, 5, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 0, 2), a);
    }
    
    @Test
    public void T3PlayerTest_t4() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {3, 0, 0},
            {0, 4, 0},
            {0, 0, 1}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 1, 2), a);
    }
    
    @Test
    public void T3PlayerTest_t5() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {2, 0, 0},
            {5, 5, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 2, 6), a);
    }
    
    @Test
    public void T3PlayerTest_t6() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {2, 5, 0},
            {2, 5, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 0, 6), a);
    }
    
    @Test
    public void T3PlayerTest_t7() { //make sure this is right 
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 0},   // 3 0 1 2 
            {2, 5, 0},
            {2, 5, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 0, 6), a);
    }
    
    @Test
    public void T3PlayerTest_t8() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 0},
            {0, 4, 0},
            {1, 6, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(1, 0, 3), a);
    }
    
    @Test
    public void T3PlayerTest_t9() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 0, 2), a);
    }
    
    @Test
    public void T3PlayerTest_t10() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {5, 5, 0},
            {5, 5, 5},
            {0, 0, 5}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 2, 2), a);
    }
    
    @Test
    public void T3PlayerTest_t11() {  
        T3Player t3p = new T3Player(); 
        int[][] state = {
             {0, 5, 0},   
             {5, 6, 0},
             {6, 6, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 0, 1), a);
    }
    
    @Test
    public void T3PlayerTest_t12() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {5, 6, 0},
            {5, 6, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 0, 2), a);
    }
    
    
    
     
   @Test
    public void T3PlayerTest_NO() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {1, 3, 0},
            {1, 2, 1},
            {6, 6, 0}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 2, 2), a);
    }
    
    @Test
    public void RotateTest_01() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {4, 0, 1},
            {0, 6, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 2, 3), a);
    }
    
    @Test
    public void RotateTest_02() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 4},
            {0, 6, 0},
            {0, 0, 1}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 2, 3), a);
    }   
    
    
    @Test
    public void RotateTest_03() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 0},
            {0, 6, 0},
            {1, 0, 4}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 0, 3), a);
    }
    
    
    @Test
    public void RotateTest_04() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {1, 0, 0},
            {0, 6, 0},
            {4, 0, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 0, 3), a);
    }
    
    @Test
    public void TieTest_01() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 6},
            {0, 4, 6},
            {0, 6, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 1, 3), a);
    }
    
    
    @Test
    public void TieTest_02() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 0, 6},
            {0, 4, 6},
            {0, 6, 6}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 0, 3), a);
    }
    
    @Test
    public void TieTest_03() {  
        T3Player t3p = new T3Player();
        int[][] state = {
            {4, 4, 0},
            {6, 6, 0},
            {0, 0, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 2, 3), a);
    }
    
    @Test
    public void sumMoreTests_t1() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {2, 3, 4},
            {0, 0, 6}, 
            {0, 0, 0}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(2, 2, 3), a);
    }
    
    @Test
    public void sumMoreTests_t2() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {3, 1, 6},
            {0, 5, 0}, 
            {0, 0, 4}
        };
        T3State s = new T3State(false, state);
        T3Action a = t3p.choose(s);
        assertEquals(new T3Action(0, 2, 2), a);
    }
    
    @Test
    public void sumMoreTests_t3() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {6, 6, 0},
            {6, 6, 0}, 
            {0, 6, 6}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        //your code should break ties by col then row in chronological order
        assertEquals(new T3Action(0, 2, 1), a);
        assertNotEquals(new T3Action(1, 2, 1), a);
        assertNotEquals(new T3Action(2, 0, 1), a);
    }
    
    @Test
    public void sumMoreTests_t4() {
        T3Player t3p = new T3Player();
        int[][] state = {
            {0, 6, 0},
            {6, 6, 0}, 
            {0, 6, 6}
        };
        T3State s = new T3State(true, state);
        T3Action a = t3p.choose(s);
        //your code should break ties by col then row in chronological order
        assertEquals(new T3Action(0, 0, 1), a);
        assertNotEquals(new T3Action(1, 2, 1), a);
        assertNotEquals(new T3Action(2, 0, 1), a);
        assertNotEquals(new T3Action(0, 2, 1), a);
    }
    
    
}
