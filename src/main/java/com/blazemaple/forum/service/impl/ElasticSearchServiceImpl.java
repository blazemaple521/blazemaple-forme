package com.blazemaple.forum.service.impl;

import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.es.DiscussPostRepository;
import com.blazemaple.forum.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/3 17:16
 */
@Service
@RequiredArgsConstructor
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private final  DiscussPostRepository discussPostRepository;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;



    @Override
    public void savaDiscussPost(DiscussPost discussPost) {
        DiscussPost save = discussPostRepository.save(discussPost);
        System.out.println(save);
    }

    @Override
    public void deleteDiscussPost(int postId) {
        discussPostRepository.deleteById(postId);
    }

    @Override
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
            .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
            .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
            .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
            .withPageable(PageRequest.of(current, limit))
            .withHighlightFields(
                new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
            )
            .build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class, IndexCoordinates.of("discusspost"));

            long totalHits = searchHits.getTotalHits();
        if (totalHits <= 0) {
            return Page.empty();
        }

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits.getSearchHits()) {
            DiscussPost post = hit.getContent();

            // 处理高亮显示的结果
            List<String> highlightedTitle = hit.getHighlightField("title");
            if (highlightedTitle != null) {
                post.setTitle(highlightedTitle.get(0));
            }

            List<String> highlightedContent = hit.getHighlightField("content");
            if (highlightedContent != null) {
                post.setContent(highlightedContent.get(0));
            }

            list.add(post);
        }

        return new PageImpl(list, PageRequest.of(current, limit), totalHits);
    }
}
