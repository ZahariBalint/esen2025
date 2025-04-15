package com.esen.bookstore.data;

import com.esen.bookstore.model.Book;
import com.esen.bookstore.repository.BookRepository;
import com.esen.bookstore.repository.BookStoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class DataLoader {

    private final BookRepository bookRepository;
    private final BookStoreRepository bookstoreRepository;

    public DataLoader(BookRepository bookRepository, BookStoreRepository bookStoreRepository) {
        this.bookRepository = bookRepository;
        this.bookstoreRepository = bookStoreRepository;
    }

    @Value("classpath:data/books.json")
    private Resource booksResource;
    @Value("classpath:data/bookstores.json")
    private Resource bookstoresResource;

    @PostConstruct
    public void loadData(){
        var objectMapper = new ObjectMapper();

        try{
            var booksJson = StreamUtils.copyToString(booksResource.getInputStream(), StandardCharsets.UTF_8);
            var books = objectMapper.readValue(booksJson, new TypeReference<List<Book>>(){});
            bookRepository.saveAll(books);
        } catch (IOException e) {
            System.out.printf("Cannot seed database ",e);
        }
    }
}
