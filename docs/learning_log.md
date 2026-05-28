# Learning Log

## Progress
Day 1: foundational data models and project setup
Day 2: implement parser to take raw SQL and turn it into a Query object
Day 3: executor implementation
Day 4: implement planner class to connect parser and executor and main class to run sample query
Day 5: implement hash index and tests for executor and parser
Day 6: implement B+Tree class with insert, search, and rangeSearch
Day 7: implement REPL and INSERT
Day 8: add benchmarks comparing full scan, hash index, and B+ tree
Day 9:



---


## Day 1
### What I built
Designed the full architecture of the database engine (Parser → Planner →
Executor → Storage + Index). Created four core data models in the storage
package: DataType (enum), Column, Row, and Table, and Database. Set up Maven
project, package structure, and connected to GitHub.

### What confused me
Minor Java Syntax and concepts resulting in overcomplicated implementation

### How I resolved it
resorted to simplest and most efficient way to get the things I needed done, done.

### Performance notes
N/A — no measurable performance work yet. Benchmarking begins once
query execution is implemented.

### If I restarted, I would
-



---


## Day 2
### What I built
Built the Parser class which takes a raw SQL string and returns a structured
Query object. Implemented tokenization using split(), keyword detection for
SELECT, FROM, and WHERE clauses, column extraction with comma cleanup, and
condition parsing. Also created the Query and QueryType classes in the parser
package.

### What confused me
- flagging concept for the loop in Parser
- parser class structure

### How I resolved it
Took a look at the bigger picture to better understand where things should go and why.

### Performance notes
N/A — no measurable performance work yet. Parser correctness will be
validated when the executor is wired up in Day 3.


---


## Day 3
### What I built
Built the Executor class which takes a parsed Query object and runs it against
the Database. Implemented row filtering logic with WHERE clause support using
a switch statement on the condition operator. The executor retrieves the target
table from the database, iterates through all rows, and returns only the rows
that match the query conditions.

### What confused me
- looping through each row in the table and deciding if the row belongs in the results or not

### How I resolved it
- created two conditions, if there is a where clause and if there isn't a where clause. If there is a where clause, 
compare the row's value and the query's condition value and see if its a match in order to filter the rows.

### Performance notes
N/A — no benchmarking yet. Performance will become measurable once we wire
up a main entry point and run queries against real data in Day 5+.


---


## Day 4
### What I built
Built the Planner class to bridge the Parser and Executor. Created the Main
class to wire all components together end to end. Ran the first real query
"SELECT Name, Age FROM users WHERE Age > 20" against a hardcoded in-memory
users table and correctly returned Alice (25) and Charlie (30), filtering out
Bob (17). Full pipeline working: Parser → Planner → Executor → Storage.

### What confused me
- null pointer error: uppercase mismatch between parser and table name

### How I resolved it
- hardcoded all the names to be uppercase for the sake of the sample query

### Performance notes
N/A — no formal benchmarking yet. First successful end-to-end query confirms
correctness. Benchmarking begins in Day 5+ once indexing is implemented and
we can compare full table scan vs index lookup performance.


---


## Day 5
### What I built
Added JUnit 5 to pom.xml and wrote unit tests for the Parser (testParseTableName,
testParseSelectedColumns, testParseWhereCondition) and Executor
(testWhereGreaterThan, testWhereLessThan, testNoWhereClause) with a @BeforeEach
setup method to avoid repeated test data. Built HashIndex class in storage/index
using HashMap<Object, ArrayList<Row>> for O(1) exact match lookups. Added
buildIndex and getIndex methods to Table. Wired the hash index into the Executor
as a fast path for = queries, falling back to full table scan for range queries.

### What confused me
- some of the HashMap functions
- recalling past implementations after returning from a few weeks break

### How I resolved it
- reviewed hashmap functions
- reviewed my documentation

### Performance notes
Hash index provides O(1) lookup for exact match queries (WHERE col = value).
Full table scan remains O(n) for range queries (>, <). B+ tree index in Day 6
will address range query performance. Formal benchmarking comparing scan vs
index will be added in Day 8.


---


## Day 6
### What I built
Built a B+ tree implementation in Java with insert, search, and rangeSearch
methods. Internal nodes navigate by key comparisons, leaf nodes store actual
row data. Leaves are linked together for efficient range scans. Handles root
splitting when a leaf exceeds the order. Known limitation: non-root leaf splits
do not propagate up to parent nodes — noted as a future improvement.

### What confused me
- translating conceptual understanding of the B+ tree onto code

### How I resolved it
- studying up on B+ Tree implementations


### Performance notes
B+ tree search is O(log n) vs hash index O(1) for exact matches, but B+ tree
supports range queries which hash index cannot. Range scan efficiency comes
from the leaf linked list — no backtracking needed once the start is found.


---


## Day 7
### What I built
Added INSERT support to the engine. Updated QueryType enum with INSERT, updated
Parser to handle INSERT INTO table VALUES (...) syntax with parenthesis and comma
stripping, added values field to Query class, and updated Executor to handle INSERT
by matching values to columns by index and adding a new Row to the table. Built a
REPL (Read-Eval-Print Loop) in Repl.java that accepts SQL input from the terminal,
parses and executes it, and prints results in a loop until EXIT is typed. Wired
REPL into Main.java replacing the hardcoded query.

### What confused me
- 

### How I resolved it


### Performance notes
N/A — INSERT adds O(1) to the end of the rows ArrayList. REPL performance
is I/O bound. Formal benchmarking in Day 8.


---


## Day 8
### What I built
Built a Benchmark class with 5 benchmark methods comparing full table scan vs
hash index vs B+ tree on 10,000 rows. Measured exact match lookup (AGE = 50)
and range query (AGE 20-50). Results: hash index is ~830x faster than full scan
for exact matches, B+ tree is ~273x faster for range queries. Numbers will be
added to README.

### What confused me
- 

### How I resolved it


### Performance notes
Exact match - Full scan: ~2,075,959ns | Hash index: ~2,500ns | B+ tree: ~4,667ns
Range query - Full scan: ~888,333ns   | B+ tree range: ~3,250ns
All tests on 10,000 rows, Java nanoTime measurements.



