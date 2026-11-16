package com.example.booklibrary.config;

import com.example.booklibrary.entity.Author;
import com.example.booklibrary.entity.Book;
import com.example.booklibrary.entity.Genre;
import com.example.booklibrary.repository.AuthorRepository;
import com.example.booklibrary.repository.BookRepository;
import com.example.booklibrary.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final String uploadDir;

    // Explicit Constructor Injection (mandatory)
    public DataInitializer(GenreRepository genreRepository,
                           AuthorRepository authorRepository,
                           BookRepository bookRepository,
                           @Value("${file.upload-dir:uploads/covers}") String uploadDir) {
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.uploadDir = uploadDir;
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (genreRepository.count() > 0) {
                return;
            }

            // 1. Initialize Genres
            Genre sciFi = genreRepository.save(new Genre("Science Fiction", "Speculative fiction based on futuristic concepts and scientific developments."));
            Genre fantasy = genreRepository.save(new Genre("Fantasy", "Stories set in imaginary worlds, often involving magic and mythical creatures."));
            Genre dystopian = genreRepository.save(new Genre("Dystopian", "Exploration of social and political structures in dark, nightmare worlds."));
            Genre classic = genreRepository.save(new Genre("Classic", "Literary masterworks that have stood the test of time."));

            // 2. Initialize Authors
            Author orwell = authorRepository.save(new Author("George Orwell", "george.orwell@literature.org", "English novelist, essayist, journalist, and critic.", LocalDate.of(1903, 6, 25)));
            Author tolkien = authorRepository.save(new Author("J.R.R. Tolkien", "jrr.tolkien@literature.org", "English writer, poet, philologist, and academic best known for The Lord of the Rings.", LocalDate.of(1892, 1, 3)));
            Author herbert = authorRepository.save(new Author("Frank Herbert", "frank.herbert@literature.org", "American science fiction author best known for the novel Dune.", LocalDate.of(1920, 10, 8)));
            Author asimov = authorRepository.save(new Author("Isaac Asimov", "isaac.asimov@literature.org", "American writer and professor of biochemistry, a master of hard science fiction.", LocalDate.of(1920, 1, 2)));
            Author shelley = authorRepository.save(new Author("Mary Shelley", "mary.shelley@literature.org", "English novelist who wrote the pioneering science-fiction Gothic novel Frankenstein.", LocalDate.of(1797, 8, 30)));

            // 3. Prepare a sample cover image in uploads folder
            String sampleCoverFileName = "sample_dune_cover.jpg";
            try {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                Path sampleFilePath = uploadPath.resolve(sampleCoverFileName);
                if (!Files.exists(sampleFilePath)) {
                    // Create a dummy image/byte array file
                    byte[] dummyImage = new byte[] {
                            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
                            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
                            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
                            (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00, (byte) 0xFF, (byte) 0xD9
                    };
                    Files.write(sampleFilePath, dummyImage);
                }
            } catch (Exception ignored) {
            }

            // 4. Initialize Books with Relationships
            Book book1 = new Book("1984", "9780451524935", "Winston Smith struggles against the totalitarian regime of Big Brother in Oceania.", 1949, 14.99, dystopian);
            book1.addAuthor(orwell);
            bookRepository.save(book1);

            Book book2 = new Book("Animal Farm", "9780451526342", "A satirical allegorical novella reflecting events leading up to the Russian Revolution.", 1945, 11.50, classic);
            book2.addAuthor(orwell);
            bookRepository.save(book2);

            Book book3 = new Book("Dune", "9780441172719", "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides.", 1965, 19.99, sciFi);
            book3.addAuthor(herbert);
            book3.setCoverImage(sampleCoverFileName);
            bookRepository.save(book3);

            Book book4 = new Book("The Hobbit", "9780547928227", "Bilbo Baggins, a hobbit, is whisked away into an epic quest to reclaim the Lonely Mountain.", 1937, 16.00, fantasy);
            book4.addAuthor(tolkien);
            bookRepository.save(book4);

            Book book5 = new Book("Foundation", "9780553293357", "Hari Seldon uses psychohistory to preserve civilization across a galactic empire.", 1951, 15.25, sciFi);
            book5.addAuthor(asimov);
            bookRepository.save(book5);

            Book book6 = new Book("Frankenstein", "9780141439471", "Victor Frankenstein creates a sentient creature in an unorthodox scientific experiment.", 1818, 12.99, classic);
            book6.addAuthor(shelley);
            bookRepository.save(book6);
        };
    }
}
