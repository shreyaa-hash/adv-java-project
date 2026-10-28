# Book Store: CrudRepository vs JpaRepository

A small bookstore inventory web app built for an Advanced Java project. It performs the same book-management tasks through two Spring Data repository interfaces so their capabilities can be demonstrated side by side.

## What the project does

The app stores books with an ID, title, author, price, quantity, and category. The browser interface can list, add, edit, and delete books. Use the tabs above the inventory to switch between two API implementations:

- **CrudRepository** (`/api/books/crud`) demonstrates the basic create, read, update, and delete API.
- **JpaRepository** (`/api/books/jpa`) demonstrates the same CRUD tasks plus sorting, pagination support, and explicit flush methods. The screen exposes sorting; the repository API provides paging methods as well.

Both repository interfaces map to the same `Book` entity/table, so changes made through one tab are visible in the other. This is useful in a classroom comparison because the data and operations stay the same while the repository type changes.

## Technologies and what each one does

| Technology | How this project uses it |
| --- | --- |
| Java 25 | Language used for the entity, repositories, REST controllers, and application setup. |
| Spring Boot 3.5.16 | Starts and configures the web app, embeds a web server, and wires components together. |
| Spring Web | Exposes HTTP/JSON endpoints for listing and changing books. |
| Spring Data JPA | Creates repository implementations and connects repository calls to the database through JPA. |
| JPA / Hibernate | Maps the `Book` Java class to a relational `books` table and performs persistence. |
| `CrudRepository` | Basic repository contract used by `BookCrudRepository`. |
| `JpaRepository` | Extended JPA repository contract used by `BookJpaRepository`; includes list, paging/sorting, batch, and flush operations. |
| MySQL | Default relational database for storing the books in `bookstore_db`. |
| H2 | Optional local database profile for trying the app without MySQL. |
| HTML | Structure of the bookstore page and book form. |
| CSS | Layout, colors, responsive styling, and visual presentation. |
| JavaScript | Calls the REST API with `fetch`, renders inventory rows, switches repository modes, and handles form actions. |
| Maven | Downloads dependencies and builds/runs the Java application using `pom.xml`. |

## Project structure

```text
src/main/java/com/bookstore/
  BookStoreApplication.java          Application entry point
  model/Book.java                    JPA entity and validation rules
  repository/BookCrudRepository.java CrudRepository interface
  repository/BookJpaRepository.java  JpaRepository interface
  controller/BookCrudController.java REST endpoints using CrudRepository
  controller/BookJpaController.java  REST endpoints using JpaRepository
  config/SampleData.java             Adds four sample books to an empty database
src/main/resources/
  application.properties             Shared configuration; MySQL is the default profile
  application-h2.properties          Optional local file-based H2 settings
  application-mysql.properties       MySQL connection settings
  static/index.html                  Frontend page
  static/styles.css                  Frontend styles
  static/app.js                      Frontend behavior and API calls
pom.xml                              Maven dependencies and Java/Spring versions
```

## How the application works

1. The user opens the page served by Spring Boot at `http://localhost:8080`.
2. The JavaScript requests book data from either `/api/books/crud` or `/api/books/jpa`.
3. A REST controller receives the request and calls its repository interface.
4. Spring Data provides the repository implementation at runtime. Hibernate turns those method calls into database work.
5. The database stores the book rows. The controller returns JSON and the page refreshes its table.

An **entity** is a Java class that represents database data. Here, each `Book` object corresponds to one row in the `books` table. The `@Entity`, `@Table`, `@Id`, and `@GeneratedValue` annotations tell JPA how to map it. Validation annotations reject blank titles/authors, negative prices or quantities, and values that exceed the configured limits.

## Run it

Requirements: JDK 25 and Maven 3.9.15 or newer. An IDE such as IntelliJ IDEA or VS Code is optional.

### Default: MySQL

Install and start MySQL Server. The app connects to `localhost:3306` and uses the `bookstore_db` database. The configured URL creates that database if it does not exist; the MySQL account must have permission to create databases and tables.

Set the MySQL username and password in PowerShell before starting the app. Replace the example password with your own local MySQL password:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-local-mysql-password"
mvn spring-boot:run
```

If MySQL uses a different port or database name, set `DB_URL` too. Keep real credentials out of source control. On the first run, four example books are inserted. Sample books are added only when the database is empty.

### Optional: H2 (no database server needed)

From the project directory, run:

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

The H2 database is saved locally under `./data/`. Then visit [http://localhost:8080](http://localhost:8080). H2 is intended as a fallback/demo option; MySQL is the normal project setup. For a real deployment, use a dedicated database user, migrations, and a secret manager rather than `ddl-auto=update`.

## API endpoints

Both APIs use JSON request and response bodies. A book request looks like:

```json
{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "price": 650.00,
  "quantity": 12,
  "category": "Programming"
}
```

| Method | CrudRepository URL | JpaRepository URL | Purpose |
| --- | --- | --- | --- |
| GET | `/api/books/crud` | `/api/books/jpa` | List books |
| GET | `/api/books/crud/{id}` | `/api/books/jpa/{id}` | Find one book |
| POST | `/api/books/crud` | `/api/books/jpa` | Add a book |
| PUT | `/api/books/crud/{id}` | `/api/books/jpa/{id}` | Replace a book's editable fields |
| DELETE | `/api/books/crud/{id}` | `/api/books/jpa/{id}` | Delete a book |
| — | — | `/api/books/jpa?sort=price` | List sorted by title, author, price, quantity, or ID |
| — | — | `/api/books/jpa/count` | Count all books |

If the requested ID does not exist, the API responds with HTTP 404. Invalid fields are rejected by bean validation.

## The repository comparison

Both interfaces supply `save`, `findById`, `findAll`, `existsById`, `count`, and delete operations. `BookCrudRepository` intentionally declares only `CrudRepository<Book, Long>`: it is the compact option for basic data management. Its `findAll()` returns an `Iterable`.

`BookJpaRepository` declares `JpaRepository<Book, Long>`. It is designed for JPA and adds useful collection, sorting, pagination, batch, and flush operations on top of its inherited repository contracts. This app demonstrates sorted listing and uses `saveAndFlush()` for create/update plus `flush()` after deletion. A flush sends pending persistence changes to the database immediately; it does not by itself mean a transaction has been committed.

The exact inheritance chain and method signatures can vary across Spring Data generations. The comparison in this project is based on the Spring Data version managed by Spring Boot 3.5.16. The practical choice is: use `CrudRepository` when the small CRUD contract is all that is needed; use `JpaRepository` when JPA-specific conveniences such as sorting, paging, and flushing are useful.

## Suggested explanation for your teacher

> “This project is a bookstore inventory app built with Java and Spring Boot. The `Book` entity maps to a table in MySQL. I created two repository interfaces for the same entity: one extends `CrudRepository` and one extends `JpaRepository`. Both can add, find, update, and delete books. `JpaRepository` provides extra JPA-oriented features, which I demonstrate with sorting and flush methods. The web page calls REST endpoints in the controllers, and the controllers call the selected repository. Spring Data JPA and Hibernate translate those repository operations into database work on MySQL.”

For the demonstration, add a book in the CrudRepository tab, switch to JpaRepository and show that the same book is still there. Then sort the JPA list by price and explain that sorting is supported by the JPA repository API. You can compare the controller/repository code and point out the extra `saveAndFlush`, `flush`, and `Sort` usage.

## Notes

- The UI and backend are served by the same Spring Boot application, so there is no separate frontend server to configure.
- MySQL is the default database; H2 is available by activating the `h2` profile.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for a learning demo. Production applications generally manage schema changes with database migrations.
- This is an educational CRUD example; it does not include login, order processing, or payment features.
#   a d v - j a v a - p r o j e c t  
 