# GitHub Repository Search API

A backend service for searching GitHub repositories with custom popularity scoring based on stars, forks, and update recency — a reference implementation showcasing modern backend architecture principles.

## Project Overview

This project demonstrates building a backend app that scores GitHub repositories. The app works as a wrapper around GitHub's public search API, getting repositories and adding a custom popularity score based on stars, forks, and how recently the repo was updated. There is a single service in the domain layer that acts as an orchestration unit. In line with DDD ideas, our domain object is "rich"—it knows by itself how to calculate its own score, so the core logic stays in the domain layer.

The project focuses on demonstrating good architecture principles with practical implementations of production-grade features. The architecture includes caching and retry mechanisms, while maintaining clean separation of concerns.

### Architecture

The main architectural approach is a lightweight hexagonal (ports and adapters) architecture, with the core logic in the `domain` layer and a simple `api` layer on top. This demonstrates how to build systems with clear separation of concerns. I also kept the package structure as flat as possible to maintain simplicity and readability.

<div align="center">
  <img src="docs/Hexagonal Architecture of GitHub Repo Search API.png" alt="Hexagonal Architecture Diagram" width="700">
</div>

The diagram above illustrates the hexagonal architecture used in this project:

- **Domain Core**: Contains the business logic, domain models, and scoring algorithm
- **Ports**: Define interfaces for the domain to interact with external systems
- **Adapters**: Implement the interfaces to connect with external systems (REST API, GitHub API)
- **Cross-cutting concerns**: Caching and retry mechanisms enhance performance and resilience

Regarding test coverage: I implemented strategic testing rather than exhaustive coverage. The codebase includes unit tests for the repository score calculation, unit tests to verify proper exception handling, and a full-cycle integration test that validates the main logic flow. For the integration test, I mocked the `RepositoryProvider` with predefined responses instead of using a web server to simulate GitHub (such as OkHttp's `MockWebServer`). This approach focuses on testing the most critical components while maintaining development efficiency.

## Design Decisions and Limitations

While working on this project, I encountered several design decisions where there was no perfect answer and each option had its own pros and cons. This section outlines the key trade-offs and decisions made during development, illustrating my thought process and problem-solving approach.

### Why I Don’t Support Sorting or Filtering by Popularity Score

It might seem nice to let clients sort or filter repositories by the custom popularity score, for example, to only get repos above a certain score or to sort results by this value. But in reality, this is not so simple. To do this, I would need to fetch lots of pages from GitHub, calculate the score for each repo, and then sort or filter everything on BE side. This would make pagination messy and much harder to keep correct. Because of these reasons, I decided not to support sorting or filtering by popularity score. Instead, I just calculate the score and include it in each repository object in the response.

### How I Calculate the Popularity Score

Figuring out how to measure repository popularity isn’t obvious without some research. After trying a few ideas, I settled on this formula:

`Popularity Score = (stars * 0.7) + (forks * 0.2) + (recencyFactor * 0.1)`

Here’s my thinking:
- **Stars** are the main signal of popularity, so they get the biggest weight.
- **Forks** show practical use and community interest, so they count too, but less.
- **Recency** (how recently the repo was updated) matters, because more active repos are usually more relevant.

A couple of details: stars and forks are just the raw numbers from GitHub—there’s no upper limit, so more is always better. For recency, I flip the value: the more days since the last update, the lower the score. So new or recently updated repos get a boost from this factor.

### Domain Model Design

The GitHub repository object is huge and full of nested properties that describe every detail of a repo. In this implementation, I picked only the fields that really matter for my use case. My thinking is simple: the domain model should only include the data I actually need. If I ever need more fields, I can always add them later. I also don't like the idea of just passing the whole massive GitHub object straight to the client—most of that data would never be used, but it would make responses bigger, slow things down, and waste resources. Smaller models are easier to read, maintain, and support. This way, there is a clear boundary between my own domain and the external GitHub API.

### Default Parameter Behavior

While testing, I ran into a problem with GitHub's API: the `q` parameter (the search query) is required, but in my API, both filter parameters are optional. If the client doesn't set either filter, I would end up making a call to GitHub without a `q` at all - which just fails. My first idea was to force at least one filter to always be set, but that felt wrong and would have meant rewriting a lot of logic. Instead, I decided to quietly set a default value for one of the filters so `q` is always present. The cleanest option was to default the `created` date filter to something neutral and far in the past. I picked April 1, 2008 (the day GitHub was founded), since every real repo will have been created after that date. This way, the API works out of the box, and users don't have to worry about this GitHub issue at all.

### Validation Strategy

The project implements a deliberate multi-layered validation approach:

1. **HTTP/Controller Layer** - Uses Spring validation annotations (`@Min`, `@Max`, `@Pattern`) to validate input format and ranges before request processing begins

2. **Domain Model Layer** - The `SearchRequest` record constructor performs business rule validation, ensuring core domain constraints (like page ranges and null checks) are enforced regardless of how the object is created

This dual-validation strategy ensures both the API contract is respected and that domain objects always maintain a valid state. I chose not to add a third validation layer in the service or query-building code as it would be redundant and violate DRY principles. By validating at the entry point (controller) and encapsulating business rules in the domain model itself, the service layer can remain focused on orchestration and business logic rather than repetitive validation.

### Retry and Caching Strategy

**Retry Mechanism:**
The application implements a resilient retry strategy for GitHub API calls using Project Reactor's retry capabilities. The WebClient is configured to:
- Perform up to 3 retry attempts with exponential backoff (starting at 1 second)
- Only retry on server errors (5xx) and connection issues, not on client errors (4xx)
- Provide informative error messages when retries are exhausted

This approach ensures the application can gracefully handle transient network issues and temporary GitHub API outages without failing the user request.

> **Note for Production Deployment:** The current retry configuration uses exponential backoff with delays that could potentially exceed client-side timeouts in worst-case scenarios. In a production environment, these parameters should be tuned based on observed API response times, client timeout settings, and business requirements. Options include adjusting backoff parameters, setting maximum retry times, implementing asynchronous processing for long-running requests, or configuring longer timeouts in both the client and server components.

**Caching Implementation:**
To minimize unnecessary API calls and improve response times, the application implements a caching strategy:
- Uses Spring Cache abstraction for clean separation between cache implementation and business logic
- Current implementation uses a simple in-memory ConcurrentMapCache for the example deployment
- In a production environment, this could be easily replaced with a distributed cache like Redis or Memcached
- Cache entries are keyed by the complete SearchRequest object, ensuring proper isolation between different search queries

## Technologies & How to Run

**Tech stack:**
- Java 21
- Spring Boot
- Maven

**How to run the project:**
1. Clone the repository and open it in your IDE or terminal.
2. Make sure you have Java 21 and Maven installed.
3. In the project root, run:
   
   ```bash
   ./mvnw spring-boot:run
   ```
   
   This will start the backend server (by default on http://localhost:8080).

**How to check in the browser:**
- The main API endpoint is `/api/repositories` and you can call it with different query parameters (see below for examples).

**Testing the API:**
- There is a file with ready-to-use HTTP requests for manual testing: [`docs/api-call-examples.http`](docs/api-call-examples.http)
- You can open this file in IntelliJ HTTP Client, VS Code REST Client, or Postman and run the examples directly.

## API Endpoint & Parameters

Here are the basics you need to know to use the API:

**Main endpoint:**
```
GET /api/repositories
```

**Query parameters:**
- `createdSince` (string, optional): ISO date (e.g. `2024-01-01`). Only repos created on or after this date are included. Defaults to `2008-04-01` if not set.
- `language` (string, optional): Filter by programming language (e.g. `java`, `python`).
- `sort` (string, optional): Sort order as supported by GitHub API (`stars`, `forks`, `updated`).
- `page` (integer, optional): Page number (1-based, default is 1).
- `size` (integer, optional): Number of results per page (default is 10, max 100).

**Example request:**
```
GET /api/repositories?createdSince=2025-07-01&language=java&sort=stars&page=3&size=15
```

The response includes a list of repositories, each with a calculated popularity score, and some metadata about your request.

```
{
  "total": 1488431,
  "items": [
    {
      "id": 953335676,
      "name": "GhidraMCP",
      "fullName": "LaurieWired/GhidraMCP",
      "url": "https://github.com/LaurieWired/GhidraMCP",
      "description": "MCP Server for Ghidra",
      "createdAt": "2025-03-23T05:36:55Z",
      "updatedAt": "2025-07-13T03:22:45Z",
      "language": "Java",
      "stargazersCount": 5386,
      "forksCount": 387,
      "popularityScore": 3849
    }
  ],
  "metadata": {
    "language": "java",
    "createdSince": "2025-01-01",
    "sort": "stars",
    "direction": "desc",
    "page": 1,
    "size": 1
  }
}
```
