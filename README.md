# Forest Fire Simulation

This project simulates the spread of fire in a forest using a **Java Swing** graphical user interface (GUI). The core idea of the simulation is to model how different terrain configurations and environmental conditions affect the dynamics and size of a forest fire.

## Problem Description

The **Forest Fire Simulation** is based on a cellular automaton model, where the forest is represented as a grid of cells, with each cell symbolizing a specific area of land. Cells can have various states, including burning, burned, or unburned, and the fire spreads between neighboring cells based on predefined rules and parameters such as:

- **Wind conditions**
- **Terrain type**
- **Vegetation density**

The simulation starts with at least one cell in the burning state, initiating the fire's spread across the grid. It tracks how quickly the fire spreads, which areas are most at risk, and how terrain configurations influence the extent of the fire.

## Model Overview

The model uses a **Moore Neighborhood**, meaning fire can spread both horizontally and vertically between adjacent cells. The spread is influenced by conditions like:

- **Wind**: Can either accelerate or slow the spread of fire.
- **Elevation**: Fire can spread upwards or downwards depending on the terrain.
- **Vegetation**: Dense areas burn faster and more intensely.

## Features

- **Simulation Visualization**: The application uses **Java Swing** for real-time visualization of the simulation, showing how fire spreads across the forest grid.
- **Configurable Parameters**: Users can modify various parameters such as wind speed, terrain configuration, and vegetation density to observe how these factors affect the fire.
- **Detailed Analytics**: The simulation tracks key metrics such as fire spread rate and the percentage of the forest burned.

## Technologies Used

- **Java**: For the core logic of the simulation.
- **Java Swing**: Provides the graphical user interface (GUI) to visualize the simulation in real time.
- **Gradle**: For project building and dependency management.
- **PDF Documentation**: Contains detailed descriptions of the model, including formulas and parameters.

## How to Set Up the Project

### Prerequisites

- **Java Development Kit (JDK)**: Ensure that you have **JDK 11** or higher installed.
- **Gradle**: Required for building and running the project.

### Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd forest-fire-simulation
   ```

2. **Build the project** using Gradle:
   ```bash
   ./gradlew build
   ```

3. **Run the simulation**:
   ```bash
   ./gradlew run
   ```

4. The application will launch the Java Swing GUI where you can adjust parameters and observe the fire simulation in action.

## Running the Simulation

Once the application is running, you can:

1. **Set Parameters**: Adjust settings such as wind speed, terrain configuration, and vegetation density.
2. **Start Simulation**: Begin the fire simulation and observe how it spreads across the forest grid.
3. **View Metrics**: Analyze data such as the speed of the fire spread and the total percentage of the forest affected by the fire.
