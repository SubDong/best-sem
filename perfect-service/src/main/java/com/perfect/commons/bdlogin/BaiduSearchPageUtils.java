package com.perfect.commons.bdlogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.perfect.commons.bdpreview.CmdHandler;
import com.perfect.dto.creative.CreativeInfoDTO;
import com.perfect.dto.creative.SublinkInfoDTO;
import com.perfect.utils.json.JSONUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by baizz on 2014-11-10.
 * 2014-12-16 refactor
 */
public class BaiduSearchPageUtils implements BaiduHttp {

    public static String getBaiduSearchPage(String curl, String keyword, int area) {
        try {
            keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
            curl = curl.replace("%KEYWORD%", keyword).replace("%AREA_ID%", area + "");
            String fileName = CmdHandler.createShell(curl);
            String html = CmdHandler.executeShell(fileName);
            CmdHandler.deleteTempFile(fileName);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getBaiduSearchPage(String cookies, String castk, String keyword, int area) {
        try {
            JsonNode jsonNode = JSONUtils.getMapper().readTree(cookies).get("cookies");
            Map<String, String> cookieMap = new HashMap<>();
            for (Iterator<JsonNode> nodeIterator = jsonNode.elements(); nodeIterator.hasNext(); ) {
                JsonNode node = nodeIterator.next();
                String name = node.get("name").asText();
                if (COOKIE_SET.contains(name))
                    continue;
                cookieMap.put(name.toUpperCase(), node.get("value").asText());
            }
            cookieMap.put("CASTK", castk);

            keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
            String curl = CmdHandler.createCurl(cookieMap, keyword, area);
            String fileName = CmdHandler.createShell(curl);
            String html = CmdHandler.executeShell(fileName);
            CmdHandler.deleteTempFile(fileName);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

//        CookieStore cookieStore = new BasicCookieStore();
//        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            JsonNode jsonNode = JSONUtils.getMapper().readTree(cookies).get("cookies");
//            jsonNode.elements().forEachRemaining(node -> {
//                BasicClientCookie cookie = new BasicClientCookie(node.get("name").asText(), node.get("value").asText());
//                cookie.setVersion(node.get("version").asInt());
//                cookie.setDomain(node.get("domain").asText());
//                cookie.setPath(node.get("path").asText());
//                cookie.setSecure(node.get("secure").asBoolean());
//                if (node.get("expiryDate") != null) {
//                    try {
//                        cookie.setExpiryDate(sdf.parse(node.get("expiryDate").asText()));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//                cookieStore.addCookie(cookie);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        StringBuilder _cookies = new StringBuilder("");
//        String userid = "";
//        String token = "";
//
//        for (Cookie cookie : cookieStore.getCookies()) {
//            if (COOKIE_SET.contains(cookie.getName())) {
//                continue;
//            }
//
//            if (__cas__st__3.equals(cookie.getName())) {
//                token = cookie.getValue();
//            } else if (__cas__id__3.equals(cookie.getName())) {
//                userid = cookie.getValue();
//            }
//
//            _cookies.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
//        }
//        _cookies = _cookies.delete(_cookies.length() - 2, _cookies.length());
//
//        HttpPost httpPost = new HttpPost(PREVIEW_URL);
//        httpPost.addHeader("Host", PREVIEW_HOST);
//        httpPost.addHeader("Cookie", _cookies.toString());
//        httpPost.addHeader("Content-Type", CONTENT_TYPE);
//
//        List<NameValuePair> postData = new ArrayList<>();
//        postData.add(new BasicNameValuePair("params", "{\"device\":1,\"keyword\":\"" + keyword + "\",\"area\":" + area + ",\"pageNo\":0}"));
//        postData.add(new BasicNameValuePair("userid", userid));
//        postData.add(new BasicNameValuePair("token", token));
//        httpPost.setEntity(new UrlEncodedFormEntity(postData, StandardCharsets.UTF_8));
//        BaiduHttp.headerWrap(httpPost);
//
//
//        try (CloseableHttpClient httpClient = BaiduHttpConnFactory.build(); CloseableHttpResponse response = httpClient.execute(httpPost)) {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            response.getEntity().writeTo(outputStream);
//            return new String(outputStream.toByteArray());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
    }

    public static int where(String html, String expectedHost) {
        Document doc = Jsoup.parse(html);

        List<CreativeInfoDTO> leftCreativeVOList = new ArrayList<>();
        List<CreativeInfoDTO> rightCreativeVOList = new ArrayList<>();

        handleLeft(doc, leftCreativeVOList);
        for (CreativeInfoDTO creativeInfoDTO : leftCreativeVOList) {
            if (creativeInfoDTO.getUrl().equals(expectedHost)) {
                return leftCreativeVOList.indexOf(creativeInfoDTO);
            }
        }

        handleRight(doc, rightCreativeVOList);

        for (CreativeInfoDTO creativeInfoDTO : rightCreativeVOList) {
            if (creativeInfoDTO.getUrl().equals(expectedHost)) {
                return -1 * leftCreativeVOList.indexOf(creativeInfoDTO);
            }
        }

        return 0;
    }


    private static void handleLeft(Document doc, final List<CreativeInfoDTO> leftCreativeVOList) {
        LinkedList<CreativeInfoDTO> creativeInfoDTOList = new LinkedList<>();

        //获取左侧推广数据
        if (doc.select("#content_left > table").isEmpty()) {
            //div
            // ec_title
            Elements elements = doc.select("#content_left > .Ec_result");
            for (Element element : elements) {
                if (element.attr("id").startsWith("5"))
                    continue;

                CreativeInfoDTO creativeInfoDTO = new CreativeInfoDTO();
                creativeInfoDTO.setDescSource(element.html());
                creativeInfoDTO.setTitle(element.select(".ec_title").text());

                creativeInfoDTO.setTitle(element.select(".ec_title").text());
                if (element.select(".ec_desc .EC_pap_big_desc").size() > 0) {
                    creativeInfoDTO.setDescription(element.select(".ec_desc .EC_pap_big_desc").text());
                    creativeInfoDTO.setUrl(element.select(".ec_desc .ec_meta .ec_url").text());
                    creativeInfoDTO.setTime(element.select(".ec_desc .ec_meta .ec_date").text());
                    Elements children = element.select(".ec_desc .EC_pap_big_paxj a");
                    if (children != null) {
                        List<SublinkInfoDTO> sublinkInfos = new ArrayList<>();
                        ListIterator<Element> iterator = children.listIterator();
                        while (iterator.hasNext()) {
                            SublinkInfoDTO sublinkInfo = new SublinkInfoDTO();
                            sublinkInfo.setDescription(iterator.next().text());
                            sublinkInfos.add(sublinkInfo);
                        }
                        creativeInfoDTO.setSublinkInfoDTOs(sublinkInfos);
                    }
                } else {
                    creativeInfoDTO.setDescription(element.select(".ec_desc").text());
                    creativeInfoDTO.setUrl(element.select(".ec_meta .ec_url").text());
                    creativeInfoDTO.setTime(element.select(".ec_meta .ec_date").text());
                }
                creativeInfoDTOList.addLast(creativeInfoDTO);

            }

        } else {
            Elements tables = doc.select("#content_left table");
            for (Element table : tables) {
                if (!table.className().startsWith(" ")) {
                    continue;
                }
                CreativeInfoDTO creativeInfoDTO = new CreativeInfoDTO();
                creativeInfoDTO.setDescSource(table.html());
                creativeInfoDTO.setTitle(table.select(".EC_title").text());

                Elements descs = table.select(".EC_body");
                if (descs.isEmpty()) {
                    creativeInfoDTO.setDescription(table.select(".EC_pap_big_zpdes").text());

                    // 处理XJ
//                    Elements xjs = table.select(".EC_pap_big_xj");
                } else {
                    creativeInfoDTO.setDescription(descs.text());
                }

                Elements sublinks = table.select(".EC_xj_underline a");
                if (!sublinks.isEmpty()) {
                    ListIterator<Element> children = sublinks.listIterator();
                    List<SublinkInfoDTO> sublinkInfos = new ArrayList<>();
                    while (children.hasNext()) {
                        SublinkInfoDTO sublinkInfo = new SublinkInfoDTO();
                        sublinkInfo.setDescription(children.next().text());
                        sublinkInfos.add(sublinkInfo);
                    }
                    creativeInfoDTO.setSublinkInfoDTOs(sublinkInfos);
                }

                creativeInfoDTO.setUrl(table.select(".EC_url").text());

                creativeInfoDTOList.addLast(creativeInfoDTO);
            }
        }
        leftCreativeVOList.addAll(creativeInfoDTOList);
    }

    private static void handleRight(Document doc, final List<CreativeInfoDTO> rightCreativeVOList) {
        Elements div_right = null;
        boolean _temp2 = (doc.getElementById("ec_im_container") != null) && (doc.getElementById("ec_im_container").children().size() > 0);

        if (_temp2) {
            div_right = doc.getElementById("ec_im_container").children();
        }

        //过滤出右侧的推广信息
        if (_temp2) {
            for (int j = div_right.size() - 1; j >= 0; j--) {
                if (!div_right.get(j).hasAttr("id")) {
                    div_right.remove(j);
                }
            }
        }

        //获取右侧推广数据
        if (_temp2 && div_right.size() > 0) {
            CreativeInfoDTO creativeVO;
            for (Element e : div_right) {
                String _title = e.select("a").first().text();
                String _description = e.select("a").get(1).text();
                String _url = e.select("a").get(1).select("font").last().text();
                _description.replace(_url, "");

                creativeVO = new CreativeInfoDTO();
                creativeVO.setDescSource(e.html());
                creativeVO.setTitle(_title);
                creativeVO.setDescription(_description);
                creativeVO.setUrl(_url);
                rightCreativeVOList.add(creativeVO);
            }
        }
    }

}
