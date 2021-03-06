//tbodyClick5

//子窗口拖动初始化
window.onload = function () {
    rDrag.init(document.getElementById('setFdKeywordDiv'));
    rDrag.init(document.getElementById('setExcludeIpDiv'));
    rDrag.init(document.getElementById('setExtensionDiv'));
    rDrag.init(document.getElementById('setScheduleDiv'));
    rDrag.init(document.getElementById('plan2'));
}

/**
 * 得到所有推广计划
 */
function getCampaignList(nowPage) {
    pageType = 2;

    $("#tbodyClick5").empty();
    $("#tbodyClick5").html("加载中...");

    if (/^\d+$/.test(nowPage) == false) {
        nowPage = 0;
    }

    var param = getNowChooseCidAndAid();
    if (param == null) {
        param = {};
    }
    param["nowPage"] = nowPage;
    param["pageSize"] = items_per_page;
    $.ajax({
        url: "/assistantCampaign/list",
        type: "post",
        data: param,
        dataType: "json",
        success: function (data) {
            $("#tbodyClick5").empty();

            records = data.totalCount;
            pageIndex = data.nextPage;
            $("#pagination_campaignPage").pagination(records, getOptionsFromForm(pageIndex));


            if (data.list.length == 0) {
                $("#tbodyClick5").html("暂无数据!");
                return;
            }
            for (var i = 0; i < data.list.length; i++) {
                var html = campaignDataToHtml(data.list[i], i);
                $("#tbodyClick5").append(html);
                if (i == 0) {
                    setCampaignValue(".firstCampaign", data.list[i].campaignId);
                    if (data.list[i].localStatus != null) {
                        $("#reduction_caipamgin").find("span").removeClass("z_function_hover");
                        $("#reduction_caipamgin").find("span").addClass("zs_top");
                    } else {
                        $("#reduction_caipamgin").find("span").removeClass("zs_top");
                        $("#reduction_caipamgin").find("span").addClass("z_function_hover");
                    }
                }
            }
        }
    });
}


/**
 * 关键词Go按钮的单击事件
 */
function skipCampaignPage() {
    var pageNo = $("#campaignPageNum").val();
    getCampaignList(/^\d+$/.test(pageNo) == false ? 0 : parseInt(pageNo) - 1);
}
/**
 * 单击某一行时将改行的数据放入文本框内
 */
$("#tbodyClick5").delegate("tr", "click", function () {
    var span = $(this).find("td:last");
    $("#planBottom").fadeIn("slow");
    if (span.html() != "&nbsp;") {
        $("#reduction_caipamgin").find("span").removeClass("z_function_hover");
        $("#reduction_caipamgin").find("span").addClass("zs_top");
    } else {
        $("#reduction_caipamgin").find("span").removeClass("zs_top");
        $("#reduction_caipamgin").find("span").addClass("z_function_hover");
    }

    var obj = $(this);
    var campaignId = $(this).find("input[type=hidden]").val();
    setCampaignValue(this, campaignId);
});

/**
 * 将一条推广计划数据添加到html
 * @param obj 传入的单个推广计划对象
 * @param index 传入对象的下标
 */
function campaignDataToHtml(obj, index) {
    var html = "";

    if (obj.campaignId == null) {
        obj.campaignId = obj.id;
    }

    if (index == 0) {
        html = html + "<tr class='list2_box3 firstCampaign'>";
    } else if (index % 2 != 0) {
        html = html + "<tr class='list2_box2'>";
    } else {
        html = html + "<tr class='list2_box1'>";
    }

    html = html + "<input type='hidden' value = " + obj.campaignId + " />";

    html = html + "<td><input type='checkbox' name='campaignCheck' value='" + obj.campaignId + "' onchange='campListCheck()'/></td>";
    html = html + "<td>" + obj.campaignName + "</td>";

    switch (obj.status) {
        case 21:
            html = html + "<td>有效</td>";
            break;
        case 22:
            html = html + "<td>处于暂停时段</td>";
            break;
        case 23:
            html = html + "<td>暂停推广</td>";
            break;
        case 24:
            html = html + "<td>推广计划预算不足</td>";
            break;
        case 25:
            html = html + "<td>账户预算不足</td>";
            break;
        default :
            html = html + "<td>本地新增</td>";
    }


    html = html + until.convert(obj.pause == false, "<td>暂停</td>:" + "<td>启用</td>")
    html = html + until.convert(obj.budget == null, "<td><不限定></td>:" + "<td>" + obj.budget + "</td>")
    html = html + until.convert(obj.showProb == 1, "<td>优选</td>:" + "<td>轮显</td>")
    html = html + until.convert(obj.isDynamicCreative == true, "<td>开启</td>:" + "<td>关闭</td>");
    html = html + until.convert(obj.schedule == "" || obj.schedule == null, "<td>全部</td>:" + "<td>已设置</td>");


    //推广地域
    html = html + until.convert(obj.regionTarget == "" || obj.regionTarget == null, "<td>账户推广地域</td>" + ":" + "<td>计划推广地域</td>");

    var fd = obj.negativeWords != null ? obj.negativeWords.length : 0;
    var jqfd = obj.exactNegativeWords != null ? obj.exactNegativeWords.length : 0;
    html = html + until.convert(fd == 0 && jqfd == 0, "<td>未设置</td>:" + "<td>" + fd + "：" + jqfd + "</td>");

    html = html + "<td>" + (obj.excludeIp != null ? obj.excludeIp.length : 0) + "</td>";
    html = html + (obj.budgetOfflineTime != null ? "<td>" + obj.budgetOfflineTime.length + "</td>" : "<td>-</td>");
    html = html + "<input type='hidden' value=" + obj.priceRatio + " class='hidden'/>";
    if (obj.localStatus != null) {
        if (obj.localStatus == 3) {
            html = html + "<td><span class='error' step='3'></span></td>";
        } else {
            html = html + "<td><span class='pen' step='" + obj.localStatus + "'></span></td>";
        }
    } else {
        html = html + "<td>&nbsp;</td>";
    }
    html = html + "</tr>";

    return html;
}


/**
 *单击某一行的时候设置文本框值
 */
function setCampaignValue(obj, campaignId) {
    $("#hiddenCampaignId").val(campaignId);
    var _tr = $(obj);
    $(".campaignName_5").val(_tr.find("td:eq(1)").html());

    if (/^[0-9]+|[0-9]+.[0-9]*$/.test(_tr.find("td:eq(4)").html())) {
        $(".budget_5").val(_tr.find("td:eq(4)").html());
    } else {
        $(".budget_5").val("<不限定>");
    }

    $(".priceRatio_5").val(_tr.find(".hidden").val());
    $(".schedule_5").html("<a>" + _tr.find("td:eq(7)").html() + "</a>");
    $(".regionTarget_5").html("<a>" + _tr.find("td:eq(8)").html() + "</a>");
    $(".isDynamicCreative_5").html("<a>" + _tr.find("td:eq(6)").html() + "</a>");
    $(".negativeWords_5").html(until.convert(_tr.find("td:eq(9)").html() != "未设置", "<a>已设置(" + _tr.find("td:eq(9)").html() + ")</a>:<a>未设置</a>"));
    $(".excluedIp_5").html(until.convert(_tr.find("td:eq(10)").html() == "0", "<a>未设置</a>" + ":" + "<a>已设置(" + _tr.find("td:eq(10)").html() + ")</a>"));

    if (_tr.find("td:eq(5)").html() == "优选") {
        $(".selectShowProb_5").html("<option value = '1' selected='selected'>优选</option><option value='2'>轮显</option>");
    } else {
        $(".selectShowProb_5").html("<option value = '1' >优选</option><option value='2' selected='selected'>轮显</option>");
    }

    $(".status_5").html(_tr.find("td:eq(2)").html());

    if (_tr.find("td:eq(11)").html() == "-") {
        //达到预算下线
        $(".budgetOfflineTime_5").html("0次");
    } else {
        //达到预算下线
        $(".budgetOfflineTime_5").html(_tr.find("td:eq(11)").html() + "次");
    }

    //推广计划状态
    if (_tr.find("td:eq(3)").html() == "启用") {
        $(".selectPause_5").html("<option value = 'true' selected='selected'>启用</option><option value='false'>暂停</option>");
    } else {
        $(".selectPause_5").html("<option value = 'true' >启用</option><option value='false' selected='selected'>暂停</option>");
    }
}


/**
 *  发送编辑推广计划信息请求
 */
function editCampaignInfo(jsonData) {
    jsonData["cid"] = $("#hiddenCampaignId").val();
    $.ajax({
        url: "/assistantCampaign/edit",
        type: "post",
        data: jsonData,
        dataType: "json",
        success: function (data) {
            var html = campaignDataToHtml(data, 0);
            var tr = $("#tbodyClick5").find(".list2_box3");
            tr.replaceWith(html);
            setCampaignValue(html, data.campaignId);
            loadTree();
        }
    });
}

/**
 * update
 * @param num
 * @param value
 */
function whenBlurEditCampaign(num, value) {
    if ($("#tbodyClick5").find("tr").length == 0) {
        return;
    }

    var jsonData = {};
    switch (num) {
        case 1:
            if (value != "") {
                if (parseInt(getChar(value)) > 30) {
                    //alert("推广计划名不能超过30个字符，汉字占两个字符");
                    AlertPrompt.show("推广计划名不能超过30个字符，汉字占两个字符！");
                    return;
                } else {
                    jsonData["campaignName"] = value;
                }
            } else {
                return;
            }
            break;
        case 2:
            if (value != "<不限定>") {
                if (!/^-?\d+\.?\d*$/.test(value)) {
                    //alert("输入正确的每日预算");
                    AlertPrompt.show("输入正确的每日预算！");
                    return;
                } else {
                    if (parseFloat(value).toFixed(3) < 50.0) {
                        //alert("每日预算必须大于50RMB");
                        AlertPrompt.show("每日预算必须大于50RMB");
                        return;
                    } else {
                        var bgt = $("#acBgt").html();
                        if (bgt != "") {
                            if (parseInt(value) > parseInt(bgt)) {
                                //alert("您的每日预算为" + bgt + "，该计划的每日预算不能超过" + bgt + "元");
                                AlertPrompt.show("您的每日预算为" + bgt + "，该计划的每日预算不能超过" + bgt + "元");
                                return;
                            } else {
                                jsonData["budget"] = value;
                            }
                        } else {
                            //alert("您的账户预算加载失败，请加载成功后再进行修改");
                            AlertPrompt.show("您的账户预算加载失败，请加载成功后再进行修改");
                            return;
                        }
                    }
                }
            } else {
                jsonData["budget"] = null;
            }

            break;
        case 3:
            if (value != "") {
                if (!/^-?\d+\.?\d*$/.test(value)) {
                    //alert("输入正确的移动出价比例后再修改！");
                    AlertPrompt.show("输入正确的移动出价比例后再修改");
                    return;
                } else {
                    jsonData["priceRatio"] = value;
                }
            }

            break;
        case 4:
            jsonData["schedule"] = JSON.stringify(value);
            break;
        case 5:
            jsonData["regionTarget"] = value;
            break;
        case 6:
            jsonData["isDynamicCreative"] = value;
            break;
        case 7:
            var words = value.split("\t");
            jsonData["negativeWords"] = words[0];
            jsonData["exactNegativeWords"] = words[1];
            break;
        case 9:
            jsonData["excludeIp"] = value;
            break;
        case 10:
            jsonData["showProb"] = value;
            break;
        case 11:
            jsonData["pause"] = value;
            break;
    }
    editCampaignInfo(jsonData);
}

/**
 * 删除推广计划
 */
function deleteCampaign() {
    var cids = "";

    $("#tbodyClick5").find(".list2_box3").each(function () {
        cids += $(this).find("input[type=hidden]").val() + ",";
    });
    if (cids == "") {
        //alert("请选择行再操作!");
        AlertPrompt.show("请选择行再操作!");
        return;
    }

    var isDel = window.confirm("您确定要删除推广计划吗?");
    if (isDel == false) {
        return;
    }
    $.ajax({
        url: "/assistantCampaign/delete",
        type: "post",
        data: {"cid": cids},
        success: function (data) {
            $("#tbodyClick5").find(".list2_box3 td:last").html("<span class='error' step='3'></span>");
            loadTree();
        }
    });
}


/**
 * 否定关键词设置确定 单击事件
 */
var negativeWordsValue = "";
var exactNegativeWordsValue = "";
$(".ntwOk").click(function () {
    var negativeWords = $("#ntwTextarea").val();
    var exactNegativeWords = $("#entwTextarea").val();

    if (validateKeyword(negativeWords) == false) {
        return;
    }
    if (validateKeyword(exactNegativeWords) == false) {
        return;
    }

    if (windowName == "negativeWords_5") {
        whenBlurEditCampaign(7, negativeWords + "\t" + exactNegativeWords);
    } else if (windowName == "inputNegativeWords_add") {
        negativeWordsValue = negativeWords;
        exactNegativeWordsValue = exactNegativeWords;
    }
    $(".TB_overlayBG").css({display: "none"});
    $("#setNegtiveWord").hide(0);
});

/*验证输入的否定关键词合法性*/
function validateKeyword(keywords) {
    var kwds = keywords.split("\n");
    if (kwds.length > 200) {
        //alert("设置的否定关键词最大不能超过200个!");
        AlertPrompt.show("设置的否定关键词最大不能超过200个!");
        return false;
    }
    for (var i = 0; i < kwds.length; i++) {
        var len = kwds[i].replace(/[^\x00-\xff]/g, 'xx').length;
        if (len > 40) {//否定关键词最大为40字节
            //alert("关键词字节数最大不能超过40个字节。\n" + kwds[i]);
            AlertPrompt.show("关键词字节数最大不能超过40个字节。\n" + kwds[i]);
            return false;
        }
    }
    return true;
}

//设置否定关键词计数
function ntwTextareaCount() {
    var negativeWords = $("#ntwTextarea").val();
    var kwds = negativeWords.split("\n");
    $("#ntwCount").html("(" + (kwds[0] == "" ? 0 : kwds.length) + "/200)");
}

//设置精确否定关键词计数
function entwTextareaCount() {
    var exactNegativeWords = $("#entwTextarea").val();
    var kwds = exactNegativeWords.split("\n");
    $("#entwCount").html("(" + (kwds[0] == "" ? 0 : kwds.length) + "/200)");
}


//单击推广计划中的否定关键词设置的事件
var negativeWordsString = "";
$(".negativeWords_5").click(function () {
    $("#ntwTextarea").val("");
    $("#entwTextarea").val("");

    windowName = "negativeWords_5"
    var cid = $("#hiddenCampaignId").val()
    $.ajax({
        url: "/assistantCampaign/getObject",
        type: "post",
        data: {"cid": cid},
        dataType: "json",
        success: function (data) {
            setNegativeWordsToTextArea(data);
            ntwTextareaCount();
            entwTextareaCount();
        }
    });
    setDialogCss("setNegtiveWord");
});


/**
 * 添加推广计划中的设置否定关键词
 */
$(".inputNegativeWords_add").click(function () {
    windowName = "inputNegativeWords_add";

    var negativeWordsArray = new Array();
    var exactNegativeWordsArray = new Array();
    var json = {};
    if (negativeWordsValue != "") {
        negativeWordsArray = negativeWordsValue.split("\n");
    }
    if (exactNegativeWordsValue != "") {
        exactNegativeWordsArray = exactNegativeWordsValue.split("\n");
    }
    json["negativeWords"] = negativeWordsArray;
    json["exactNegativeWords"] = exactNegativeWordsArray;

    setNegativeWordsToTextArea(json);
    setDialogCss("setNegtiveWord");
});

/**
 * 将传入的data放入多行文本框中
 * @param data
 */
function setNegativeWordsToTextArea(data) {
    var negativeWords = data.negativeWords == undefined ? new Array() : data.negativeWords;
    var exactNegativeWords = data.exactNegativeWords == undefined ? new Array() : data.exactNegativeWords;
    var ntwcontent = "";
    var exntcontent = "";

    for (var i = 0; i < negativeWords.length; i++) {
        if (i < negativeWords.length - 1) {
            ntwcontent = ntwcontent + negativeWords[i] + "\r";
        } else {
            ntwcontent = ntwcontent + negativeWords[i];
        }
    }
    for (var i = 0; i < exactNegativeWords.length; i++) {
        if (i < exactNegativeWords.length - 1) {
            exntcontent = exntcontent + exactNegativeWords[i] + "\r";
        } else {
            exntcontent = exntcontent + exactNegativeWords[i];
        }
    }
    $("#ntwTextarea").val(ntwcontent);
    $("#entwTextarea").val(exntcontent);
}


//单击推广计划中的IP排除的事件
var excludeIpStr = "";
$(".excluedIp_5").click(function () {
    windowName = "excluedIp_5";
    $("#IpListTextarea").val("");
    var cid = $("#hiddenCampaignId").val();

    $.ajax({
        url: "/assistantCampaign/getObject",
        type: "post",
        data: {"cid": cid},
        dataType: "json",
        success: function (data) {
            var content = "";
            var excludeIp = data.excludeIp == undefined ? new Array() : data.excludeIp;
            for (var i = 0; i < excludeIp.length; i++) {
                if (i < excludeIp.length - 1) {
                    content = content + excludeIp[i] + "\r";
                } else {
                    content = content + excludeIp[i];
                }
            }
            $("#IpListTextarea").val(content);
            IpListTextareaCount();
        }
    });

    setDialogCss("setExcludeIp");
});

/**
 * 添加推广计划中的设置排除ip
 */
$(".inputExcludeIp_add").click(function () {
    windowName = "inputExcludeIp_add";
    $("#IpListTextarea").val(excludeIpStr);
    setDialogCss("setExcludeIp");
});

function IpListTextareaCount() {
    var IpListTextarea = $("#IpListTextarea").val();
    var ips = IpListTextarea.split("\n");
    $("#IpListCount").html("IP排除(" + (ips[0] == "" ? 0 : ips.length) + "/20)");
}


/**
 * IP排除设置单击事件
 */
$(".excludeIpOk").click(function () {

    var cid = $("#hiddenCampaignId").val();
    var ipList = $("#IpListTextarea").val();

    var ipArray = ipList.split("\n");
    var errorIp = "";

    if (ipArray.length > 20) {
        //alert("IP排除数量最大为20个");
        AlertPrompt.show("IP排除数量最大为20个");
        return;
    }

    for (var i = 0; i < ipArray.length; i++) {
        if (validateIPFormat(ipArray[i]) == false) {
            errorIp = errorIp + "\n" + ipArray[i];
        }
    }

    if (errorIp.length != 0 && ipArray[0] != "") {
        alert("IP地址格式输入不正确!" + errorIp);
        AlertPrompt.show("IP地址格式输入不正确!" + errorIp);
        return;
    }

    if (windowName == "excluedIp_5") {
        whenBlurEditCampaign(9, ipList);
    } else if (windowName == "inputExcludeIp_add") {
        excludeIpStr = ipList;
    }
    $(".TB_overlayBG").css({display: "none"});
    $("#setExcludeIp").hide(0);
});


//单击推广计划中的推广时段的事件
var windowName = "";
var jsonSchdule_add = null;
$(".schedule_5,.inputSchedule_add").click(function () {
    if ($(this)[0].className == "schedule_5") {
        windowName = "scheduleOk";
        var cid = $("#hiddenCampaignId").val();
        getCampaignObjByCidAndCreateUI(cid);
    } else if ($(this)[0].className == "inputSchedule_add") {
        windowName = "addCampaign_schedule";
        if (jsonSchdule_add == null) {
            jsonSchdule_add = {};
            jsonSchdule_add["schedule"] = "";
        }
        createChooseTimeUIByCampaignData(jsonSchdule_add);
    }
    setDialogCss("setExtension");
});


//单击设置推广时段窗口中确定按钮的事件
$(".scheduleOk").click(function () {
    if (windowName == "scheduleOk") {
        //得到用户选择的时间段
        var schecdules = getInputScheduleData();
        whenBlurEditCampaign(4, schecdules);
    } else if (windowName == "addCampaign_schedule") {
        jsonSchdule_add = {};
        jsonSchdule_add["schedule"] = getInputScheduleData();
    }
    $(".TB_overlayBG").css({display: "none"});
    $("#setExtension").hide(0);
});


//得到用户选择的推广时段数据
function getInputScheduleData() {
    var jsonArray = new Array();

    $(".hours").find("input[type=checkbox]").each(function () {
        var is = $(this)[0];
        if (is.checked == false) {
            var ul = $(this).parentsUntil("ul").parent();
            var lastHour = 25;
            var startHour;
            var weekDay = $(this).attr("name");
            ul.find(".changeGray").each(function (index) {
                if (index == 0) {
                    startHour = $(this).html();
                }
                if ((parseInt($(this).html()) - 1) > lastHour) {
                    var scheduleType = {};
                    scheduleType["weekDay"] = weekDay;
                    scheduleType["startHour"] = startHour;
                    scheduleType["endHour"] = (parseInt(lastHour) + 1);
                    jsonArray.push(scheduleType);
                    startHour = $(this).html();
                }
                lastHour = $(this).html();
                if (ul.find(".changeGray").last().html() == $(this).html()) {
                    var scheduleType = {};
                    scheduleType["weekDay"] = weekDay;
                    scheduleType["startHour"] = startHour;
                    scheduleType["endHour"] = (parseInt($(this).html()) + 1);
                    jsonArray.push(scheduleType);
                }
            });

        }
    });
    return jsonArray;
}

/**
 * 推广时段时间选择效果
 */
$(".hours").delegate("li", "click", function () {
    if ($(this).attr("class") == "changeGreen") {
        $(this).removeClass("changeGreen");
        $(this).addClass("changeGray");
    } else {
        $(this).removeClass("changeGray");
        $(this).addClass("changeGreen");
    }
    whenChooseLiToSetCheckBox($(this));
});

//当选中了某时段时，将复选框状态设置为未选中，若当天没有选中任何时段，则将复选框设置为选中状态
function whenChooseLiToSetCheckBox(thisLi) {
    var ul = thisLi.parent();
    if (ul.find(".changeGray").length == 0) {
        ul.find("input[type=checkbox]")[0].checked = true;
    } else {
        ul.find("input[type=checkbox]")[0].checked = false;
    }
}


$(".hours").delegate("li", "mouseover", function () {
    if (isPressDownCtrl == true) {
        if ($(this).attr("class") == "changeGreen") {
            $(this).removeClass("changeGreen");
            $(this).addClass("changeGray");
        } else {
            $(this).removeClass("changeGray");
            $(this).addClass("changeGreen");
        }
        whenChooseLiToSetCheckBox($(this));
    }

});


/**
 * 设置推广时段的选择星期几的复选框事件
 */
$(".hours").delegate("input[type=checkbox]", "click", function () {
    var ul = $(this).parentsUntil("ul").parent();
    if ($(this)[0].checked == true) {
        ul.find("li[class=changeGray]").removeClass("changeGray");
        ul.find("li[class='']").addClass("changeGreen");
    }
});


/**
 * 生成选择推广时段的ui
 */

//campaignObj
function getCampaignObjByCidAndCreateUI(cid) {
    $.ajax({
        url: "/assistantCampaign/getObject",
        type: "post",
        data: {"cid": cid},
        dataType: "json",
        success: function (data) {
            createChooseTimeUIByCampaignData(data);
        }
    });
}

/**
 * 根据传过来的数据生成设置推广时段的ui
 */
function createChooseTimeUIByCampaignData(data) {
    var weeks = new Array("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
    var html = "";
    for (var i = 0; i < weeks.length; i++) {
        if (data.schedule == undefined) {
            html = html + "<ul>" + "<div><input type='checkbox' checked='checked'' name='" + (i + 1) + "'/>" + weeks[i] + "</div>";
        } else {
            for (var s = 0; s < data.schedule.length; s++) {
                if (data.schedule[s].weekDay == (i + 1)) {
                    html = html + "<ul>" + "<div><label class='checkbox-inlines'><input type='checkbox'  name='" + (i + 1) + "'/>" + weeks[i] + "</label></div>";
                    break;
                }
            }
            if (s >= data.schedule.length) {
                html = html + "<ul>" + "<div>  <label class='checkbox-inlines'><input type='checkbox' checked='checked'' name='" + (i + 1) + "'/>" + weeks[i] + "</label></div>";
            }
        }

        var changeGrayArray = new Array();
        if (data.schedule != null) {
            for (var m = 0; m < data.schedule.length; m++) {
                if (data.schedule[m].weekDay == (i + 1)) {
                    for (var n = 0; n < 24; n++) {
                        if (n >= data.schedule[m].startHour && n < data.schedule[m].endHour) {
                            changeGrayArray.push(n);
                        }
                    }
                }
            }
        }

        for (var j = 0; j <= 23; j++) {
            var className = "changeGreen";

            for (var a = 0; a < changeGrayArray.length; a++) {
                if (changeGrayArray[a] == j) {
                    className = "changeGray";
                    break;
                }
            }

            if (j % 6 == 0) {
                html = html + "<li style='margin-left: 10px;' class='" + className + "'>" + j + "</li>";
            } else {
                html = html + "<li class='" + className + "'>" + j + "</li>"
            }
        }
        html = html + "</ul><br/><br/>";
    }
    $(".hours").html(html);
}


/*显示设置推广地域窗口*/
rDrag.init(document.getElementById('SetAera'));
$(".regionTarget_5").click(function () {
    $(".TB_overlayBG").css({
        display: "block", height: $(document).height()
    });
    $("#ctrldialogplanRegionDialog").css({
        left: ($("body").width() - $("#ctrldialogplanRegionDialog").width()) / 2 - 20 + "px",
        top: ($(window).height() - $("#ctrldialogplanRegionDialog").height()) / 2 + $(window).scrollTop() + "px",
        display: "block"
    });
    $(".close").click(function () {
        $(".TB_overlayBG").css("display", "none");
        $("#ctrldialogplanRegionDialog").css("display", "none");
    });
    var cid = $("#hiddenCampaignId").val();
    /*    top.dialog({
     title: "设置推广地域",
     padding: "5px",
     content: "<iframe src='/assistantCampaign/showSetPlace?cid=" + cid + "' width='900' height='500' marginwidth='0' marginheight='0' scrolling='no' frameborder='0'></iframe>",
     oniframeload: function () {
     },
     onclose: function () {
     },
     onremove: function () {
     }
     }).showModal();*/

});
var AreaClick = $("#ctrlradioboxpartRegion,#ctrlradioboxallRegion,#useAcctRegion,#usePlanRegion");
AreaClick.click(function () {
    $("#ctrldialogplanRegionDialog").css({
        left: ($("body").width() - $("#ctrldialogplanRegionDialog").width()) / 2 - 20 + "px",
        top: ($(window).height() - $("#ctrldialogplanRegionDialog").height()) / 2 + $(window).scrollTop() + "px",
        display: "block"
    });
});

/**
 * 弹出添加推广计划的窗口
 */
$("#addCampaign").click(function () {
    showAddCampaignWindow();
});

function showAddCampaignWindow() {
    $(".inputCampaignName").val("<请输入推广计划名称>");
    $(".inputBudget").val("<请输入每日预算，不填默认为不限定>");
    $(".inputPriceRatio").val("");
    setDialogCss("plan");
}


/**
 * 创建推广计划并转到关键词
 */
$("#createCampaignOk").click(function () {
    var bgt = $("#acBgt").html();
    var campaignName = $("input[name='inputCampaignName']").val();
    var budget = $("input[name='inputBudget']").val();
    var priceRatio = $("input[name='inputPriceRatio']").val();
    var campaignPause = $("#inputCampaignPause").val();
    var showProb = $("#inputShowProb").val();
    var schedule = getInputScheduleData();
//    var regionTarget =//
    var negativeWords = negativeWordsValue;
    var exactNegativeWords = exactNegativeWordsValue
    var excludeIp = excludeIpStr;

    var adgroupName = $("#inputAdgroupName").val();
    var maxPrice = $("#inputAdgroupPrice").val();
    var adgroupPause = $("#inputAdgroupPause").val();

    if (campaignName == "" || campaignName == "<请输入推广计划名称>") {
        AlertPrompt.show("请输入推广计划名称！");
        //alert("请输入推广计划名称");
        return;
    } else {
        if (parseInt(getChar(campaignName)) > 30) {
            //alert("推广计划名不能超过30个字符，汉字占两个字符");
            AlertPrompt.show("推广计划名不能超过30个字符，汉字占两个字符");
            return;
        }
    }
// /^-?\d+\.?\d*$/
    if (budget != "<请输入每日预算，不填默认为不限定>") {
        if (/^[0-9]+|[0-9]+\.[0-9]{2}$/.test(budget) == false) {
            //alert("每日预算只能是数值");
            AlertPrompt.show("每日预算只能是数值");
            return;
        } else {
            if (parseFloat(budget).toFixed(3) <= 49) {
                //alert("每日预算必须大于50RMB");
                AlertPrompt.show("每日预算必须大于50RMB");
                return;
            } else {
                if (parseInt(budget) > parseInt(bgt)) {
                    //alert("每日预算不能大于账户预算值");
                    AlertPrompt.show("每日预算不能大于账户预算值");
                    return;
                }
            }
        }
    } else {
        budget = null;
    }
    if (priceRatio != "") {
        if (!/^-?\d+\.?\d*$/.test(priceRatio)) {
            //alert("移动出价比例只能是数值");
            AlertPrompt.show("移动出价比例只能是数值");
            return;
        } else {
            if (parseFloat(priceRatio).toFixed(2) < 0.1 || parseFloat(priceRatio).toFixed(2) > 10.0) {
                //alert("0.1<移动出价比例<10.0");
                AlertPrompt.show("0.1<移动出价比例<10.0");
                return;
            }
        }
    }
    if (/^[0-9]+|[0-9]+\.[0-9]{2}$/.test(priceRatio) == false) {

    }
    if (adgroupName == "" || adgroupName == "<请输入推广单元名称>") {
        //alert("请输入推广单元名称");
        AlertPrompt.show("请输入推广单元名称");
        return;
    } else {
        if (parseInt(getChar(adgroupName)) > 30) {
            //alert("推广单元名不能超过30个字符，汉字占两个字符");
            AlertPrompt.show("推广单元名不能超过30个字符，汉字占两个字符");
            return;
        }
    }
    if (maxPrice != "") {
        if (!/^-?\d+\.?\d*$/.test(maxPrice)) {
            //alert("推广单元的最高出价只能是数字!");
            AlertPrompt.show("推广单元的最高出价只能是数字！");
            return;
        }
    } else {
        //alert("请输入推广单元最高出价！");
        AlertPrompt.show("请输入推广单元最高出价！");
        return;
    }

    $.ajax({
        url: "/assistantCampaign/add",
        type: "post",
        data: {
            "campaignName": campaignName,
            "budget": budget,
            "priceRatio": priceRatio,
            "pause": campaignPause,
            "showProb": showProb,
            "schedule": JSON.stringify(schedule),
            "negativeWords": negativeWords,
            "exactNegativeWords": exactNegativeWords,
            "excludeIp": excludeIp,
            "adgroupName": adgroupName,
            "maxPrice": maxPrice,
            "adgroupPause": adgroupPause
        },
        success: function (data) {
            if (data.msg == "1") {
                //alert("添加成功");
                AlertPrompt.show("添加成功！");
                loadTree();
                getCampaignList(0)
                jsonSchdule_add = null;
                negativeWordsValue = "";
                exactNegativeWordsValue = "";
                excludeIpStr = "";
                $("#plan input[type=text]").val("");
                $(".TB_overlayBG,#plan").hide(0);
            } else {
                //alert(data.msg);
                AlertPrompt.show(data.msg);
            }
        }
    });


    //loadTree();
});


/**
 * 验证输入的ip格式是否正确
 */
function validateIPFormat(ipAddress) {
    var regex = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.((\d{1,2}|1\d\d|2[0-4]\d|25[0-5])|(\*))$/;
    return regex.test(ipAddress);
}


/**
 * 设置弹出框css
 * @param idSelector id选择器
 */
function setDialogCss(idSelector) {
    $(".TB_overlayBG").css({
        display: "block", height: $(document).height()
    });
    $("#" + idSelector).css({
        left: ($("body").width() - $("#" + idSelector).width()) / 2 - 20 + "px",
        top: ($(window).height() - $("#" + idSelector).height()) / 2 + $(window).scrollTop() + "px",
        display: "block"
    });
}
$(".closeAddCampaign").click(function () {
    //关闭该窗口时重新赋值
    jsonSchdule_add = null;
    negativeWordsValue = "";
    exactNegativeWordsValue = "";
    excludeIpStr = "";
});


/**
 * 还原按钮的单击事件
 */
$("#reduction_caipamgin").click(function () {
    showReductionCampaignWindow();
});


function showReductionCampaignWindow() {
    var choose = $("#tbodyClick5").find(".list2_box3");
    if (choose != undefined && choose.find("td:last").html() != "&nbsp;") {
        if (confirm("是否还原选择的数据?") == false) {
            return;
        }

        var step = choose.find("td:last span").attr("step");
        var id = $("#tbodyClick5").find(".list2_box3").find("input[type=hidden]").val();
        switch (parseInt(step)) {
            case 1:
                reducCpg_Add(id);
                break;
            case 2:
                reducCpg_update(id);
                break;
            case 3:
                reducCpg_del(id);
                break;
            case 4:
                break;
        }

    }
}


/**
 * 还原新增的推广计划(localStatus为1的)
 * @param id
 */
function reducCpg_Add(id) {
    $.ajax({
        url: "/assistantCampaign/delete",
        type: "post",
        data: {"cid": id},
        dataType: "json",
        success: function (data) {
            $("#tbodyClick5").find(".list2_box3").remove();
            loadTree();
        }
    });
}

/**
 * 还原修改的推广计划(localStatus为2的)
 * @param id
 */
function reducCpg_update(id) {
    $.ajax({
        url: "/assistantCampaign/reducUpdate",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (data) {
            var html = campaignDataToHtml(data, 0);
            var tr = $("#tbodyClick5").find(".list2_box3");
            tr.replaceWith(html);
            setCampaignValue(html, data.campaignId);
            loadTree();
        }
    });
}


/**
 * 还原软删除
 * @param id
 */
function reducCpg_del(id) {
    $.ajax({
        url: "/assistantCampaign/reducDel",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (data) {
            loadTree();
            $("#tbodyClick5").find(".list2_box3 td:last").html("&nbsp;");
        }
    });
}


/**
 * 弹出快速创建计划窗口
 */
$("#quickAddplan").click(function () {
    showQuickAddPlanWindow();
});

function uploadCampagin() {
    var _list = $("#tbodyClick5").find(".list2_box3");
    var id = _list.find("input[type=hidden]").val();
    var step = _list.find("td:last span").attr("step");
    if (step != undefined) {
        if (confirm("是否上传选择的数据到凤巢?一旦上传将不能还原！") == false) {
            return;
        }
    } else {
        //alert("已经是最新数据了！");
        AlertPrompt.show("已经是最新数据了！");
        return;
    }
    switch (parseInt(step)) {
        case 1:
            if (id.length > 18) {
                uploadOperate(id, 1);
            }
            break;
        case 2:
            uploadOperate(id, 2);
            break
        case 3:
            if (id.length < 18) {
                uploadOperate(id, 3);
            } else {
                deleteCampaign();
            }
            break;
    }
}
function uploadOperate(id, ls) {
    $.get("/assistantCampaign/upload", {cid: id, ls: ls}, function (result) {
        if (result.msg == "1") {
            //alert("上传成功!");
            AlertPrompt.show("上传成功！");
            getCampaignList(0);
            setTimeout("loadTree()", 1500);
        } else {
            AlertPrompt.show(result.msg);
            //alert(result.msg);
        }
    });
}

function showQuickAddPlanWindow() {
    top.dialog({
        title: "快速新建计划",
        padding: "5px",
        align: 'right bottom',
        content: "<iframe src='/assistantCampaign/showCreatePlanWindow' width='900' height='620' marginwidth='0' marginheight='0' scrolling='no' frameborder='0'></iframe>",
        oniframeload: function () {
        },
        onclose: function () {
            if (jsonData.cid != null) {
                whenClickTreeLoadData(getCurrentTabName(), getNowChooseCidAndAid());
                loadTree();
            }
        },
        onremove: function () {
            loadTree();
            whenClickTreeLoadData(getCurrentTabName(), getNowChooseCidAndAid());
        }
    }).showModal(dockObj);
}

/**
 * 弹出窗口的关闭事件
 */
$(".closeAddCampaign").click(function () {
    $(this).parent().parent().hide(0);
})

//推广时段的取消按钮的事件
function closeSetExtension() {
    if ($("#plan").css("display") == "none") {
        $(".TB_overlayBG").css({display: "none"});
    }
    $("#setExtension").hide(0);
}
function closeSetExcludeIp() {
    if ($("#plan").css("display") == "none") {
        $(".TB_overlayBG").css({display: "none"});
    }
    $("#setExcludeIp").hide(0);
}
function closeSetNegtiveWord() {
    if ($("#plan").css("display") == "none") {
        $(".TB_overlayBG").css({display: "none"});
    }
    $("#setNegtiveWord").hide(0);
}


//Ctrl键按下
var isPressDownCtrl = false;
$(window).on("keydown keyup", function (event) {
    var keyCode = event.keyCode;
    if (keyCode == 17) {
        if (event.type == "keydown") {
            isPressDownCtrl = true;
        } else {
            isPressDownCtrl = false;
        }
    }
});
/************************************************************关键词的右击菜单************************************************************/
/**
 * 菜单名，方法
 * @type {{text: string, func: func}}
 */
var menu_campaign_add = {
        text: "添加计划",
        img: "../public/img/zs_function1.png",
        func: function () {
            showAddCampaignWindow();
        }
    }, menu_campaign_quickCreatePlan = {
        text: "快速创建计划",
        img: "../public/img/zs_function17.png",
        func: function () {
            showQuickAddPlanWindow();
        }
    }, menu_campaign_del = {
        text: "删除计划",
        img: "../public/img/zs_function2.png",
        func: function () {
            deleteCampaign();
        }
    }, menu_campaign_redu = {
        text: "还原",
        img: "../public/img/zs_function9.png",
        func: function () {
            showReductionCampaignWindow();
        }
    }, menu_campaign_upload = {
        text: "更新到凤巢",
        img: "../public/img/update2.png",
        func: function () {
            uploadCampagin();
        }
    }, menu_campaign_searchWord = {
        text: "搜索词",
        img: "../public/img/zs_function10.png",
        func: function () {
            searchword();
        }
    }
    , menu_keyword_copy = {
        text: "复制",
        img: "../public/img/zs_function13.png",
        func: function () {
            editCommons.Copy();
        }

    }
    , menu_keyword_paste = {
        text: "粘贴",
        img: "../public/img/zs_function15.png",
        func: function () {
            editCommons.Parse();
        }
    }
    , menu_keyword_select = {
        text: "全选",
        img: "../public/img/zs_function16.png",
        func: function () {
            CtrlAll();
        }

    }
/**
 * 右键菜单显示的选项
 * @type {*[]}
 */
var campaignMenuData = [
    [menu_campaign_add, menu_campaign_quickCreatePlan, menu_campaign_del, menu_campaign_redu, menu_campaign_upload, menu_campaign_searchWord, menu_keyword_copy, menu_keyword_paste, menu_keyword_select]
];
/**
 * 用户缓存右键点击的对象
 * @type {null}
 */
var tmp = null;
/**
 * 菜单name值，标识唯一，beforeShow显示完成后方法
 * @type {{name: string, beforeShow: beforeShow}}
 */
var campaignMenuExt = {
    name: "campaign",
    beforeShow: function () {
        var _this = $(this);
        tmp = _this;
        $.smartMenu.remove();
    }
};
$("#tbodyClick5").on("mousedown", "tr", function () {
    $(this).smartMenu(campaignMenuData, campaignMenuExt);
});


//多行选中
/*$("tbody").delegate("tr","click", function () {
 if(isPressDownCtrl==true){
 alert($(this).attr("class"));
 if(/list2_box3/.test($(this).attr("class"))){
 $(this).removeClass("list2_box3");
 }else{
 $(this).addClass("list2_box3");
 }
 }else{
 $(this).parent().find("tr").removeClass("list2_box3");
 $(this).addClass("list2_box3");
 }
 });*/
function campListCheck() {
    var CheckCount = $("input[name='campaignCheck']").length;
    var readyCheckCount = 0;
    for (var i = 0; i < CheckCount; i++) {
        if ($("input[name='campaignCheck']:eq(" + i + ")").prop("checked")) {
            readyCheckCount++;
        }
    }
    if (CheckCount == readyCheckCount) {
        document.getElementsByName("campaignAllCheck")[0].checked = true;
    } else {
        document.getElementsByName("campaignAllCheck")[0].checked = false;
    }
}
