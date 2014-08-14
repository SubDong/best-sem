<%--
  Created by IntelliJ IDEA.
  User: baizz
  Date: 14-7-25
  Time: 下午6:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>大数据智能营销</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/accountCss/public.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/accountCss/style.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/public/themes/flick/jquery-ui-1.11.0.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/public/css/ui.daterangepicker.css">


    <style type="text/css">
        .title{
            width:100%;
            height:60px;
            background-color: #ffffff;
            border-bottom: 2px solid #CBC5D1;
        }

        .title span{
            float:left;
            margin-left: 15px;
            margin-top: 20px;
            font-size:16px;
            font-family: "微软雅黑";
            height: 67%;
            border-bottom: 2px solid gray;
        }

    </style>

</head>
<body>
<div class="top over">
    <div class="logo fl">
        <img src="${pageContext.request.contextPath}/public/img/logo.png">
    </div>
    <div class="logo_text fl">
        大数据智能营销
    </div>
</div>
<div class="concent over">
    <div class="nav fl">
        <div class="nav_left over fl">
            <div class="user over">
                <div class="user_img fl">
							<span class="img">
								<img src="${pageContext.request.contextPath}/public/img/user.png">
							</span><span class="name"> JOHN DOE </span>
                </div>
                <div class="user_close fr">
                    <a href="#">
                        退出
                    </a>
                </div>
            </div>
            <div class="nav_mid">
                <div class="nav_top">
                    帐户全景<span></span>
                </div>
                <div class="nav_under over">
                    <ul>
                        <li>
                            <h3>推广助手</h3>
                        </li>
                        <li>
                            <span class="list1"></span>
                            <a href="#">
                                账户预警
                            </a>
                        </li>
                        <li>
                            <span class="list2"></span>
                            <a href="#">
                                批量上传
                            </a>
                        </li>
                        <li>
                            <span class="list3"></span>
                            <a href="#">
                                批量操作
                            </a>
                        </li>
                        <li>
                            <span class="list4"></span>
                            <a href="#">
                                关键词查找
                            </a>
                        </li>
                        <li>
                            <span class="list5"></span>
                            <a href="#">
                                推广查询
                            </a>
                        </li>
                        <li>
                            <h3>智能结构</h3>
                        </li>
                        <li>
                            <span class="list6"></span>
                            <a href="#">
                                关键词拓展
                            </a>
                        </li>
                        <li>
                            <span class="list7"></span>
                            <a href="#">
                                智能分组
                            </a>
                        </li>
                        <li>
                            <h3>智能竞价</h3>
                        </li>
                        <li>
                            <span class="list8"></span>
                            <a href="#">
                                诊断关键词
                            </a>
                        </li>
                        <li>
                            <span class="list9"></span>
                            <a href="#">
                                筛选关键词
                            </a>
                        </li>
                        <li>
                            <span class="list10"></span>
                            <a href="#">
                                设置规则
                            </a>
                        </li>
                        <li>
                            <h3>数据报告</h3>
                        </li>
                        <li>
                            <span class="list11"></span>
                            <a href="#">
                                基础报告
                            </a>
                        </li>
                        <li>
                            <span class="list12"></span>
                            <a href="#">
                                定制报告
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="tips fl">
            <input type="image" src="${pageContext.request.contextPath}/public/img/button2.png">
        </div>
    </div>

    <div class="mid over fr">
        <div class="on_title over">
            <a href="#">
                推广助手
            </a>
            &nbsp;&nbsp;>&nbsp;&nbsp;<span>账户预警</span>
        </div>

        <div class="title"><span>消费提醒</span></div>
        <div><br/>
            <table border="1">
                <thead>
                    <tr>
                        <td>百度账户ID</td>
                        <td>预算类型</td>
                        <td>预算金额</td>
                        <td>预警百分率</td>
                        <td>手机号码</td>
                        <td>邮箱地址</td>
                        <td>是否启用</td>
                    </tr>
                </thead>
                <c:forEach items="${list}" var="va">
                <tr>
                    <td><c:out value="${va.id}"/></td>
                    <td><c:out value="${va.budgetType}"/></td>
                    <td><c:out value="${va.budget}"/></td>
                    <td><c:out value="${va.warningPercent}"/></td>
                    <td><c:out value="${va.tels}"/></td>
                    <td><c:out value="${va.mails}"/></td>
                    <td>
                        <select onchange=" window.location.href='/assistant/updateWarningRuleOfIsEnbled?id=${va.id}'+'&isEnbled='+this.value;">
                            <c:if test="va.isEnable==0">
                                <option value = "0" selected="true">禁用</option>
                                <option value = "1">启用</option>
                            </c:if>
                                <c:if test="va.isEnable==1">
                                    <option value = "0">禁用</option>
                                    <option value = "1" selected="true">启用</option>
                                </c:if>
                            </select>
                    </td>
                </tr>
                </c:forEach>
            </table>

        </div>












        <div class="footer">
            <p>
                <a href="#">
                    关于我们
                </a>
                |
                <a href="#">
                    联系我们
                </a>
                |
                <a href="#">
                    隐私条款
                </a>
                |
                <a href="#">
                    诚聘英才
                </a>
            </p>

            <p>
                Copyright@2013 perfect-cn.cn All Copyright Reserved. 版权所有：北京普菲特广告有限公司京ICP备***号
            </p>
        </div>
    </div>
</div>
</body>
</html>