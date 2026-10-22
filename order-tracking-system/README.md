# Order Tracking System

A simple order-tracking web application. A customer enters an order ID and sees the
current status, order details and a chronological tracking timeline, all read from MySQL.

**Tech stack:** HTML, CSS, JavaScript (frontend) | Java (backend, built-in HttpServer + JDBC) | MySQL (database)

## Features

- Clean tracking page with an order ID input
- Retrieves the order and its full tracking history from MySQL
- Shows current status, customer-facing details, a 5-step progress bar and a chronological timeline
- Clear message when an order does not exist
- Five sample orders at different delivery stages (ORD-1001 to ORD-1005)

## Order lifecycle

Order Placed -> Confirmed & Packed -> Shipped / In Transit -> Out for Delivery -> Delivered

## Design rule

`orders` stores only the current status. `tracking_history` stores one row per status change.
History rows are only ever inserted, never overwritten. `OrderDAO` therefore exposes
read methods only (no update or delete for history).

## OOP concepts used

| Concept | Where |
|---|---|
| Encapsulation | Private final fields with getters in `Order` and `TrackingEvent` |
| Inheritance | `Order` and `TrackingEvent` extend the abstract `BaseEntity` |
| Abstraction | `BaseEntity` (abstract class), `OrderDAO` (interface) |
| Polymorphism | `describe()` overridden in each model; each `OrderStatus` constant overrides `getDescription()`; an `OrderDAO` reference holds an `OrderDAOImpl` |

## Project structure

```
order-tracking-system/
├── lib/        MySQL JDBC driver (jar)
├── database/   schema.sql, seed.sql
├── src/        Main.java, model/, enums/, dao/, handler/, util/
└── public/     index.html, style.css, script.js
```

## Setup

1. Install JDK 17+ and MySQL Server + Workbench.
2. Download MySQL Connector/J (Platform Independent) and put the `.jar` inside `lib/`.
3. In MySQL Workbench run `database/schema.sql`, then `database/seed.sql`.
4. Open `src/util/DBConnection.java` and set your MySQL password.
5. Open the `order-tracking-system` folder in VS Code and run `src/Main.java`.
6. Open http://localhost:8080 in a browser.

Command-line alternative (from the project root; use `;` instead of `:` on Windows):

```
javac -d bin -sourcepath src src/Main.java
java -cp "bin:lib/*" Main
```

## Sample orders

| Order ID | Stage |
|---|---|
| ORD-1001 | Order Placed |
| ORD-1002 | Confirmed & Packed |
| ORD-1003 | Shipped / In Transit |
| ORD-1004 | Out for Delivery |
| ORD-1005 | Delivered |
| ORD-9999 | Does not exist (shows the not-found message) |

## Limitations

- Logistics is simulated: timestamps and locations are illustrative sample data, not live delivery data.
- Read-only demo: status changes are made directly in the database; there is no admin or operator interface.
- Order IDs are guessable and there is no customer verification or login.
- The database password is stored in source code (acceptable for a local demo only).
- Single-threaded server intended for local demonstration.
