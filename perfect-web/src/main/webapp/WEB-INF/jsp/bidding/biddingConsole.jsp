<%--
  Created by IntelliJ IDEA.
  User: baizz
  Date: 2014-9-26
  Time: 15:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object number = request.getSession().getAttribute(session.getId() + "-number");
    if (number == null) {
        number = 3;
    }
%>
<!doctype html>
<html>
<head>
    <title>Bidding Console</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/public/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/public/css/accountCss/backstage.css">
    <style type="text/css">
        .backstage_list ul li input, .backstage_list ul li select {
            height: 28px;
        }

        .table tr td {
            text-align: center;
        }
    </style>
</head>
<body>
<jsp:include page="../homePage/pageBlock/backstage_nav.jsp"/>
<div class="backstage_concent mid over">
    <div id="bidding_box">
<%--        <div class="backstage_list over">
            <ul>
                <li><span>请输入模拟登录次数:</span>
                    <label for="number"></label><input type="text" class="form-control fl" id="number">
                </li>
                <li><span>请输入验证码:</span>
                    <label for="imageCode"></label><input id="imageCode" type="text" class="form-control fl">
                    <img id="img" style="cursor: pointer;"
                         src="${pageContext.request.contextPath}/admin/bdLogin/getCaptcha"
                         onclick="refreshImg();" onerror="javascript:alert(${sessionScope.imgError});">
                    <button class="btn sure btn-lg" type="button" style="width:80px; margin-left:10px;"
                            onclick="validateImageCode();">提交
                    </button>
                </li>
            </ul>
        </div>--%>
        <div class="backstage_list over">
            <ul>
                <li>
                    <span>请输入curl: </span>
                    <input id="curlStr" type="text" class="form-control fl">
                    <button class="btn sure btn-lg" type="button" style="width:80px; margin-left:10px;" onclick="saveCurl();">提交</button>
                </li>
            </ul>
        </div>
        <div>
            <table id="table1"
                   class="table table-striped table-bordered table-hover datatable dataTable"
                   aria-describedby="DataTables_Table_0_info">
                <thead>
                <tr>
                    <td>总数</td>
                    <td>空闲</td>
                    <td>非空闲</td>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <a href="http://www2.baidu.com" target="_blank">百度推广后台登录</a>
    </div>
</div>
<script type="text/javascript" src="http://cdn.bootcss.com/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript" src="http://cdn.bootcss.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
<script type="text/javascript">
    String.prototype.trims = function () {
        return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
    };

    $(function () {
        <%--$("#number").val("${sessionScope.number}");--%>
        $("#number").val("<%=number%>");

        $.ajax({
            url: "/admin/biddingUrl/list",
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                $("#table1").find("tbody").empty();
                var results = data.rows;
                if (results != null && results.length > 0) {
                    var idle = 0;
                    var notIdle = 0;
                    $.each(results, function (i, item) {
                        if (item.idle == true) {
                            idle++;
                        } else {
                            notIdle++;
                        }
                    });
                    var tr = "<tr><td>" + results.length + "</td><td>" + idle + "</td><td>" + notIdle + "</td></tr>";
                    $("#table1").find("tbody").append(tr);
                }
            }
        });

    });

    var refreshImg = function () {
        var img = document.getElementById("img");
        img.setAttribute("src", "${pageContext.request.contextPath}/admin/bdLogin/getCaptcha?" + Math.random());
    };

    var saveCurl = function () {
        var curl = $("#curlStr").val();
        $.ajax({
            url: "/admin/bdLogin/saveCurl",
            type: "POST",
            dataType: "json",
            data: {
                "curl": curl
            },
            success: function (data, textStatus, jqXHR) {
                if(data.status == "success"){
                    window.location.reload(true);
//                    alert("success!");
                    baiduAccountAlertPrompt.show("success!")
                }
            }
        });
    };

    var validateImageCode = function () {
        var imageCode = $("#imageCode").val();
        if (imageCode == null || imageCode.trims().length == 0) {
            return false;
        }

        var number = $("#number").val();

        $.ajax({
            url: "/admin/bdLogin/checkImageCode",
            type: "POST",
            dataType: "json",
            data: {
                "number": number,
                "code": imageCode
            },
            success: function (data, textStatus, jqXHR) {
                if (data.status == "fail") {
                    refreshImg();
//                    alert("failed!");
                    baiduAccountAlertPrompt.show("failed!")
                } else if (data.status == "success") {
                    if (parseInt(data.number) == 0) {
//                        alert("全部模拟登录完成!");
                        baiduAccountAlertPrompt.show("全部模拟登录完成!")
                    } else {
                        window.location.reload(true);
//                        alert("success!\n 剩余: " + $("#number").val() + "次");
                        baiduAccountAlertPrompt.show("success!\n 剩余: " + $("#number").val() + "次")
                    }
                }
            }
        });
    };

</script>
</body>
</html>
