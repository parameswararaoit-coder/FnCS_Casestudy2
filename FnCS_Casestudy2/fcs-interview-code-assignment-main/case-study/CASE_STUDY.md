# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
- How granular does cost tracking need to be? Should we track at the product SKU level, category level, or warehouse/store level?
- What is the frequency of cost allocation updates? Real-time, daily, weekly, or monthly?
- Are there shared costs between warehouses and stores (e.g., regional transportation, shared labor pools) that need proportional allocation?
- How do we handle costs during the warehouse replacement process? Should costs be attributed to the old warehouse until cutover or gradually shifted?
- What cost allocation methods are currently used (direct, activity-based, weighted average)? Are they working effectively?

**Important Considerations:**

- Multi-dimensional Cost Attribution: In a fulfillment environment, a single cost (e.g., transportation) may serve multiple warehouses and stores simultaneously. Defining clear allocation rules based on volume, distance, or usage metrics is critical to avoid distorted unit economics.
- Temporal Alignment: Costs often don't align perfectly with when products are received, stored, or sold. For example, labor costs are incurred daily, but inventory may sit for weeks. Establishing a consistent time-based allocation methodology prevents mismatched reporting.
- Business Unit Code Continuity: Given the warehouse replacement functionality where Business Unit Codes are reused, the system must distinguish between cost periods for the old vs. new warehouse while maintaining reporting continuity for the business unit as a whole.
- Variable vs. Fixed Cost Separation: Warehouses have significant fixed costs (rent, utilities, permanent staff) and variable costs (seasonal labor, transportation based on volume). Proper categorization enables better decision-making about capacity utilization and marginal costs.

**Related Experience Considerations:**

- In previous fulfillment systems, lack of automated cost allocation led to month-end manual adjustments consuming 40+ hours of finance team time
- Overhead allocation disputes between departments often stemmed from unclear allocation formulas rather than actual cost issues
- Cost tracking at too granular a level (e.g., per-transaction) created system performance issues without proportional business value
## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

**Key Questions:**

- What percentage of total costs are the top 3 cost drivers (typically labor, transportation, and real estate)?
- Are there seasonal patterns that create capacity mismatches (overcapacity in slow periods, undercapacity in peaks)?
- How does our cost per unit/order compare to industry benchmarks or similar operations?
- What is the current warehouse and store utilization rate? Are facilities operating at optimal capacity?
- Are there opportunities for store-to-store or warehouse-to-warehouse transfers to reduce stockouts without increasing overall inventory?

**Identification Approach:**

- Data-Driven Baseline Analysis: Establish current cost per unit metrics across all warehouses and stores to identify outliers. High-cost locations may indicate operational inefficiencies or structural issues requiring intervention.
- Pareto Analysis: Apply 80/20 rule to identify which cost categories and locations drive the majority of expenses. Focus optimization efforts where they'll have maximum impact.
- Benchmarking: Compare similar warehouses/stores (similar volume, geography, product mix) to identify best performers and understand what drives their efficiency.

**Prioritization Framework:**

- Impact vs. Effort Matrix: Plot optimization opportunities based on potential cost savings vs. implementation complexity
- Quick Wins First: Implement low-hanging fruit (e.g., route optimization, shift scheduling improvements) to build momentum and fund larger initiatives
- Strategic Alignment: Prioritize optimizations that also improve service quality or enable growth, not just cost reduction

**Implementation Strategies:**

**Warehouse Network Optimization:**

- Analyze whether the current warehouse locations minimize transportation costs and delivery times
- Consider warehouse consolidation or repositioning based on demand patterns
- Expected outcome: 10-15% reduction in transportation costs, improved delivery times

**Labor Productivity Enhancement:**

- Implement workforce management systems to optimize staffing levels based on demand forecasts
- Cross-train staff to handle multiple functions during peak/off-peak periods
- Expected outcome: 5-10% reduction in labor cost per unit processed

**Inventory Optimization:**

- Use the Location → Warehouse → Store relationship to position inventory closer to demand
- Reduce slow-moving inventory through better demand forecasting and redistribution
- Expected outcome: 15-20% reduction in carrying costs, improved cash flow

**Process Automation:**

- Identify repetitive manual processes in warehouse operations for automation
- Expected outcome: Improved accuracy, 20-30% labor time savings on automated tasks


**Monitoring and Continuous Improvement:**

- Establish KPIs for each optimization initiative with monthly tracking
- Create feedback loops where operational teams can suggest improvements
- Conduct quarterly reviews to assess ROI and adjust strategies

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

**Key Questions:**

- What financial systems are currently in use (ERP, accounting software, budgeting tools)?
- What is the chart of accounts structure? How do cost centers align with our Warehouse/Store entities?
- What are the critical reporting deadlines (month-end close, quarterly reporting) that require timely data?
- Are there regulatory or audit requirements for cost data traceability and accuracy?
- What is the tolerance for data latency? Must certain costs be reflected in real-time, or is end-of-day batch processing acceptable?
- How do we handle cost corrections or reversals? What audit trail is required?

**Benefits of Integration:**

- Single Source of Truth: Eliminates duplicate data entry and reconciliation efforts between operational and financial systems, reducing errors and saving significant finance team time (typically 30-50 hours per month-end).
- Timely Decision Making: Real-time or near-real-time cost visibility enables proactive management. For example, if labor costs are trending over budget mid-month, managers can adjust staffing immediately rather than discovering the overrun after month-end.
- Automated Compliance and Audit Trail: Integration ensures every cost transaction has complete traceability from source (warehouse/store operation) to financial statement, meeting audit and compliance requirements without manual documentation.
- Improved Forecasting Accuracy: When financial systems receive actual operational data continuously, variance analysis between actual vs. planned costs is more accurate and timely, improving future forecasts.
- Cost Allocation Automation: Programmatic cost allocation rules can execute automatically during integration, ensuring consistent methodology and eliminating manual allocation spreadsheets.

**Ensuring Seamless Integration:**

**API-First Design:**

- Design RESTful APIs with proper versioning to allow financial systems to consume cost data
- Implement webhooks or event-driven architecture for real-time updates on critical cost transactions
- Ensure APIs handle authentication, rate limiting, and error responses gracefully

**Data Mapping and Transformation Layer:**

- Create clear mapping between operational entities (Warehouse, Store, Product) and financial dimensions (cost centers, GL accounts, profit centers)
- Handle the Business Unit Code reuse scenario explicitly: ensure archived warehouse costs map to the same business unit but different time periods in the financial system

**Reconciliation Framework:**

- Implement automated reconciliation processes that compare totals between systems daily
- Create exception reports for discrepancies requiring investigation
- Maintain reconciliation history for audit purposes

**Data Quality and Validation:**

- Validate data at source before sending to financial systems (completeness, formatting, business rule compliance)
- Implement retry mechanisms for failed transactions with appropriate alerting
- Ensure idempotency to prevent duplicate transactions if retries occur

**Phased Rollout Approach:**

- Start with read-only integration to validate data accuracy without risking financial data integrity
- Pilot with a single warehouse or store before full rollout
- Run parallel systems during transition period to verify accuracy

**Monitoring and Alerting:**

- Implement integration health dashboards showing data flow status, latency, error rates
- Set up alerts for integration failures, unusual data patterns, or missed SLAs
- Establish clear escalation procedures when integration issues occur

**Technical Considerations:**

- Ensure database transactions maintain ACID properties when updating both systems
- Consider eventual consistency models for non-critical data to improve performance
- Plan for disaster recovery: what happens if one system is unavailable for extended periods?

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

**Key Questions:**

- What is the budgeting cycle frequency? Annual, quarterly, or rolling forecasts?
- How many years of historical data are available for trend analysis?
- What external factors significantly impact costs (fuel prices, minimum wage changes, seasonal demand)?
- How stable is the warehouse/store network? How many openings/closures/replacements happen annually?
- What level of budget granularity is needed? By location, cost category, time period?
- Who are the stakeholders in the budgeting process, and what approval workflows are required?
- How much variance from budget is acceptable before corrective action is triggered?

**Importance in Fulfillment Operations:**

- Resource Planning: Budgets drive decisions about warehouse capacity, labor hiring, equipment investments, and inventory levels. Inaccurate forecasts lead to either overcapacity (wasted costs) or undercapacity (lost sales, expedited shipping costs).
- Performance Management: Budgets serve as performance benchmarks. Without accurate forecasts, it's difficult to distinguish between operational inefficiency and external factors, making accountability unclear.
- Cash Flow Management: Fulfillment operations are capital-intensive. Accurate forecasts ensure adequate working capital for inventory purchases, facility leases, and operational expenses without excess cash sitting idle.
- Strategic Investment Decisions: Understanding future cost trajectories informs decisions about warehouse automation, network redesign, or expansion. Poor forecasting can lead to premature investments or missed opportunities.

**System Design Considerations:**

**Multi-Factor Forecasting Models:**

- Driver-Based Forecasting: Link costs to operational drivers (units processed, orders fulfilled, storage volume) rather than simple historical trending
- Segmentation: Forecast separately for each warehouse/store considering their unique characteristics (size, age, automation level, market)
- Variable vs. Fixed Decomposition: Model fixed costs (rent, permanent staff) and variable costs (seasonal labor, transportation) separately as they behave differently

**Historical Data Foundation:**

- Maintain at least 3-5 years of historical cost data with sufficient granularity
- Normalize historical data for one-time events (e.g., warehouse replacement costs, natural disasters)
- Handle the warehouse replacement scenario: ensure the system can show cost trends for a Business Unit Code across multiple physical warehouse generations

**Scenario Planning Capabilities:**

- Enable "what-if" analysis: what happens to costs if volume increases 20%? If we add automation?
- Model sensitivity to external factors: fuel price changes, wage inflation, currency fluctuations
- Support best-case, worst-case, and most-likely scenarios

**Collaborative Workflow:**

- Allow warehouse/store managers to input local knowledge and assumptions
- Enable finance to review, challenge, and consolidate forecasts
- Maintain version control and audit trail of forecast iterations
- Support approval workflows with appropriate authorization levels

**Integration with Operational Plans:**

- Link budgets to operational forecasts (expected volume, growth plans, new store openings)
- Ensure consistency between sales forecasts and fulfillment capacity/cost budgets
- Connect to strategic initiatives (e.g., if opening 10 new stores, automatically flow setup costs into budget)

**Variance Analysis and Learning:**

- Track actual vs. budget variance in real-time
- Categorize variances (volume-driven, price-driven, efficiency-driven)
- Use machine learning to improve forecast accuracy over time by learning from past errors
- Enable rolling forecasts that update as actual data becomes available

**Temporal Considerations:**

- Handle different time horizons: detailed monthly forecasts for next quarter, quarterly for rest of year, annual for out-years
- Address seasonality explicitly in models
- Plan for known events (peak seasons, warehouse replacements, contract renewals)

**Technical Implementation:**

- Consider using time-series forecasting algorithms (ARIMA, Prophet) for baseline predictions
- Implement adjustment layers where business rules and human judgment can override statistical forecasts
- Design for scalability: as the warehouse/store network grows, forecasting should scale without proportional manual effort
- Provide both granular (location-level) and aggregated (regional, company-wide) views

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

**Key Questions:**

- What is the typical timeline for warehouse replacement (planning to cutover to decommissioning)?
- How should costs be allocated during the transition period when both warehouses may operate simultaneously?
- What categories of costs need to be preserved in history? All operational costs, or only specific categories?
- Are there one-time replacement costs (moving expenses, dual operations, training) that should be tracked separately?
- How long must cost history be preserved? Are there regulatory or business requirements?
- Should the new warehouse inherit any cost baselines or budgets from the old warehouse, or start fresh?
- How do we handle multi-year contracts (leases, service agreements) that span the replacement?

**Importance of Preserving Cost History:**

**Business Unit Trend Analysis:** By reusing the Business Unit Code, the company maintains continuity in cost reporting for a geographic area or market. Preserving cost history allows analysis of cost trends for the business unit over many years, even as physical warehouses change. This is critical for:

- Understanding long-term cost trajectory of serving a market
- Evaluating whether warehouse replacements achieve intended cost improvements
- Benchmarking new warehouse performance against historical baseline

**Investment Justification and ROI Tracking:** Warehouse replacements typically require significant capital investment. Preserved cost history enables:

**Retrospective analysis:** did the new warehouse achieve projected cost savings?
**Learning for future replacement decisions:** which assumptions proved accurate?
**Stakeholder accountability:** were business case projections realistic?

**Regulatory and Audit Compliance:** Many industries require multi-year cost records for tax purposes, financial audits, or regulatory compliance. Archiving without preserving cost data creates compliance gaps.
**Contractual Obligations: Historical cost data may be needed to:**

- Support pricing negotiations with customers who have cost-plus contracts
- Resolve disputes about service level agreements or cost allocations
- Demonstrate cost basis for insurance claims

**Institutional Knowledge:** Understanding why certain operational choices were made in the past requires context about their cost implications. Without cost history, this organizational learning is lost.

**Relationship to New Warehouse Budget Management:**

**Realistic Budget Setting:**

- Historical cost data from the archived warehouse provides the most relevant baseline for budgeting the new warehouse
- Adjustments can be made for known differences (newer facility = lower maintenance, automation = different labor profile)
- Without cost history, budgets would rely on less relevant benchmarks from different markets or facility types

**Cost Migration Tracking:**

- By maintaining Business Unit Code continuity, the system can track how costs migrate from old to new warehouse during transition
- Identify if costs are actually being eliminated or just temporarily shifted
**Example:** if labor costs in the old warehouse decline as it winds down, but the new warehouse's labor costs exceed the budget by the same amount, this indicates insufficient efficiency gains

**Variance Analysis Context:**

**When the new warehouse's actual costs vary from budget, historical data helps diagnose whether issues are:**

- Temporary startup inefficiencies (common in new facilities)
- Structural problems requiring intervention
- Market changes affecting all operations

- This context prevents overreaction to temporary issues or complacency about persistent problems

**Learning and Continuous Improvement:**

- Comparing multiple warehouse replacement projects over time identifies best practices
- Understanding cost patterns during ramp-up periods helps set realistic expectations
- Recognizing which cost categories typically improve post-replacement vs. which don't

**Implementation Considerations:**

**Data Architecture:**

- Maintain separate cost records for each physical warehouse but allow aggregation by Business Unit Code
- Include warehouse instance identifier and active date ranges to disambiguate
- Example schema: BusinessUnitCode | WarehouseInstanceID | ActiveFrom | ActiveTo | CostData

**Transition Period Handling:**

- Define clear rules for cost allocation during overlap periods (old warehouse winding down, new ramping up)
- Track one-time transition costs separately to avoid distorting operational cost trends
- Consider creating a virtual "transition cost center" that rolls up to the Business Unit but doesn't distort individual warehouse metrics

**Reporting Flexibility:**

- Enable reports that show Business Unit Code costs across all warehouse instances (full history)
- Enable reports that show individual warehouse instance costs (specific facility performance)
- Support comparison views: new warehouse vs. old warehouse at similar points in their lifecycles

**Budget Inheritance and Adjustment:**

- Provide mechanism to copy old warehouse budgets to new warehouse as a starting point
- Document assumptions and adjustments made for the new warehouse
- Track budget evolution: original projection → revised based on learnings → actual

**Sunset and Archival Process:**

- Define clear criteria for when old warehouse data moves from active to archived status
- Ensure archived data remains queryable for reporting even if not in active operational systems
- Maintain metadata explaining the replacement context for future users of historical data

**Strategic Insight:**
The warehouse replacement scenario is fundamentally about managing change while maintaining continuity. Cost control serves both backward-looking (preserving learning) and forward-looking (managing new operation) purposes. A well-designed system treats replacement not as an isolated event but as part of the ongoing lifecycle of serving a market, with each warehouse generation building on lessons from predecessors.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
