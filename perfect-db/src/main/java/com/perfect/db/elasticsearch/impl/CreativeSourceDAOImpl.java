package com.perfect.db.elasticsearch.impl;

import com.perfect.dao.RegionalCodeDAO;
import com.perfect.dao.creative.CreativeSourceDAO;
import com.perfect.db.elasticsearch.base.BaseEsDaoImpl;
import com.perfect.dto.creative.CreativeSourceDTO;
import com.perfect.dto.creative.EsSearchResultDTO;
import com.perfect.utils.RegionalCodeUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by vbzer_000 on 2014/9/19.
 */
@Component
public class CreativeSourceDAOImpl extends BaseEsDaoImpl<CreativeSourceDTO, String> implements CreativeSourceDAO {

    @Resource
    private RegionalCodeDAO regionalCodeDAO;

    @Resource
    private Client esClient;

    private final String AGG_KEYWORDS = "keywords";
    private final String AGG_HOSTS = "hosts";
    private final String AGG_REGIONS = "regions";

    @Override
    public EsSearchResultDTO search(String query, int page, int size, int[] regions) {

        query = query.replaceAll("\n", " ");

        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(query);
        builder.analyzer("ik").field("title").field("body").defaultOperator(QueryStringQueryBuilder.Operator.OR);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        boolQueryBuilder.must(builder);
        if (regions.length > 0)
            boolQueryBuilder.should(QueryBuilders.termsQuery("region", regions));

        SearchResponse sr = esClient.prepareSearch("data").setTypes("creative").setQuery(boolQueryBuilder).setFrom(
                (page - 1) * size).setSize(size).addSort("_score", SortOrder.DESC)
                .setHighlighterRequireFieldMatch(false)
                .addHighlightedField("title").addHighlightedField("body").setHighlighterPreTags("<font color='red'>")
                .setHighlighterPostTags("</font>")
                .addAggregation(AggregationBuilders.terms("all").field("title").field("body").size(10))
                .addAggregation(AggregationBuilders.terms(AGG_KEYWORDS).field("keyword").size(10))
                .addAggregation(AggregationBuilders.terms(AGG_REGIONS).field("region").size(10))
                .addAggregation(AggregationBuilders.terms(AGG_HOSTS).field("host").size(10))
                .get();

        EsSearchResultDTO esSearchResultDTO = new EsSearchResultDTO();

        if (sr == null || sr.getHits().totalHits() == 0) {
            return esSearchResultDTO;
        }


        esSearchResultDTO.setTotal(sr.getHits().totalHits());
        Map<Integer, String> regionMap = new HashMap<>();

        List<CreativeSourceDTO> hitList = new ArrayList<>();
        for (SearchHit searchHit : sr.getHits()) {
            Map<String, Object> map = searchHit.getSource();
            CreativeSourceDTO creativeSourceEntity = new CreativeSourceDTO();
            for (Map.Entry<String, HighlightField> entry : searchHit.getHighlightFields().entrySet()) {
                HighlightField field = entry.getValue();
                map.put(field.name(), field.fragments()[0]);
            }


            try {
                BeanUtils.populate(creativeSourceEntity, map);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            Object regionObj = map.get("region");
            if (regionObj == null) {
                creativeSourceEntity.setRegion("无");
            } else {
                int region = Integer.parseInt(regionObj.toString());

                if (region < 100) {
                    // 获取省名称
                    String name = regionalCodeDAO.getProvinceNameById(region);
                    regionMap.put(region, name);
                } else {
                    // 获取市级名称
                    String name = regionalCodeDAO.getRegionNameById(region);
                    regionMap.put(region, name);
                }

                creativeSourceEntity.setRegion(regionMap.get(region));
            }
            hitList.add(creativeSourceEntity);
        }

        esSearchResultDTO.setList(hitList);

        BigDecimal total = BigDecimal.valueOf(sr.getHits().totalHits());
        for (Aggregation aggregation : sr.getAggregations()) {
            if (aggregation.getName().equals(AGG_KEYWORDS)) {
                Terms tr = (Terms) aggregation;
                for (Terms.Bucket bucket : tr.getBuckets()) {
                    esSearchResultDTO.addKeyword(bucket.getKey(), new BigDecimal(bucket.getDocCount()).divide(total, 4,
                            BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                }
            } else if (aggregation.getName().equals(AGG_HOSTS)) {
                Terms tr = (Terms) aggregation;
                for (Terms.Bucket bucket : tr.getBuckets()) {
                    esSearchResultDTO.addHost(bucket.getKey(), new BigDecimal(bucket.getDocCount()).divide(total, 4,
                            BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                }

            } else if (aggregation.getName().equals(AGG_REGIONS)) {
                Terms tr = (Terms) aggregation;
                for (Terms.Bucket bucket : tr.getBuckets()) {
                    int region = bucket.getKeyAsNumber().intValue();
                    String name = regionMap.get(region);
                    if (name == null) {
                        try {
                            regionMap.putAll(RegionalCodeUtils.regionalCode(Arrays.asList(region)));
                            name = regionMap.get(region);
                        } catch (NullPointerException e) {
                            name = "-";
                        }
                    }
                    esSearchResultDTO.addRegions(name, new BigDecimal(bucket.getDocCount()).divide(total, 4,
                            BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                }
            } else if (aggregation.getName().equals("all")) {
                Terms tr = (Terms) aggregation;
                for (Terms.Bucket bucket : tr.getBuckets()) {
                    esSearchResultDTO.addTerm(bucket.getKey(), new BigDecimal(bucket.getDocCount()).divide(total, 4,
                            BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)));
                }
            }
        }
        return esSearchResultDTO;
    }

    @Override
    public String getIndex() {
        return "creative";
    }

    @Override
    public String getType() {
        return "source";
    }

    @Override
    public String getId() {
        return "_id";
    }

    @Override
    public Class<String> getIdClass() {
        return String.class;
    }

    @Override
    public <E> Class<E> getEntityClass() {
        return null;
    }

    @Override
    public Class<CreativeSourceDTO> getDTOClass() {
        return CreativeSourceDTO.class;
    }
}
