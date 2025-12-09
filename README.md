# Monte-Carlo-Integration-Project

## Project Description

### What is it?
The **Monte Carlo Integration Visualization** is a project designed to display how the Monte Carlo integration method works, and understand how well the Monte Carlo integration method works in comparison to the the Riemann Sum method for a large number of points.

### How do you run it?
To run the **Monte Carlo Integration Visualization**, you must first enter every integration parameter into their respective TextField boxes. For example, the min and max bounds of Integration should be entered into their respective TextFields to to be interpreted into the program. When every field has been filled out with values that make sense for the program to interpret (e.g. an Expression in the "Equation" TextField), the program will automatically render and display the graphics depending on which integration method is selected, and compute the Net Area using said method.

## Who did what?

### Quentin

1. Created the BuildAndVerify() method
2. Created overall project structure and linked all parts together (between Georges' methods in the App class and the main controller class)
3. Created the riemannDisplay() method to display Riemann Sum rectangles on the LineChart.
4. Created monteCarloDisplay() method to display Monte Carlo points and how they're rendered and considered on the function
5. Created clearGraph() method to clear the graph of all the graphical points displayed on it

### George

1. Created integrateMonteCarlo() method to handle the mathematical integration of a function using the Monte Carlo integration technique
2. Created the integrateRiem() method to handle the matematical integration of a function using the Riemann Sum integration technique
3. Created plotPoints() method to ensure the randomly plotted points are accessible by both the graphical methods and the mathematical methods so they sync up and accurately display the same data

### Edric

1. Created the plotFunction() method
2. Handled all FXML structure
3. Created all UI and aesthetic
4. Helped implement riemannDisplay() method
5. Made the different colors for monteCarloDispolay()
