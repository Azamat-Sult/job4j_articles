package ru.job4j.articles.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.model.Article;
import ru.job4j.articles.model.Word;
import ru.job4j.articles.service.generator.ArticleGenerator;
import ru.job4j.articles.store.Store;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleArticleService implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleArticleService.class.getSimpleName());

    private final ArticleGenerator articleGenerator;

    public SimpleArticleService(ArticleGenerator articleGenerator) {
        this.articleGenerator = articleGenerator;
    }

    @Override
    public void generate(Store<Word> wordStore, int count, int step, Store<Article> articleStore) {
        LOGGER.info("Геренация статей в количестве {}", count);
        var words = wordStore.findAll();
        int iterations = count / step;
        for (int iteration = 0; iteration < iterations; iteration++) {
            int start = iteration * step;
            int end = start + step;
            var articles = IntStream.iterate(start, i -> i < end, i -> i + 1)
                    .peek(i -> LOGGER.info("Сгенерирована статья № {}", i))
                    .mapToObj((x) -> articleGenerator.generate(words))
                    .collect(Collectors.toList());
            articles.forEach(articleStore::save);
        }
    }
}
