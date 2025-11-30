/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.mycompany.montecarlo.App;
import java.util.HashMap;
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
    private static final double DELTA_Riem = 0.1;
    private static final double DELTA_MonteC = 1.4;

    //Tests for min and max methods
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

    //Tests for Riemann Sum
    @Test
    void testIntegrateRiem_Quadratic_RightSum() {
        ExpressionBuilder f = new ExpressionBuilder("x^2");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);

        double leftBound = 0.0;
        double rightBound = 4.0;

        // Exact integral of x^2 from 0 to 4 is 64/3 ≈ 21.33333...
        double expectedArea = 64.0 / 3.0;

        double actualArea = App.integrateRiem(function, leftBound, rightBound, 1000, "right");

        assertEquals(expectedArea, actualArea, DELTA_Riem);
    }

    @Test
    void testIntegrateRiem_Quadratic_LeftSum() {
        ExpressionBuilder f = new ExpressionBuilder("x^2");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        double leftBound = 0.0;
        double rightBound = 4.0;

        // Exact integral of x^2 from 0 to 4 is 64/3 ≈ 21.33333...
        double expectedArea = 64.0 / 3.0;

        double actualArea = App.integrateRiem(function, leftBound, rightBound, 1000, "left");

        assertEquals(expectedArea, actualArea, DELTA_Riem);
    }

    @Test
    void testIntegrateRiem_Trigonometric_RightSum() {
        ExpressionBuilder f = new ExpressionBuilder("sinx");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        double leftBound = 0.0;
        double rightBound = Math.PI; // Use Math.PI for the upper bound

        // Exact integral of sin(x) from 0 to pi is 2.0
        double expectedArea = 2.0;

        double actualArea = App.integrateRiem(function, leftBound, rightBound, 1000, "right");

        assertEquals(expectedArea, actualArea, DELTA_Riem);
    }

    @Test
    void testIntegrateRiem_Trigonometric_LeftSum() {
        ExpressionBuilder f = new ExpressionBuilder("sinx");
        f.variable("x");
        Expression function = f.build();
        function.setVariable("x", 0);
        double leftBound = 0.0;
        double rightBound = Math.PI;

        // Exact integral of sin(x) from 0 to pi is 2.0
        double expectedArea = 2.0;

        double actualArea = App.integrateRiem(function, leftBound, rightBound, 1000, "left");

        assertEquals(expectedArea, actualArea, DELTA_Riem);
    }
    
    
    //Test for monte carlo and plotPoints
    @Test
    void testIntegrateMonteCarlo_PositiveArea() {
        // Function f(x) = x^2
        ExpressionBuilder f = new ExpressionBuilder("x^2");
        f.variable("x");
        Expression function = f.build();
        double leftBound = 0.0;
        double rightBound = 4.0;
        double minValue = App.getMin(function, leftBound, rightBound); // Mock returns 0.0
        double maxValue = App.getMax(function, leftBound, rightBound); // Mock returns 16.0
        
        // Generate random points using the plotPoints logic
        HashMap<Double, Double> randPoints = App.plotPoints(leftBound, rightBound, minValue, maxValue, 100000);
        
        // Exact integral is 64/3
        double expectedArea = 64.0 / 3.0; 
        
        double actualArea = App.integrateMonteCarlo(function, leftBound, rightBound, randPoints);
        
        // The Monte Carlo estimate should be close to the exact integral
        assertEquals(expectedArea, actualArea, DELTA_MonteC);
    }


    @Test
    void testIntegrateMonteCarlo_CrossingXAxis_ZeroNetArea() {
        ExpressionBuilder f = new ExpressionBuilder("x");
        f.variable("x");
        Expression function = f.build();
        double leftBound = -2.0;
        double rightBound = 2.0;

        double minValue = -2.0; 
        double maxValue = 2.0; 

        HashMap<Double, Double> randPoints = App.plotPoints(leftBound, rightBound, minValue, maxValue, 100000);

        double expectedArea = 0.0; 
        
        double actualArea = App.integrateMonteCarlo(function, leftBound, rightBound, randPoints);

        assertEquals(expectedArea, actualArea, DELTA_MonteC);
    }

    @Test
    void testIntegrateMonteCarlo_NegativeArea() {
        ExpressionBuilder f = new ExpressionBuilder("-x");
        f.variable("x");
        Expression function = f.build();
        
        double leftBound = 1.0;
        double rightBound = 5.0;
        
        double minValue = -5.0; 
        double maxValue = -1.0; 

        HashMap<Double, Double> randPoints = App.plotPoints(leftBound, rightBound, minValue, maxValue, 100000);
        
        double expectedArea = -12.0; 
        
        double actualArea = App.integrateMonteCarlo(function, leftBound, rightBound, randPoints);
        
        assertEquals(expectedArea, actualArea, DELTA_MonteC);
    }
    
    
}


