package com.perfect.app.keyword.controller;

import com.perfect.service.KeywordQualityService;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

/**
 * Created by baizz on 2014-07-28.
 * 2014-12-9 refactor
 */
@RestController
@Scope("prototype")
@RequestMapping("/keywordQuality")
public class KeywordQualityController {

    @Resource
    private KeywordQualityService keywordQualityService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView findKeywordQuality(@RequestParam(value = "redisKey", required = false) String redisKey,
                                           @RequestParam(value = "fieldName", required = false, defaultValue = "impression") String fieldName,
                                           @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
                                           @RequestParam(value = "skip", required = false, defaultValue = "0") Integer skip,
                                           @RequestParam(value = "sort", required = false, defaultValue = "-1") Integer sort) {
        AbstractView jsonView = new MappingJackson2JsonView();
        Map<String, Object> values = keywordQualityService.find(redisKey, fieldName, limit, skip, sort);
        jsonView.setAttributesMap(values);
        return new ModelAndView(jsonView);
    }

    @RequestMapping(value = "/downloadCSV", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadQualityReportCSV(HttpServletResponse response,
                                         @RequestParam(value = "redisKey", required = false) String redisKey)
            throws IOException {
        String filename = UUID.randomUUID().toString().replace("-", "") + ".csv";
        response.addHeader("Content-Disposition", "attachment;filename=" + new String((filename).getBytes("UTF-8"), "ISO8859-1"));
        try (OutputStream os = response.getOutputStream()) {
            keywordQualityService.downloadQualityCSV(redisKey, os);
        }
    }
}
