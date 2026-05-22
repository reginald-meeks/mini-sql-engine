# Mini SQL Engine
A from-scratch relational database engine built in Java with SQL parsing, query execution, and indexing.

## Overview


---

## Motivation


---

## Architecture & Design


### Architectural Style



### Package Structure
```
src/main/java/
├── main/         # Entry point and end-to-end wiring
├── parser/       # SQL tokenization and Query object construction
├── executor/     # Query execution and row filtering
├── planner/      # Query routing between parser and executor
└── storage/      # In-memory data models (Table, Row, Column, Database)
    └── index/    # HashIndex for O(1) exact match lookups
```

---

## Benchmark Results
| Operation          | Full Scan    | Index Lookup  | Speedup  |
|--------------------|--------------|---------------|----------|
| Exact match (=)    | ~2,075,959ns | ~2,500ns      | ~830x    |
| Range query (>=,<=)| ~888,333ns   | ~3,250ns      | ~273x    |


---

## How to Run


---

## What I Learned
