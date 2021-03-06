var loadExistsCampaign = function () {
    $.ajax({
        url: '/campaign/getAllDownloadCampaign',
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            $("#existsCampaign ul").empty();
            var results = data.rows;
            if (results.length > 0) {
                $.each(results, function (i, item) {
                    var _li = "";
                    if (i % 2 == 0) {
                        _li = "<li><label class='checkbox-inlines'><input id='" + item.campaignId + "' type='checkbox' name='campaign_exists'>" + item.campaignName + "</label></li>";
                    } else {
                        _li = "<li><label class='checkbox-inlines'><input id='" + item.campaignId + "' type='checkbox' class='current' name='campaign_exists'>" + item.campaignName + "</label></li>";
                    }
                    $("#existsCampaign ul").append(_li);
                });
            }
        }
    });
};

var loadNewCampaignData = function () {
    $.ajax({
        url: '/account/getNewCampaign',
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            $("#newCampaign ul").empty();
            var results = data.rows;
            if (results.length > 0) {
                $.each(results, function (i, item) {
                    var _li = "";
                    if (i % 2 == 0) {
                        _li = "<li><label class='checkbox-inlines'><input id='" + item.campaignId + "' type='checkbox' name='campaign_new'>" + item.campaignName + "</label></li>";
                    } else {
                        _li = "<li><label class='checkbox-inlines'><input id='" + item.campaignId + "' type='checkbox' class='current' name='campaign_new'>" + item.campaignName + "</label></li>";
                    }
                    $("#newCampaign ul").append(_li);
                });
            }
        }
    });
};
var ajaxbg = $("#background,#progressBar");
var updateAllCampaign = function () {
    $.ajax({
        url: '/account/updateAccountData',
        type: 'POST',
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            //alert("更新成功!");
            AlertPrompt.show("更新成功!");
            ajaxbg.hide();
            $(".TB_overlayBG").css("display", "none");
            $("#download").css("display", "none");
            $("#downloadData").css("display", "none");

        }
    });
};

var updateExistsCampaign = function () {
    var campaignIds = "";
    $.each($("input[name='campaign_exists']:checked"), function (i, item) {
        if (item.checked) {
            campaignIds += item.id + ",";
        }
    });

    if (campaignIds.length == 0) {
        return;
    }

    campaignIds = campaignIds.substring(0, campaignIds.length - 1);

    $.ajax({
        url: '/account/updateAccountData',
        type: 'POST',
        dataType: 'json',
        data: {
            "campaignIds": campaignIds
        },
        success: function (data, textStatus, jqXHR) {
            //alert("更新成功!");
            AlertPrompt.show("更新成功!");
            ajaxbg.hide();
            $(".TB_overlayBG").css("display", "none");
            $("#download").css("display", "none");
        }
    });
};

var updateNewCampaign = function () {
    var campaignIds = [];
    $.each($("input[name='campaign_new']:checked"), function (i, item) {
        if (item.checked) {
            campaignIds.push(item.id);
        }
    });

    if (campaignIds.length == 0) {
        return;
    }

    $.ajax({
        url: '/account/updateAccountData',
        type: 'POST',
        dataType: 'json',
        data: {
            "campaignIds": JSON.stringify(campaignIds)
        },
        success: function (data, textStatus, jqXHR) {
            //alert("更新成功!");
            AlertPrompt.show("更新成功!");
             ajaxbg.hide();
            $(".TB_overlayBG").css("display", "none");
            $("#download").css("display", "none");
        }
    });
};

var index = 0;
$(function () {
    //loadExistsCampaign();
    //
    //loadNewCampaignData();

    $("input[name=no1]").on('click', function () {
        $.each($("input[name=no1]"), function (i, item) {
            if (item.checked) {
                if (i == 0) {
                    index = i;
                } else if (i == 1) {
                    index = i;
                } else if (i == 2) {
                    index = i;
                }
            }
        });
    });

    $("#downloadAccount").on('click', function () {
        if (index == 0) {
            updateAllCampaign();
        } else if (index == 1) {
            updateExistsCampaign();
        } else if (index == 2) {
            updateNewCampaign();
        }
    });

});