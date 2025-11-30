/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.mycompany.montecarlo.App;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 * @author 6302743
 */
public class ComputationTest {
    private static final double DELTA = 0.001; 
    
    @Test
    void testGetMin() {
        ExpressionBuilder f = new ExpressionBuilder("x^2 - 4x + 1");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = -1.0;
        double rightBound = 5.0;
        
        // Expected minimum for f(x) = x^2 - 4x + 1 on [-1, 5] is -3 (at x=2)
        double expectedMin = -3.0;
        double actualMin = App.getMin(function, leftBound, rightBound);

        assertEquals(expectedMin, actualMin, DELTA);
    }
    
    @Test
    void testGetMax() {
        ExpressionBuilder f = new ExpressionBuilder("x^2 - 4x + 1");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = -1.0;
        double rightBound = 5.0;
        
        // Expected maximum for f(x) = x^2 - 4x + 1 on [-1, 5] is 6 (at x=-1 and x=5)
        double expectedMax = 6.0;
        double actualMax = App.getMax(function, leftBound, rightBound);
        
        assertEquals(expectedMax, actualMax, DELTA, "The maximum value calculation should be accurate within the specified tolerance.");
    }
    
    @Test
    void testGetMinTrig() {
        ExpressionBuilder f = new ExpressionBuilder("sinx");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = -1.0;
        double rightBound = 5.0;
        
        // Expected minimum is -1
        double expectedMin = -1.0;
        double actualMin = App.getMin(function, leftBound, rightBound);

        assertEquals(expectedMin, actualMin, DELTA);
    }
    
    @Test
    void testGetMaxTrig() {
        ExpressionBuilder f = new ExpressionBuilder("sinx");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = -1.0;
        double rightBound = 5.0;
        
        // Expected maximum is 1
        double expectedMax = 1.0;
        double actualMax = App.getMax(function, leftBound, rightBound);
        
        assertEquals(expectedMax, actualMax, DELTA, "The maximum value calculation should be accurate within the specified tolerance.");
    }
    

    @Test
    void testGetMinWhenMinIsAtLeftBound() {
        ExpressionBuilder f = new ExpressionBuilder("x^2 - 4x + 1");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = 3.0;
        double rightBound = 5.0;
        
        // Min should be at x=3: f(3) = -2
        double expectedMin = -2.0;
        double actualMin = App.getMin(function, leftBound, rightBound);
        
        assertEquals(expectedMin, actualMin, DELTA);
    }

    @Test
    void testGetMaxWhenMaxIsAtRightBound() {
        ExpressionBuilder f = new ExpressionBuilder("x^2 - 4x + 1");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        
        double leftBound = 3.0;
        double rightBound = 5.0;
        
        // Function f(x) = x^2 - 4x + 1. On [3, 5], the function is increasing.
        // Max should be at x=5: f(5) = 6
        double expectedMax = 6.0;
        double actualMax = App.getMax(function, leftBound, rightBound);
        
        assertEquals(expectedMax, actualMax, DELTA);
    }
}

