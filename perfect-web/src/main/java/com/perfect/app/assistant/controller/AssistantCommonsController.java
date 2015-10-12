package com.perfect.app.assistant.controller;

import com.perfect.autosdk.sms.v3.KeywordInfo;
import com.perfect.commons.web.WebContextSupport;
import com.perfect.dto.adgroup.AdgroupDTO;
import com.perfect.dto.campaign.CampaignDTO;
import com.perfect.dto.creative.CreativeDTO;
import com.perfect.dto.keyword.AssistantKeywordIgnoreDTO;
import com.perfect.dto.keyword.KeywordDTO;
import com.perfect.dto.keyword.KeywordInfoDTO;
import com.perfect.param.FindOrReplaceParam;
import com.perfect.service.AdgroupService;
import com.perfect.service.AssistantKeywordService;
import com.perfect.service.CampaignService;
import com.perfect.service.CreativeService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author xiaowei
 * @title AssistantCommonsController
 * @package com.perfect.app.assistant.controller
 * @description 业务公共类方法，包括各层级的文字替换，复制粘贴等业务功能
 * @update 2015年09月25日. 上午11:45
 */
@RestController
@Scope("prototype")
@RequestMapping(value = "/assistantCommons")
public class AssistantCommonsController extends WebContextSupport {


    @Resource
    private AssistantKeywordService assistantKeywordService;

    @Resource
    private CreativeService creativeService;

    @Resource
    private AdgroupService adgroupService;

    @Resource
    private CampaignService campaignService;

    private static Integer OBJ_SIZE = 18;

    @RequestMapping(value = "/checkSome", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView findOrReplace(@RequestBody FindOrReplaceParam forp) {
        switch (forp.getType()) {
            case "keyword":
                List<KeywordInfoDTO> findDto = keyWordFindOrReplace(forp);
                return writeMapObject(DATA, findDto);
            case "creative":
                List<CreativeDTO> creativeDTOs = creativeWordFindOrReplace(forp);
                return writeMapObject(DATA, creativeDTOs);
            case "adgroup":
                List<AdgroupDTO> adgroupDTOs = adgroudFindOreReplace(forp);
                setCampaignNameByLongId(adgroupDTOs);
                return writeMapObject(DATA, adgroupDTOs);
            case "campaign":
                System.out.println("campaign");
                return writeMapObject(DATA, null);
        }
        return writeMapObject(DATA, null);
    }

    /**
     * 文字替换参数对象
     *
     * @param forp
     * @return
     */

    //start keywordTextFindOrReplace
    private List<KeywordInfoDTO> keyWordFindOrReplace(final FindOrReplaceParam forp) {
        List<KeywordInfoDTO> returnResult = new ArrayList<>();
        if (forp.getForType() == 0) {
            String[] keywordIds = forp.getCheckData().split(",");
            List<String> kidStr = Arrays.asList(keywordIds);
            kidStr.stream().forEach(s -> {
                if (s.length() > OBJ_SIZE) {
                    KeywordInfoDTO dto = assistantKeywordService.findByInfoStrId(s);
                    switchCaseKeyword(forp, dto, returnResult);
                } else {
                    KeywordInfoDTO dto = assistantKeywordService.findByInfoLongId(Long.valueOf(s));
                    switchCaseKeyword(forp, dto, returnResult);
                }
            });
        } else {
            if (forp.getCampaignId() != null) {
                if (forp.getCampaignId().length() > OBJ_SIZE) {
                    List<KeywordInfoDTO> keywordInfoDTOLis = assistantKeywordService.getKeywordInfoByCampaignIdStr(forp.getCampaignId());
                    keywordInfoDTOLis.stream().forEach(s -> {
                        switchCaseKeyword(forp, s, returnResult);
                    });
                } else {
                    List<KeywordInfoDTO> keywordInfoDTOLis = assistantKeywordService.getKeywordInfoByCampaignIdLong(Long.valueOf(forp.getCampaignId()));
                    keywordInfoDTOLis.stream().forEach(s -> {
                        switchCaseKeyword(forp, s, returnResult);
                    });
                }
            }
        }
        return returnResult;
    }

    private void switchCaseKeyword(FindOrReplaceParam forp, KeywordInfoDTO dto, List<KeywordInfoDTO> returnResult) {
        switch (forp.getForPlace()) {
            case "keyword":
                KeywordInfoDTO keyword = getRuleData(forp, 1, dto);
                if (keyword != null)
                    returnResult.add(keyword);
                break;
            case "pcUrl":
                KeywordInfoDTO pcUrl = getRuleData(forp, 2, dto);
                if (pcUrl != null)
                    returnResult.add(pcUrl);
                break;
            case "mibUrl":
                KeywordInfoDTO mibUrl = getRuleData(forp, 3, dto);
                if (mibUrl != null)
                    returnResult.add(mibUrl);
                break;
            case "allUrl":
                KeywordInfoDTO allUrl = getRuleData(forp, 4, dto);
                if (allUrl != null)
                    returnResult.add(allUrl);
                break;
        }
    }

    private KeywordInfoDTO getRuleData(FindOrReplaceParam forp, Integer type, KeywordInfoDTO dto) {
        KeywordInfoDTO keywordDTO = null;
        if (forp.isfQcaseLowerAndUpper() || (!forp.isfQcaseLowerAndUpper() && !forp.isfQcaseAll() && !forp.isfQigonreTirm())) {//isfQcaseLowerAndUpper
            switch (type) {
                case 1:
                    if (dto.getObject().getKeyword().contains(forp.getFindText())) {
                        if (forp.getReplaceText() != null) {
                            dto.getObject().setKeyword(dto.getObject().getKeyword().replace(forp.getFindText(), forp.getReplaceText()));
                            KeywordDTO updateDTO = dto.getObject();
                            assistantKeywordService.updateKeyword(updateDTO);
                            return dto;
                        } else {
                            return dto;
                        }
                    }
                    break;
                case 2:
                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                            }
                        }
                    }
                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTOPc = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTOPc);
                            }
                        }
                    }
                    return dto;
            }
        }
        if (forp.isfQcaseAll()) {//isfQigonreTirm
            switch (type) {
                case 1:
                    if (dto.getObject().getKeyword().equals(forp.getFindText())) {
                        if (forp.getReplaceText() != null) {
                            dto.getObject().setKeyword(dto.getObject().getKeyword().replace(forp.getFindText(), forp.getReplaceText()));
                            KeywordDTO updateDTO = dto.getObject();
                            assistantKeywordService.updateKeyword(updateDTO);
                            return dto;
                        } else {
                            return dto;
                        }
                    }
                    break;
                case 2:
                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                            }
                        }
                    }
                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTOPc = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTOPc);
                            }
                        }
                    }
                    return dto;
            }
        }
        if (forp.isfQigonreTirm()) {//isfQcaseAll
            switch (type) {
                case 1:
                    if (dto.getObject().getKeyword().trim().contains(forp.getFindText())) {
                        if (forp.getReplaceText() != null) {
                            dto.getObject().setKeyword(dto.getObject().getKeyword().replace(forp.getFindText(), forp.getReplaceText()));
                            KeywordDTO updateDTO = dto.getObject();
                            assistantKeywordService.updateKeyword(updateDTO);
                            return dto;
                        } else {
                            return dto;
                        }
                    }
                    break;
                case 2:
                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getObject().getMobileDestinationUrl() != null) {
                        if (dto.getObject().getMobileDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setMobileDestinationUrl(dto.getObject().getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTO = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTO);
                                return dto;
                            }
                        }
                    }

                    if (dto.getObject().getPcDestinationUrl() != null) {
                        if (dto.getObject().getPcDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.getObject().setPcDestinationUrl(dto.getObject().getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                KeywordDTO updateDTOPc = dto.getObject();
                                assistantKeywordService.updateKeyword(updateDTOPc);
                            }
                        }
                    }
                    return dto;
            }
        }
        return keywordDTO;
    }
    //end keywordTextFindOrReplace


    //start creativeTextFindOrReplace

    private List<CreativeDTO> creativeWordFindOrReplace(final FindOrReplaceParam forp) {
        List<CreativeDTO> returnResult = new ArrayList<>();
        if (forp.getForType() == 0) {//操作选中
            String[] creativeIds = forp.getCheckData().split(",");
            List<String> cridStr = Arrays.asList(creativeIds);
            cridStr.stream().forEach(crid -> {//如果选中是本地添加的创意
                if (crid.length() > OBJ_SIZE) {
                    CreativeDTO creativeDTO = creativeService.findByObjId(crid);
                    switchCaseCreative(forp, creativeDTO, returnResult);
                } else {
                    CreativeDTO creativeDTO = creativeService.findOne(Long.valueOf(crid));
                    switchCaseCreative(forp, creativeDTO, returnResult);
                }
            });
        } else {//操作整个计划下的创意
            if (forp.getCampaignId().length() > OBJ_SIZE) {
                List<CreativeDTO> creativeDTOs = creativeService.getByCampaignIdStr(forp.getCampaignId());
                creativeDTOs.stream().forEach(s -> {
                    switchCaseCreative(forp, s, returnResult);
                });
            } else {
                List<CreativeDTO> creativeDTOs = creativeService.getByCampaignIdLong(Long.valueOf(forp.getCampaignId()));
                creativeDTOs.stream().forEach(s -> {
                    switchCaseCreative(forp, s, returnResult);
                });
            }
        }
        return returnResult;
    }

    private void switchCaseCreative(FindOrReplaceParam forp, CreativeDTO dto, List<CreativeDTO> returnResult) {
        switch (forp.getForPlace()) {
            case "cTitle":
                CreativeDTO creativeTitle = getRuleData(forp, 1, dto);
                if (creativeTitle != null)
                    returnResult.add(creativeTitle);
                break;
            case "cDesc1":
                CreativeDTO creativeDesc1 = getRuleData(forp, 2, dto);
                if (creativeDesc1 != null)
                    returnResult.add(creativeDesc1);
                break;
            case "cDesc2":
                CreativeDTO creativeDesc2 = getRuleData(forp, 3, dto);
                if (creativeDesc2 != null)
                    returnResult.add(creativeDesc2);
                break;
            case "titleAndDesc":
                CreativeDTO creativeTitleAndDesc = getRuleData(forp, 4, dto);
                if (creativeTitleAndDesc != null)
                    returnResult.add(creativeTitleAndDesc);
                break;
            case "pcUrl":
                CreativeDTO creativePcUrl = getRuleData(forp, 5, dto);
                if (creativePcUrl != null)
                    returnResult.add(creativePcUrl);
                break;
            case "pcsUrl":
                CreativeDTO creativePcsUrl = getRuleData(forp, 6, dto);
                if (creativePcsUrl != null)
                    returnResult.add(creativePcsUrl);
                break;
            case "pcAllUrl":
                CreativeDTO creativePcsAllUrl = getRuleData(forp, 7, dto);
                if (creativePcsAllUrl != null)
                    returnResult.add(creativePcsAllUrl);
                break;
            case "mibUrl":
                CreativeDTO creativeMibUrl = getRuleData(forp, 8, dto);
                if (creativeMibUrl != null)
                    returnResult.add(creativeMibUrl);
                break;
            case "mibsUrl":
                CreativeDTO creativeMibsUrl = getRuleData(forp, 9, dto);
                if (creativeMibsUrl != null)
                    returnResult.add(creativeMibsUrl);
                break;
            case "mibAllUrl":
                CreativeDTO creativemMibAllUrl = getRuleData(forp, 10, dto);
                if (creativemMibAllUrl != null)
                    returnResult.add(creativemMibAllUrl);
                break;
            case "AllUrl":
                CreativeDTO creativeAllUrl = getRuleData(forp, 11, dto);
                if (creativeAllUrl != null)
                    returnResult.add(creativeAllUrl);
                break;
        }

    }

    private CreativeDTO getRuleData(FindOrReplaceParam forp, Integer type, CreativeDTO dto) {
        CreativeDTO creativeDTO = null;
        if (forp.isfQcaseLowerAndUpper() || (!forp.isfQcaseLowerAndUpper() && !forp.isfQcaseAll() && !forp.isfQigonreTirm())) {//isfQcaseLowerAndUpper
            switch (type) {
                case 1:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 2:
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 5:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 6:
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 7:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 8:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 9:
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 10:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 11:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
            }
        }

        if (forp.isfQcaseAll()) {
            switch (type) {
                case 1:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 2:
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 5:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 6:
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 7:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 8:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 9:
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 10:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 11:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
            }
        }

        if (forp.isfQigonreTirm()) {
            switch (type) {
                case 1:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 2:
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 3:
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 4:
                    if (dto.getTitle() != null) {
                        if (dto.getTitle().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setTitle(dto.getTitle().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription1() != null) {
                        if (dto.getDescription1().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription1(dto.getDescription1().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getDescription2() != null) {
                        if (dto.getDescription2().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setDescription2(dto.getDescription2().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 5:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 6:
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 7:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    break;
                case 8:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 9:
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
                case 10:
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
                case 11:
                    if (dto.getPcDestinationUrl() != null) {
                        if (dto.getPcDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDestinationUrl(dto.getPcDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getPcDisplayUrl() != null) {
                        if (dto.getPcDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setPcDisplayUrl(dto.getPcDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDestinationUrl() != null) {
                        if (dto.getMobileDestinationUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDestinationUrl(dto.getMobileDestinationUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    if (dto.getMobileDisplayUrl() != null) {
                        if (dto.getMobileDisplayUrl().trim().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setMobileDisplayUrl(dto.getMobileDisplayUrl().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                creativeService.updateCreative(dto);
                            }
                        }
                    }
                    return dto;
            }
        }

        return creativeDTO;
    }

    //end creativeTextFindOrReplace


    //start adgroupTextFindOrReplace

    private List<AdgroupDTO> adgroudFindOreReplace(final FindOrReplaceParam forp) {
        List<AdgroupDTO> returnResult = new ArrayList<>();
        if (forp.getForType() == 0) {
            String[] ids = forp.getCheckData().split(",");
            List<String> strIds = Arrays.asList(ids);
            strIds.stream().forEach(s -> {
                if (s.length() > OBJ_SIZE) {
                    AdgroupDTO adgroupDTO = adgroupService.findByObjId(s);
                    switchCaseAdgroup(forp, adgroupDTO, returnResult);
                } else {
                    AdgroupDTO adgroupDTO = adgroupService.findOne(Long.valueOf(s));
                    switchCaseAdgroup(forp, adgroupDTO, returnResult);
                }
            });
        } else {
            if (forp.getCampaignId().length() > OBJ_SIZE) {
                List<AdgroupDTO> adgroupDTOs = adgroupService.getAdgroupByCampaignObjId(forp.getCampaignId());
                adgroupDTOs.stream().forEach(s -> {
                    switchCaseAdgroup(forp, s, returnResult);
                });
            } else {
                List<AdgroupDTO> adgroupDTOs = adgroupService.getAdgroupByCampaignId(Long.valueOf(forp.getCampaignId()));
                adgroupDTOs.stream().forEach(s -> {
                    switchCaseAdgroup(forp, s, returnResult);
                });
            }
        }

        return returnResult;
    }

    private void switchCaseAdgroup(FindOrReplaceParam forp, AdgroupDTO dto, List<AdgroupDTO> returnResult) {
        if (forp.getForPlace().equals("adgroupName")) {
            AdgroupDTO creativeTitle = getRuleData(forp, 1, dto);
            if (creativeTitle != null)
                returnResult.add(creativeTitle);
        }
    }

    private AdgroupDTO getRuleData(FindOrReplaceParam forp, Integer type, AdgroupDTO dto) {
        AdgroupDTO adgroupDTO=null;
        if (forp.isfQcaseLowerAndUpper() || (!forp.isfQcaseLowerAndUpper() && !forp.isfQcaseAll() && !forp.isfQigonreTirm())) {//isfQcaseLowerAndUpper
            switch (type) {
                case 1:
                    if (dto.getAdgroupName() != null) {
                        if (dto.getAdgroupName().contains(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setAdgroupName(dto.getAdgroupName().replace(forp.getFindText(), forp.getReplaceText()));
                                adgroupService.updateAdgroup(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
            }
        }

        if (forp.isfQcaseAll()) {
            switch (type) {
                case 1:
                    if (dto.getAdgroupName() != null) {
                        if (dto.getAdgroupName().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setAdgroupName(dto.getAdgroupName().replace(forp.getFindText(), forp.getReplaceText()));
                                adgroupService.updateAdgroup(dto);
                                return dto;
                            } else {
                                return dto;
                            }
                        }
                    }
                    break;
            }
        }

        if (forp.isfQigonreTirm()) {
            switch (type) {
                case 1:
                    if (dto.getAdgroupName() != null) {
                        if (dto.getAdgroupName().trim().equals(forp.getFindText())) {
                            if (forp.getReplaceText() != null) {
                                dto.setAdgroupName(dto.getAdgroupName().trim().replace(forp.getFindText(), forp.getReplaceText()));
                                adgroupService.updateAdgroup(dto);
                                return dto;
                            }
                        }
                    }
                    break;
            }
        }

        return adgroupDTO;
    }



















    private void setCampaignNameByLongId(List<AdgroupDTO> list) {
        if (list.size() > 0) {
            List<CampaignDTO> campaignEntity = (List<CampaignDTO>) campaignService.findAll();
            for (int i = 0; i < campaignEntity.size(); i++) {
                for (AdgroupDTO a : list) {
                    if (a.getCampaignId() != null) {
                        if (a.getCampaignId().equals(campaignEntity.get(i).getCampaignId())) {
                            a.setCampaignName(campaignEntity.get(i).getCampaignName());
                        }
                    } else {
                        if (a.getCampaignObjId().equals(campaignEntity.get(i).getId())) {
                            a.setCampaignName(campaignEntity.get(i).getCampaignName());
                        }
                    }

                }
            }
        }
    }
}