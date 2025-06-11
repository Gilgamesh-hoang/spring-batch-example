# Spring Batch Order Processing Project

This project demonstrates a simple two-step batch job using **Spring Batch** to:
1. Read `Order` data from a CSV file and persist it to a database.
2. Read persisted `Order` entities from the database, process them, and write a summary to a new CSV file.

---

## 📦 Features

- Spring Batch with two distinct `Step`s
- Use of `FlatFileItemReader` to read CSV files
- Use of `JpaPagingItemReader` to read data from the database
- Writing data to both database and file output
- Custom processing logic in each step
- Separation of configuration into clean, modular classes

---

## 🛠 Technologies Used

- Java 17+
- Spring Boot
- Spring Batch
- Spring Data JPA
- MySQL (H2 in-memory for simplicity)
- Lombok

---

## 🔁 Job Workflow

### Step 1: Import Orders from CSV

- **Reader**: Reads `Order` entries from a CSV file (path configured via `application.properties`)
- **Processor**: Performs simple validation/logging
- **Writer**: Saves valid `Order` objects into the database via `OrderRepository`

### Step 2: Export High-Value Order Summary

- **Reader**: Fetches all `Order` entries from the database using JPA
- **Processor**: Filters out orders with `itemPrice <= 13,000,000`
- **Writer**: Outputs `OrderSummary` data to `output/orders_summary.csv`

---

## ▶️ How to Run

1. Clone the repository
2. Ensure you have Java and Maven installed
3. Run MySQL server, then create a database named `batch_db` (or change the name in `application.properties`)
4. Run `batch-sql-init.sql` to create tables which Spring Batch will use.
4. Run the application:
```bash
mvn spring-boot:run
```
4. The batch job will automatically execute on startup
5. Check the console log and `output/orders_summary.csv` for results

---

## 📌 Notes

- You can skip the header row in the CSV by enabling `reader.setLinesToSkip(1)` in the config.
- Make sure the `resources/orders.csv` file exists and is properly formatted.
- The output file `output/orders_summary.csv` will be overwritten each run.

---
