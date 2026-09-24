# Mini SQL Engine
A from-scratch relational database engine built in Java with SQL parsing, query execution, and indexing.

## Overview
Mini SQL Engine is a relational database engine built from scratch in Java.
It supports SELECT, INSERT, and WHERE SQL operations against in-memory tables.
Queries are tokenized and parsed into structured Query objects, routed through
a planner layer, and executed against a storage layer — mirroring the internal
architecture of production databases like MySQL and PostgreSQL.

The engine implements two indexing strategies: a Hash Index for O(1) exact
match lookups, integrated into query execution, and a B+ Tree with linked-leaf
range scans, implemented as a standalone structure. Benchmarks on 10,000 rows
show the hash index performing ~830x faster than a full table scan for exact
matches.

---

## Motivation
Most students interact with databases as black boxes — writing SQL queries and
trusting the engine to figure out the rest. After completing a Database Systems
course (CS4350) where we built a transit scheduling system using JDBC and SQLite,
I became curious about what was actually happening underneath. How does a database
parse a query? How does an index make lookups faster? How does a query planner
decide which path to take?

This project is my answer to those questions. Rather than consuming a database,
I built one — from the parser that reads raw SQL strings, to the executor that
filters rows, to the indexing layer that makes it fast. The goal was to develop
the kind of systems intuition that tutorial projects and CRUD apps don't build:
understanding not just how to use tools, but how those tools work.

---

## Architecture & Design

### Architectural Style
This engine follows a pipeline architecture where each component has a single
responsibility and passes its output to the next stage: Parser → Planner →
Executor → Storage. This separation of concerns keeps each layer independently
testable and extensible — adding a new SQL operation like INSERT required
updating only the parser, query model, and executor without touching the planner
or storage layer. The architecture is directly inspired by interpreter design
patterns studied in Crafting Interpreters (R. Nystrom), applied here to SQL
parsing rather than general-purpose language parsing.

### Package Structure
```
mini-sql-engine/
├── docs/
│   ├── learning_log.md
│   └── README.md
├── src/
│   ├── main/java/
│   │   ├── benchmark/
│   │   │   └── Benchmark.java       # performance comparisons: scan vs index
│   │   ├── executor/
│   │   │   └── Executor.java        # query execution, index selection, row filtering
│   │   ├── main/
│   │   │   ├── Main.java            # entry point and database setup
│   │   │   └── Repl.java            # interactive SQL terminal
│   │   ├── parser/
│   │   │   ├── Parser.java          # SQL tokenization and query construction
│   │   │   ├── Query.java           # structured query model
│   │   │   └── QueryType.java       # enum: SELECT, INSERT
│   │   ├── planner/
│   │   │   └── Planner.java         # layer boundary; currently passes queries to the executor
│   │   └── storage/
│   │       ├── index/
│   │       │   ├── BPlusTree.java   # sorted index with linked-leaf range scans (standalone)
│   │       │   └── HashIndex.java   # O(1) exact match lookups
│   │       ├── Column.java          # column definition (name, DataType)
│   │       ├── Database.java        # top-level container for tables
│   │       ├── DataType.java        # enum: STRING, INTEGER, DOUBLE
│   │       ├── Row.java             # key-value store of column values
│   │       └── Table.java           # holds columns, rows, and indexes
│   └── test/java/
│       ├── executor/
│       │   └── ExecutorTest.java
│       └── parser/
│           └── ParserTest.java
└── pom.xml
```

### Key Design Decisions
- **Pipeline over monolith:** Each component (Parser, Planner, Executor, Storage)
  has a single responsibility. Adding INSERT required changes to only three classes
  — the rest of the pipeline was untouched.
- **In-memory storage:** Tables live in Java HashMaps and ArrayLists rather than
  on disk. This simplifies the implementation while still demonstrating query
  execution and indexing concepts accurately.
- **Two-path executor:** The executor checks for a hash index on `=` conditions
  before falling back to a full table scan. This mirrors how real query engines
  use indexes to choose execution paths.
- **Hash index for exact match, B+ tree for ranges:** Each index strategy has a
  specific use case. Hash index gives O(1) exact lookups; B+ tree's sorted leaf
  linked list enables range scans without backtracking. The B+ tree is not yet
  wired into the executor, so range queries currently use a full scan.
- **Immutable Column model:** Columns are defined once and never modified —
  consistent with how real database schemas work.

### Known Limitations
- B+ tree split only handles the root node — non-root leaf splits do not
  propagate up to parent nodes, and duplicate keys that span multiple leaves
  are not handled. Checking the original benchmark's result counts against a
  full scan showed the tree returning 0 rows for both the exact-match and range
  queries (expected 100 and 3,100), so the B+ tree benchmark has been removed
  pending the fix. Ancestor tracking for the fix is implemented;
  internal-node splitting is in progress.
- The B+ tree is not integrated into the executor; only the hash index is used
  during query execution.
- The planner is currently a pass-through; index selection happens in the executor.
- WHERE supports a single condition with `=`, `<`, or `>` on integer columns.
- INSERT values are stored as Strings — no automatic type casting to Integer
  or Double based on column DataType. Type handling would require extending
  the parser and executor.
- Parser assumes single-space-separated tokens — SQL strings with extra spaces
  or tabs will break tokenization.
- All data is in-memory and does not persist between sessions.

---

## Benchmark Results
| Operation          | Full Scan    | Hash Index    | Speedup  |
|--------------------|--------------|---------------|----------|
| Exact match (=)    | ~2,075,959ns | ~2,500ns      | ~830x    |

Measured on 10,000 rows with single-run `System.nanoTime()` timings — directional,
not statistically rigorous. B+ tree results pending the split-propagation fix
(see Known Limitations).

---

## How to Run
**Prerequisites:** Java 25, Maven

```bash
# Clone the repo
git clone https://github.com/reginald-meeks/mini-sql-engine.git
cd mini-sql-engine

# Run tests
mvn test

# Run the REPL
mvn exec:java -Dexec.mainClass="main.Main"

# Run benchmarks
mvn exec:java -Dexec.mainClass="benchmark.Benchmark"
```

---

## What I Learned
Building this project changed how I think about software I use every day. A
database always seemed like a black box — you write SQL and results come back.
Now I understand every layer: how a raw string becomes a structured query, how
an executor decides which path to take, how an index transforms a linear scan into
a constant-time lookup.

The performance numbers surprised me. Before benchmarking, I understood
intellectually that O(1) is faster than O(n). Seeing a hash index run 830x
faster than a full scan on 10,000 rows made that real in a way that no lecture
could.

The best analogy I came up with: a database is like a library. On paper, a
library is just books in a building. But a good library needs a catalog system,
a checkout process, organized shelving, and a way to find anything instantly.
The books are the data. Everything else is the database engine. That's what
I built.

I also found unexpected connections to my coursework — the SQL parser follows
the same tokenization patterns covered in Crafting Interpreters (R. Nystrom), and
the indexing strategies directly extended what I learned in a Database Systems course.
This project was where those concepts stopped being academic and
started making sense.

---

## Future Features
- Recursive B+ tree splitting for non-root nodes, with duplicate-key handling
  and a correctness test against full-scan results
- Integrate the B+ tree into the executor for range queries (`>=`, `<=`, `BETWEEN`)
- CREATE TABLE and DROP TABLE SQL support
- Persistent storage via file serialization
- Multi-column indexes and composite WHERE conditions (AND, OR)
- Query cost estimation in the planner for smarter index selection
- Type-aware INSERT parsing (auto-cast values to column DataType)
- Python benchmarking script with matplotlib visualization of scan vs index latency