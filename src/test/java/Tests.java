/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.mycompany.montecarlo.App;
import com.mycompany.montecarlo.MainViewController;
import java.util.HashMap;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author 6302743
 */
public class Tests {

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

        double expectedArea = 2.0;

        double actualArea = App.integrateRiem(function, leftBound, rightBound, 1000, "left");

        assertEquals(expectedArea, actualArea, DELTA_Riem);
    }

    //Test for monte carlo and plotPoints
    @Test
    void testIntegrateMonteCarlo_PositiveArea() {
        ExpressionBuilder f = new ExpressionBuilder("x^2");
        f.variable("x");
        Expression function = f.build();
        double leftBound = 0.0;
        double rightBound = 4.0;
        double minValue = App.getMin(function, leftBound, rightBound); // Mock returns 0.0
        double maxValue = App.getMax(function, leftBound, rightBound); // Mock returns 16.0

        HashMap<Double, Double> randPoints = App.plotPoints(leftBound, rightBound, minValue, maxValue, 100000);

        double expectedArea = 64.0 / 3.0;

        double actualArea = App.integrateMonteCarlo(function, leftBound, rightBound, randPoints);

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
    
    
    // Tests for the input valitation method: buildAndVerify
    // Tests are executed on the buildAndVerifyCore method
    // It mimicks the logic of the buildAndVerify method without 
    // using any javafx classes since they are hard to pass into a test file
    
    
    // Reusable array
    private String[] netAreaArray = new String[]{"0.0"};

    @BeforeEach
    void setUp() {
        // Reset the area array before each test to check if it's cleared on error
        netAreaArray[0] = "0.0";
    }

    @Test
    void testSuccessfulExecution() {
        // Test a standard, valid case (uses the simulated area result of "0.5")
        String result = MainViewController.buildAndVerifyCore(
            "x*x",          // equation
            "Trapezoidal",  // integrationMethod
            null,           // riemannEndpoint (not needed for Trapezoidal)
            "0",            // lowerBoundText
            "1",            // upperBoundText
            "100",          // numPointsText
            netAreaArray
        );
        assertEquals("", result, "Should return an empty string on success.");
        assertEquals("0.5", netAreaArray[0], "The netAreaText array should be updated with the simulated result.");
    }

    // 1. Integration Method Checks ---
    
    @Test
    void testMissingIntegrationMethod() {
        String result = MainViewController.buildAndVerifyCore("x", null, "Left", "0", "1", "10", netAreaArray);
        assertEquals("Integration type not specified", result, "Should catch null integration method.");
        assertEquals("", netAreaArray[0], "Area should be cleared on error.");
    }

    @Test
    void testMissingRiemannEndpoint() {
        String result = MainViewController.buildAndVerifyCore("x", "Riemann Sum", null, "0", "1", "10", netAreaArray);
        assertEquals("Riemann Sum direction not specified", result, "Should catch missing endpoint for Riemann Sum.");
        assertEquals("", netAreaArray[0], "Area should be cleared on error.");
    }
    
    // 2. Input Parsing Checks (Bounds/Points) ---

    @Test
    void testInvalidLowerBoundFormat() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "not_a_number", "1", "10", netAreaArray);
        assertEquals("Lower bound must be a valid double", result);
    }
    
    @Test
    void testInvalidUpperBoundFormat() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "0", "not a double", "10", netAreaArray);
        assertEquals("Upper bound must be a valid double", result);
    }

    @Test
    void testInvalidNumPointsFormat() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "0", "1", "ten", netAreaArray);
        assertEquals("Number of points must be a valid integer", result);
    }

    // 3 and 4. Bound Order and Limit Checks ---
    
    @Test
    void testLowerBoundGreaterThanUpper() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "5", "4", "10", netAreaArray);
        assertEquals("Lower bound must be strictly less than upper bound", result);
    }

    @Test
    void testBoundsEqual() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "1.0", "1.0", "10", netAreaArray);
        assertEquals("Lower bound must be strictly less than upper bound", result);
    }
    
    @Test
    void testLowerBoundTooLow() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "-1000.1", "10", "10", netAreaArray);
        assertEquals("Upper bound has to be less than 1000 and lower bound has to be greater than -1000", result);
    }

    @Test
    void testUpperBoundTooHigh() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "0", "1000.1", "10", netAreaArray);
        assertEquals("Upper bound has to be less than 1000 and lower bound has to be greater than -1000", result);
    }

    // 5. Number of Points Check ---
    
    @Test
    void testNumPointsZero() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "0", "1", "0", netAreaArray);
        assertEquals("Number of points must be between 1 and 100,000", result);
    }
    
    @Test
    void testNumPointsTooHigh() {
        String result = MainViewController.buildAndVerifyCore("x", "Trapezoidal", null, "0", "1", "100001", netAreaArray);
        assertEquals("Number of points must be between 1 and 100,000", result);
    }
    
    // 6. Equations tests
    
    @Test
    void testEquationIsNull() {
        String result = MainViewController.buildAndVerifyCore(null, "Trapezoidal", null, "0", "1", "10", netAreaArray);
        assertEquals("No equation selected", result);
    }
    
    @Test
    void testEquationIsBlank() {
        String result = MainViewController.buildAndVerifyCore("  ", "Trapezoidal", null, "0", "1", "10", netAreaArray);
        assertEquals("No equation selected", result);
    }

    @Test
    void testRestrictedTanFunction() {
        String result = MainViewController.buildAndVerifyCore("tan(x)", "Trapezoidal", null, "0", "1", "10", netAreaArray);
        assertEquals("Tangent and cotangent functions are not supported", result);
    }

    @Test
    void testRestrictedRationalFunction() {
        String result = MainViewController.buildAndVerifyCore("1/x", "Trapezoidal", null, "0", "1", "10", netAreaArray);
        assertEquals("Rational functions aren't supported", result);
    }
    
    @Test
    void testInvalidEquationSyntax() {
        String result = MainViewController.buildAndVerifyCore("x ^^^ 5", "Trapezoidal", null, "0", "1", "10", netAreaArray);
        assertEquals("Invalid function", result, "Should catch a non-parsable syntax error.");
    }

    // --- 10. Continuity Check (Testing a discontinuity not covered by step 8) ---
    
    @Test
    void testDiscontinuousFunction() {
        String result = MainViewController.buildAndVerifyCore("log(x)", "Trapezoidal", null, "-1", "1", "10", netAreaArray);

        assertEquals("Function is not continuous on the interval", result);
    }
}
