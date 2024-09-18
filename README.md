Note: This project was initially a group project for the course CIT 594: Data Structures & Software Design. Since then, I have independently redesigned it to enhance functionality and improve overall user experience.

# **ETF Builder: An ETF Simulator**

### **Overview:**
ETF Builder is a Java-based computer application designed to construct, simulate and analyze the performance of Exchange Traded Funds (ETFs) over time. The simulator integrates market data processing, portfolio management, and various financial metrics calculations to provide a comprehensive tool for ETF strategy testing and analysis. 

### **Data:**
Supporting data consists of various financial metrics for the 393 members of the S&P500 that have been a member of this group since 2010. Data is calculated monthly between January 2010 - March 2024. All data per Bloomberg. 

### **Features:**
- #### ETF Construction:
  - User can input criteria which are used in conjunction with an alogrithm to produce an ETF
     - Key Parameters
       1. Stock evaluation preferences (Score ETF constuction priorities must add to 100)
          - Net Income
          * Market Cap
          * PE Ratio
          * YoY Sales Growth %
          * Net Debt - Common Ratio
        2. Industry selection (use "All" if do not want to limit by industry)
        3. ETF reinvestment rate (month period between re-balancing ETF)
        4. Dollars available to invest
        
  - User can search for stocks to include in an ETF according to various metrics and criteria to identify stocks that fit needs, or input a fully constructed exisiting ETF to test against market data.

- #### ETF Rebalancing for Algorithm-Generated ETFs:
  -  Automatically tests and adjusts algorithm-created ETF compositions based on custom-defined strategies.

- #### Portfolio Simulation:
  - Calculates monthly total returns for each ETF in the portfolio, with the option to compare the ETF's returns to returns that would have been achieved if the user invested in the S&P500 index instead.

### **Usage:**
Run ETFBuilderApplication with the following VM option: --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls



![page to select select simulation start date](https://github.com/user-attachments/assets/c986f3a3-a100-4a94-bd17-dedbbb26fd0c)

![page to select your own stocks to add to ETF](https://github.com/user-attachments/assets/33d4dbd2-c292-4cbd-95d9-4b26c2944f32)

![page to create an ETF using the system algorithm](https://github.com/user-attachments/assets/d9d19402-4573-4e93-9556-5fec2cf771bb)

![line chart showing total ETF returns](https://github.com/user-attachments/assets/f652a860-b943-4a83-a55b-8ed847f3397b)

![line chart showing ETF returns compared to S&P500 returns](https://github.com/user-attachments/assets/f1a9640e-9662-4b5a-9f72-0c9d04729743)
