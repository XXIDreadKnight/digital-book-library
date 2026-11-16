# Digital Book Library - Spring Boot Backend Application

A robust, enterprise-grade Spring Boot 3 backend application designed for managing a digital book library catalog, complete with authors, genres, and local cover image file uploads.

This project strictly adheres to **Layered Architecture**, **Spring Data JPA specifications**, **Constructor-based Dependency Injection**, **JSR-303 Data Validation**, **Server-side Pagination & Sorting**, and **Multipart File Storage**.

---

## 📋 Compliance with Mandatory Technical Requirements

### 1. Project Structure and Bean Management
- **Layered Architecture**: The codebase is cleanly separated into dedicated packages:
  - `com.example.booklibrary.entity` - JPA Entities (`Book`, `Author`, `Genre`)
  - `com.example.booklibrary.repository` - Spring Data JPA Repositories
  - `com.example.booklibrary.service` & `service.impl` - Business logic interfaces and implementations
  - `com.example.booklibrary.controller` - REST Controllers
  - `com.example.booklibrary.dto` - Request and Response Data Transfer Objects
  - `com.example.booklibrary.exception` - Custom exceptions & `@RestControllerAdvice` Global Exception Handler
  - `com.example.booklibrary.config` - Spring Configurations (`OpenApiConfig`, `DataInitializer`)
- **Stereotype Annotations**:
  - `@RestController` on all API controllers (`BookController`, `AuthorController`, `GenreController`)
  - `@Service` on all service implementations (`BookServiceImpl`, `AuthorServiceImpl`, `GenreServiceImpl`, `FileStorageServiceImpl`)
  - `@Repository` on all repository interfaces (`BookRepository`, `AuthorRepository`, `GenreRepository`)
  - `@Configuration` on configuration and startup classes (`OpenApiConfig`, `DataInitializer`)
- **Dependency Injection (DI)**:
  - **Constructor Injection is strictly enforced across all components**.
  - **Zero field injection**: Writing `@Autowired` directly on fields is completely prohibited and absent throughout the entire project.

---

### 2. Complex Database and Spring Data (JPA / H2)
- **Minimum 3 Entities**:
  - `Book` (ID, title, ISBN, description, publication year, price, coverImage, genre, authors, timestamps)
  - `Author` (ID, full name, email, biography, birth date, books)
  - `Genre` (ID, name, description, books)
- **Entity Relationships (Two Distinct Logical Types)**:
  - **Type 1: `@ManyToOne` / `@OneToMany`**:
    - `Genre` has many `Books` (`@OneToMany(mappedBy = "genre")`)
    - `Book` belongs to a single `Genre` (`@ManyToOne` with `@JoinColumn(name = "genre_id")`)
  - **Type 2: `@ManyToMany`**:
    - `Book` and `Author` have a bidirectional Many-to-Many relationship joined via intermediate table `book_authors` (`@JoinTable`). A book can have multiple co-authors, and an author can write multiple books.
- **Repository Layer & Query Methods**:
  - All repositories extend `JpaRepository<T, ID>`.
  - **Method-Name Query Conventions (at least 2 implemented)**:
    1. `findByTitleContainingIgnoreCaseAndPublicationYearGreaterThanEqual(String title, Integer publicationYear)`
    2. `findByPriceLessThanEqualAndGenreNameIgnoreCase(Double maxPrice, String genreName)`
    3. `findByGenreId(Long genreId, Pageable pageable)`
  - **Complex / Custom Queries using `@Query` (at least 2 implemented)**:
    1. `@Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")` - Joins Many-to-Many table to query books by author ID with pagination.
    2. `@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")` - Multi-field search with pagination.
    3. `@Query("SELECT new com.example.booklibrary.dto.GenreStatsDTO(g.name, COUNT(b), AVG(b.price)) FROM Genre g LEFT JOIN g.books b GROUP BY g.name")` - Statistical aggregation query calculating book count and average price per genre.
- **Transactions**:
  - `@Transactional` is placed on all service methods modifying database state (`createBook`, `updateBook`, `deleteBook`, `uploadCoverImage`, `createAuthor`, `updateAuthor`, etc.).
  - Read operations utilize `@Transactional(readOnly = true)` for optimized performance.

---

### 3. Controller Layer and Request Handling
- **HTTP Methods Correctly Applied**:
  - `@GetMapping` for all retrieval and search operations.
  - `@PostMapping` for creation endpoints and file upload operations.
  - `@PutMapping` for resource updates.
  - `@DeleteMapping` for resource deletions.
- **Request Handling Mechanisms**:
  - `@PathVariable`: Used for retrieving IDs (`/api/books/{id}`, `/api/authors/{id}`, `/api/books/{id}/cover`).
  - `@RequestParam`: Used for pagination/sorting (`page`, `size`, `sort`), filtering (`title`, `minYear`, `maxPrice`, `genre`), keyword searching (`keyword`), and receiving `MultipartFile file`.
  - `@RequestBody` with `@Valid`: Used for deserializing and validating JSON payloads (`BookRequestDTO`, `AuthorRequestDTO`, `GenreRequestDTO`).

---

### 4. Validation, File Upload, and Pagination
- **JSR-303 Validation**:
  - Annotated on all DTO request models with `@NotBlank`, `@NotNull`, `@Size`, `@Min`, `@Max`, `@Pattern`, `@Email`, and `@Past`.
  - Enforced in Controller methods using `@Valid @RequestBody`.
  - A `@RestControllerAdvice` Global Exception Handler intercepts `MethodArgumentNotValidException` and produces a structured map of field errors with HTTP 400 Bad Request.
- **File Upload Feature**:
  - Endpoint `POST /api/books/{id}/cover` accepts `multipart/form-data` with `@RequestParam("file") MultipartFile file`.
  - Files are validated (size limits, allowed extensions: `.jpg`, `.png`, `.webp`, `.gif`, empty checks).
  - Saved physically on local disk under `uploads/covers/` using UUID-prefixed filenames to prevent collisions.
  - The generated file path/name is persisted in the `Book` entity's `coverImage` column.
  - Download/streaming endpoint `GET /api/books/{id}/cover` serves the physically saved file directly to the client/browser.
- **Server-Side Pagination & Sorting**:
  - `GET /api/books` accepts Spring Data `Pageable` parameters: `?page=0&size=5&sort=title,asc` (or `sort=price,desc`).
  - Returns `PageResponseDTO` containing content, page number, page size, total pages, total elements, and last flag.

---

## 🛠️ Technology Stack
- **Framework**: Spring Boot 3.4.3
- **Language**: Java 21
- **Database**: H2 In-Memory Database (`jdbc:h2:mem:librarydb`)
- **Persistence**: Spring Data JPA & Hibernate
- **Validation**: Jakarta / Hibernate Validator (JSR-303)
- **API Documentation**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Build Tool**: Maven

---

## 🚀 Running the Application

### Option A: Using Maven (Installed locally)
```bash
cd digital-book-library
mvn spring-boot:run
```

### Option B: Using Maven Wrapper
On Windows:
```cmd
.\mvnw.cmd spring-boot:run
```

On Linux / macOS:
```bash
./mvnw spring-boot:run
```

### Option C: Opening in IDE
Open the `digital-book-library` folder in **IntelliJ IDEA**, **Eclipse**, or **VS Code** as a Maven project, and run `DigitalBookLibraryApplication.java`.

---

## 🌐 Interactive Documentation & Tools

Once running, navigate to:
- **Swagger UI (Interactive API Tester)**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **OpenAPI JSON Docs**: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
- **H2 Web Console**: [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
  - JDBC URL: `jdbc:h2:mem:librarydb`
  - User Name: `sa`
  - Password: *(leave blank)*

---

## 📡 API Endpoints Reference

### 1. Books (`/api/books`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/books?page=0&size=10&sort=title,asc` | Get paginated & sorted list of books |
| `GET` | `/api/books/{id}` | Get single book by ID with author and genre info |
| `POST` | `/api/books` | Create a new book (`@Valid @RequestBody BookRequestDTO`) |
| `PUT` | `/api/books/{id}` | Update existing book (`@PathVariable` + `@Valid @RequestBody`) |
| `DELETE`| `/api/books/{id}` | Delete book by ID (`@PathVariable`) |
| `POST` | `/api/books/{id}/cover` | Upload cover image (`multipart/form-data`, `@RequestParam("file")`) |
| `GET` | `/api/books/{id}/cover` | Download / view cover image physically stored on disk |
| `GET` | `/api/books/search?keyword=dune` | Search books by keyword across title/description (`@Query`) |
| `GET` | `/api/books/by-author/{authorId}` | Find books by author ID joining Many-to-Many (`@Query`) |
| `GET` | `/api/books/filter/title-year?title=1984&minYear=1900` | Filter by title and year (Method-Name Query 1) |
| `GET` | `/api/books/filter/price-genre?maxPrice=20.0&genre=Fantasy` | Filter by price and genre (Method-Name Query 2) |
| `GET` | `/api/books/statistics` | Aggregated book count & average price per genre (`@Query`) |

### 2. Authors (`/api/authors`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/authors` | List all authors |
| `GET` | `/api/authors/{id}` | Get author by ID |
| `POST` | `/api/authors` | Create author (`@Valid @RequestBody AuthorRequestDTO`) |
| `PUT` | `/api/authors/{id}` | Update author |
| `DELETE`| `/api/authors/{id}` | Delete author |
| `GET` | `/api/authors/search?name=Orwell` | Search authors by name |

### 3. Genres (`/api/genres`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/genres` | List all genres |
| `GET` | `/api/genres/{id}` | Get genre by ID |
| `POST` | `/api/genres` | Create genre (`@Valid @RequestBody GenreRequestDTO`) |
| `PUT` | `/api/genres/{id}` | Update genre |
| `DELETE`| `/api/genres/{id}` | Delete genre |
| `GET` | `/api/genres/search?name=Science` | Search genres by name |

---

## 🧪 Quick Test Examples (cURL)

### 1. Create a New Book (POST with Validation)
```bash
curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Brave New World",
    "isbn": "9780060850524",
    "description": "A dystopian novel exploring a technologically advanced future society.",
    "publicationYear": 1932,
    "price": 15.99,
    "genreId": 3,
    "authorIds": [1]
  }'
```

### 2. Get Paginated & Sorted Books
```bash
curl "http://localhost:8081/api/books?page=0&size=5&sort=price,desc"
```

### 3. Upload a Book Cover Image (`multipart/form-data`)
```bash
curl -X POST "http://localhost:8081/api/books/1/cover" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@my_book_cover.jpg"
```

### 4. Search Books Using Custom `@Query`
```bash
curl "http://localhost:8081/api/books/search?keyword=totalitarian"
```

### 5. Get Genre Statistics (`@Query` Aggregation)
```bash
curl "http://localhost:8081/api/books/statistics"
```
