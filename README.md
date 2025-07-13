# GitHub Repository Search API

A backend service for searching GitHub repositories with custom popularity scoring based on stars, forks, and update recency — built for the Redcare Pharmacy Backend Coding Challenge.

## Project Overview

The goal of this project is to build a backend app that scores GitHub repositories. The app works as a wrapper around GitHub’s public search API, getting repositories and adding a custom popularity score based on stars, forks, and how recently the repo was updated. Also, there is a single service in the domain layer that acts as an orchestration unit. In line with DDD ideas, our domain object is "rich"—it knows by itself how to calculate its own score, so the core logic stays in the domain layer.

Because I had limited time and wanted to keep things simple, I focused on the most important parts for this challenge. I decided not to add things like caching or retry logic, which would be needed in a real-world app. Instead, the main goal was to show a good architecture, where different parts of the app are separated and can work on their own. I skipped some small improvements that would be required for a production system, but our structure makes it easy to add them later if needed, without big changes.

The main idea was to show clear separation between layers in the app. I used a lightweight hexagonal (ports and adapters) architecture, with the core logic in the `domain` layer and a simple `api` layer on top. For this small task, I know a classic layered approach would also work, but here I wanted to show that I understand how to build more complex systems if needed. I also tried to keep the package structure as flat as possible, to avoid too much complexity for such a small project.

A quick note about test coverage: I did not write tests for every small component (for example, I skipped writing tests for simple mappers between domain and DTOs). Instead, I focused on the most important things: I have unit tests for the repository score calculation, unit tests to check that low-level exceptions are correctly turned into high-level exceptions, and a full-cycle integration test that checks the main logic. For the integration test, I also made a shortcut — I mocked the `RepositoryProvider` with predefined responses instead of using a web server to simulate GitHub (such as OkHttp's `MockWebServer`). This way, I covered the critical parts, but didn’t spend time on less important tests, given the time limits.

## Design Decisions and Limitations

While working on this project, I ran into a few places where there was no perfect answer and each option had its own pros and cons. In this section, I just list all the trade-offs or decisions I made as I went along. These are not always connected to each other, but just show my thinking process and how I tried to solve problems as they came up.

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

The GitHub repository object is huge and full of nested properties that describe every detail of a repo. For this challenge, I picked only the fields that really matter for my use case. My thinking is simple: the domain model should only include the data I actually need. If I ever need more fields, I can always add them later. I also don't like the idea of just passing the whole massive GitHub object straight to the client—most of that data would never be used, but it would make responses bigger, slow things down, and waste resources. Smaller models are easier to read, maintain, and support. This way, there is a clear boundary between my own domain and the external GitHub API.

### Default Parameter Behavior

While testing, I ran into a problem with GitHub's API: the `q` parameter (the search query) is required, but in my API, both filter parameters are optional. If the client doesn't set either filter, I would end up making a call to GitHub without a `q` at all - which just fails. My first idea was to force at least one filter to always be set, but that felt wrong and would have meant rewriting a lot of logic. Instead, I decided to quietly set a default value for one of the filters so `q` is always present. The cleanest option was to default the `created` date filter to something neutral and far in the past. I picked April 1, 2008 (the day GitHub was founded), since every real repo will have been created after that date. This way, the API works out of the box, and users don’t have to worry about this GitHub issue at all.


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

I decided not to spend time on generating a full Swagger/OpenAPI spec for this challenge, but here are the basics you need to know to use the API:

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
