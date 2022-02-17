layui.use('layer', function () {
    var layer = layui.layer
});
layui.use('element', function () {
    var element = layui.element;
});

layui.use('laydate', function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1'


    });
    laydate.render({
        elem: '#test2'


    });
})

var exportData

var searchData = getFormData();
var nowPage = 1;
var size = 10
var banUserControl = 0;
function pageGat (count, nowpage) {
    layui.use('laypage', function () {
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit: size,
            curr: nowpage,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function (obj, first) {
                size = obj.limit;
                if (!first) {
                    searchData = getFormData();
                    searchData.page = obj.curr;
                    searchData.size = obj.limit;

                    $.get({
                        url: window.ioApi.user.dailyUserActive,
                        data: searchData,
                        success: function (res) {
                            console.log("pageLOg");
                            dataCount = res.data.total;
                            tableData(res.data.content, searchData.platform);
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
    searchData.page = 1
    searchData.size = size
    $.get({
        url: window.ioApi.user.dailyUserActive,
        data: searchData,
        success: function (res) {
            console.log(res);
            dataCount = res.total;
            tableData(res.data.content, searchData.platform);
            pageGat(res.data.total, nowPage);
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取用户列表错误'
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
    searchData = getFormData();
    searchData.page = 1
    searchData.size = size
    $.get({
        url: window.ioApi.user.dailyUserActive,
        data: searchData,
        success: function (res) {
            dataCount = res.total;
            tableData(res.data.content, searchData.platform);
            pageGat(res.data.total, nowPage)
        },
        error: function () {
            layer.open({
                title: '错误信息'
                , content: '获取主播列表错误'
            });
        }
    })
});
$("#resetBtn").on("click", function () {
    location.reload();
});

displayRoleElement(["SEARCHUSERID"], "userIdBox")
var tData;


function tableData (list, platform) {
    list = dFilter(list)
    exportData = list
    var cols= [
        { field: 'dailyTime', title: '日期', align: "center" ,width:200},
        { field: 'displayName', title: '用户昵称', align: "center" },
        { field: 'userId', title: '用户ID', align: "center" },
        { field: 'platform', title: '平台', align: "center" },
        { field: 'avgLiveWatchTime', title: '平均停留时间', align: "center" },
        { field: 'liveWatchTime', title: '直播间停留时间', align: "center" },
        { field: 'giftCount', title: '打赏次数', align: "center" },
        { field: 'giftFlow', title: '打赏金额', align: "center" },
        { field: 'connectedLiveCount', title: '连麦次数', align: "center" ,width:100},
        { field: 'bulletMessageCount', title: '弹幕互动', align: "center" },
    ]

    layui.use('table', function () {
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , limit: size
            , title: '用户列表'
            , loading: false
            , cellMinWidth: 150
            , cols: [cols],
            id: 'testReload'
        });
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                jumpDetailsUrl("userDetails", "userId", data.userId)
            }
            else if (obj.event === 'toPage') {
                window.localStorage.setItem('displayName', data.displayName);
                jumpDetailsUrl("userBetRecord", "userId", data.userId)
            }
        });
        roleTableBtn(["USERDETAILS"], "detail")
        roleTableBtn(["USERBETRECORD"], "toPage")
    });

}

function dFilter(list) {
    console.log([...list]);
    for (var i = 0; i < list.length; i++) {
        list[i].dailyTime = dateFormat("YYYY-mm-dd", list[i].dailyTime)
        list[i].avgLiveWatchTime = formatSeconds(list[i].avgLiveWatchTime)
        list[i].liveWatchTime = formatSeconds(list[i].liveWatchTime)
        list[i].platform = getPlatform(list[i].platform)
    }
    console.log(list);
    return list
}


function getFormData () {
    var userId = $("#userId").val()
    var displayName = $("#displayName").val()
    var startTime = $("#test1").val() === "" ? "" : new Date($("#test1").val()).getTime()
    var endTime = $("#test2").val() === "" ? "" : new Date($("#test2").val()).getTime()
    var platform = parseInt($("#platform").val())
    return {
        userId,
        displayName,
        startTime,
        endTime,
        platform
    }
}


$("#excel").on("click", function () {
    let upData = getFormData()
    $.get({
        url: window.ioApi.user.dailyUserActive,
        data: upData,
        success: function (res) {
            exportData = dFilter(res.data.content)
            getExcel()
        }
    })
})


// { field: 'dailyTime', title: '日期', align: "center" ,width:200},
// { field: 'displayName', title: '用户昵称', align: "center" },
// { field: 'userId', title: '用户ID', align: "center" },
// { field: 'platform', title: '平台', align: "center" },
// { field: 'avgLiveWatchTime', title: '平均停留时间', align: "center" },
// { field: 'liveWatchTime', title: '直播间停留时间', align: "center" },
// { field: 'giftCount', title: '打赏次数', align: "center" },
// { field: 'giftFlow', title: '打赏金额', align: "center" },
// { field: 'connectedLiveCount', title: '连麦次数', align: "center" ,width:100},
// { field: 'bulletMessageCount', title: '弹幕互动', align: "center" },

function getExcel(){
    var aoa = [
        ['日期', '用户昵称', "用户ID", "平台",
            "平均停留时间", "直播间停留时间", "打赏次数", "打赏金额",
            "连麦次数", "弹幕互动"
        ],
    ];
    for (var i = 0; i < exportData.length; i++) {
        var newDataArr = [];
        newDataArr.push(exportData[i].dailyTime);
        newDataArr.push(exportData[i].displayName);
        newDataArr.push(exportData[i].userId);
        newDataArr.push(exportData[i].platform);
        newDataArr.push(exportData[i].avgLiveWatchTime);
        newDataArr.push(exportData[i].liveWatchTime);
        newDataArr.push(exportData[i].giftCount);
        newDataArr.push(exportData[i].giftFlow);
        newDataArr.push(exportData[i].connectedLiveCount);
        newDataArr.push(exportData[i].bulletMessageCount);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '用户报表 导出日期：' + dateFormat("YYYY-mm-dd", new Date().getTime()) + '.xlsx');
}

