# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```
Yes, I would refactor the data access layer for better consistency and maintainability.

Currently, the codebase uses multiple persistence approaches:

Store uses Panache Active Record (Store.findById(), persist()),

Product uses a Panache Repository,

Warehouse follows a repository with domain model and use-case separation,

Fulfilment accesses the database directly through EntityManager.

This inconsistency increases cognitive load, makes the code harder to understand, and complicates testing and long-term maintenance.

I would standardize on the repository + domain/use-case pattern, because it clearly separates business logic from persistence concerns. This approach improves testability (by allowing repositories to be mocked), enforces cleaner boundaries, and makes the system easier to evolve as business rules grow.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```
Advantages of Open API approach

1. Single source of truth (clear API contract) – everyone uses the same definition of endpoints, inputs, outputs, and auth.
2. Auto docs + easier onboarding – interactive documentation makes it faster to understand and use the API.
3. Validation/testing support – schema-based validation and contract tests catch breaking changes early.

Disadvantages of Open API approach

1. Maintenance burden – if you don’t keep it updated, it becomes misleading.
2. Verbose/complex specs – large APIs create big files that are hard to manage manually.
3. Doesn’t capture everything / awkward cases – some behaviors (streaming/events, deep unions, business rules) don’t fit neatly, and generated SDKs may still need manual improvement.

Advantages of traditional approach
1. Fast to start (minimal setup)
2. Flexible (easy to describe special behaviors in plain text)
3. Less tooling overhead (no spec file to maintain)

Disadvantages of Open API approach
1. Docs drift easily (implementation changes, docs don’t)
2. Harder integrations (less precise contract, more back-and-forth)
3. Less automation (no guaranteed validation/contract tests, limited code generation)

I  choose the traditional approach when the API is small, changes often, or is only used by one team. Because it’s faster and more flexible.

I  choose OpenAPI when multiple teams or partners depend on the API because a shared contract, auto-docs, and validation reduce confusion and breaking changes. 

```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```
To balance thorough testing with time and resource constraints, I would prioritize tests based on business risk and value, rather than aiming for 100% coverage from day one.

I’d primarily use TDD for critical business logic, where backend developers write a small failing test first for each key rule such as validations, permissions, state transitions, and error scenarios. This ensures correctness early and allows safe refactoring.

Testing priorities would be:

Unit tests (highest priority)

1. Focus on business rules, domain logic, and use cases
2. Fast to run and cheap to maintain
3. Written using TDD wherever possible

Integration tests (selective but essential)

1.Cover database interactions, repositories, and external integrations
2. Validate mappings, transactions, and persistence behavior

API / contract tests

1. Ensure REST APIs behave correctly and do not break consumers
2. Especially important in microservices environments

End-to-end tests (minimal but critical paths only)

1. Used only for core user journeys due to higher cost and slower execution

Pros of this approach:

1. Fewer bugs and regressions
2. Clearer requirements (tests act as living documentation)
3. Safer refactoring and more stable APIs over time

Trade-offs:

Slightly slower development initially due to writing tests first

1. Learning curve for teams new to TDD
2. Ongoing test maintenance is required

To ensure test coverage remains effective over time, I would:

1. Enforce tests in CI/CD pipelines
2. Track coverage trends rather than chasing 100% coverage
3. Review tests during code reviews
4. Regularly refactor tests along with production code

Overall, this approach balances speed and quality by investing more upfront while significantly reducing long-term defects and maintenance costs.
```