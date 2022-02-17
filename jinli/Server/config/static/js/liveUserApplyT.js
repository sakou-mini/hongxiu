layui.use('layer', function () {
    var layer = layui.layer
});
layui.use('element', function () {
    var element = layui.element;
});

var nowPage = 1;
function pageGat (count, nowpage) {
    layui.use('laypage', function () {
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit: 10,
            curr: nowpage,
            jump: function (obj, first) {
                if (!first) {
                    var data;
                    var msg = $("#class").val();
                    if (!upMsgVerification(msg)) {
                        data = {
                            page: obj.curr,
                            limit: obj.limit,
                            queryType: parseInt($("#violationType").val()),
                            platform: 2,
                            queryStatue:parseInt($("#status").val())
                        }
                    }
                    else {
                        data = {
                            page: obj.curr,
                            limit: obj.limit,
                            queryType: parseInt($("#violationType").val()),
                            condition: msg,
                            platform: 2,
                            queryStatue:parseInt($("#status").val())
                        }
                    }
                    console.log(data);
                    $.get({
                        url: window.ioApi.liveUser.getAppLiveUser,
                        data: data,
                        success: function (res) {
                            dataCount = res.count;
                            for (var i = 0; i < res.data.length; i++) {
                                if (res.data[i].phoneNumber === "unbound") {
                                    res.data[i].phoneNumber = "未绑定"
                                }
                            }
                            tableData(res.data.content);
                            pageGat(res.data.total, obj.curr)
                        },
                        error: function () {
                            layer.open({
                                title: '错误信息'
                                , content: '获取主播列表错误'
                            });
                        }
                    });
                }
            }
        });
    });
}
firstInitTable();
function firstInitTable () {
    var data;
    var msg = $("#class").val();
    if (!upMsgVerification(msg)) {
        data = {
            page: 1,
            limit: 10,
            queryType: 0,
            platform: 2,
            queryStatue:parseInt($("#status").val())
        }
    }
    else {
        data = {
            page: 1,
            limit: 10,
            queryType: 0,
            condition: msg.trim(),
            platform: 2,
            queryStatue:parseInt($("#status").val())
        }
    }
    $.get({
        url: window.ioApi.liveUser.getAppLiveUser,
        data: data,
        success: function (res) {
            dataCount = res.count;
            if (res.data) {
                for (var i = 0; i < res.data.length; i++) {
                    if (res.data[i].phoneNumber === "unbound") {
                        res.data[i].phoneNumber = "未绑定"
                    }
                }
            }
            tableData(res.data.content);
            pageGat(res.data.total, nowPage)
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取主播列表错误'
            });
        }
    })
}

layui.use('layer', function () {
    var layer = layui.layer
});
// 插件加载
layui.use('element', function () {
    var element = layui.element;
});

$("#searchBtn").on("click", function () {
    var msg = $("#class").val();
    $.get({
        url: window.ioApi.liveUser.getAppLiveUser,
        data: {
            page: 1,
            limit: 10,
            queryType: parseInt($("#violationType").val()),
            condition: msg.trim(),
            platform: 2,
            queryStatue:parseInt($("#status").val())
        },
        success: function (res) {
            dataCount = res.count;
            if (res.data) {
                for (var i = 0; i < res.data.length; i++) {
                    if (res.data[i].phoneNumber === "unbound") {
                        res.data[i].phoneNumber = "未绑定"
                    }
                }
            }
            tableData(res.data.content);
            pageGat(res.data.total, nowPage)
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取主播列表错误'
            });
        }
    })

})

$("#resetBtn").on("click", function () {
    location.reload();
})

function tableData (list) {
    for (var i = 0; i < list.length; i++) {
        console.log(list[i].applyLiveUserTime);
        list[i].applyLiveUserTime = dateFormat("YYYY-mm-dd HH:MM:SS", list[i].applyLiveUserTime)
        list[i].backOfficeName= list[i].backOfficeName?list[i].backOfficeName:"-"
        console.log(list[i].backOfficeName === "");
        switch (list[i].statue) {
            case 1:
                list[i].statue = "未审核"
                break
            case 2:
                list[i].statue = "已通过"
                break
            case 3:
                list[i].statue = "未通过"
                break
            default:
                list[i].statue = "错误状态"
        }
    }
    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '主播审核列表'
            , height: 500
            , loading: false
            , cols: [[
                { field: 'avatarUrl', title: '用户信息', templet: '#imgTemplet', width: 200, align: "center" },
                { field: 'displayName', title: '昵称', align: "center" },
                { field: 'phoneNumber', title: '手机号', align: "center" },
                { field: 'realName', title: '真实姓名', align: "center" },

                { field: "applyLiveUserTime", title: "申请时间", align: "center" },
                { field: "statue", title: "状态", align: "center" },
                { field: "backOfficeName", title: "操作人", align: "center" },
                { align: 'center', title: '操作', toolbar: '#barDemo', align: "center" }
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                jumpDetailsUrl("liveUserDetails", "id", data.id)
            }
        });
    });
}

/**
 * @method getLiveUser 根据ID获取主播信息
 * @param {string} id 主播ID
 * @param {function} callback 获取到主播信息后的回调函数，携带一个主播信息参数
 * */
function getLiveUser (id, callback) {
    $.get({
        url: window.ioApi.liveUser.getUserIdImage,
        data: {
            id: id
        },
        success: function (res) {
            callback(res)
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取主播身份证图片错误'
            });
        }
    })
}
function SubmitMsg () {
    layer.msg('审核提交成功');
}

