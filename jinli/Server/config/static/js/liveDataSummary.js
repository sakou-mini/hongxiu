layui.use('layer', function(){
    var layer = layui.layer
});
layui.use('element', function(){
    var element = layui.element;
});
layui.use('laydate', function(){
    var laydate = layui.laydate;
    laydate.render({
        elem: '#test1'
    });
    laydate.render({
        elem: '#test2'
    });
});
var exportData
var platform = 1

$("#search").on("click",function () {
    platform = parseInt($("#platform").val())
    getSummaryData()
})
getSummaryData()

function getSummaryData() {
    $.get({
        url:window.ioApi.userData.todayLiveTotalData,
        data:{
            platform
        },
        success:function (res) {
            console.log(res);
            $("#attentionNum").text(res.attentionNum)
            $("#banLiveUserCount").text(res.banLiveUserCount)
            $("#banUserCount").text(res.banUserCount)
            $("#bulletChatNum").text(res.bulletChatNum)
            $("#connectedLiveCount").text(res.connectedLiveCount)
            $("#incomeCoin").text(res.incomeCoin)
            $("#incomeUserNum").text(res.incomeUserNum)
            $("#liveCount").text(res.liveCount)
            $("#liveTime").text(formatSeconds(res.liveTime))
            $("#liveUserNum").text(res.liveUserNum)
            $("#liveVisitorCount").text(res.liveVisitorCount)
            // $("#platform").text(res.platform)
            $("#recordTime").text(res.recordTime)
            $("#rewardCount").text(res.rewardCount)
            $("#visitorNum").text(res.visitorNum)
        }
    })
}














var searchData = getFormData();
var nowPage = 1 ;
var size = 10
function pageGat(count,nowpage){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:nowpage,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                size =obj.limit;
                if(!first){
                    searchData = getFormData();
                    searchData.page=obj.curr;
                    searchData.size=obj.limit;

                    $.get({
                        url:window.ioApi.userData.queryLiveTotalData,
                        data:searchData,
                        success:function (res) {
                            dataCount = res.total;
                            tableData(res.content);
                        },
                        error:function () {
                            layer.open({
                                title: '????????????'
                                ,content: '??????????????????'
                            });
                        }
                    });
                }
            }
        });
    });
}
firstInitTable();
function firstInitTable(){

    searchData.page = 1
    searchData.size = size
    $.get({
        url:window.ioApi.userData.queryLiveTotalData,
        data:searchData,
        success:function (res) {
            console.log(res);
            dataCount = res.total;
            tableData(res.content);
            pageGat(res.total,nowPage);
        },
        error:function () {
            layer.open({
                title: '????????????'
                ,content: '??????????????????'
            });
        }
    })
}

layui.use('layer', function(){
    var layer = layui.layer
});
// ????????????
layui.use('element', function(){
    var element = layui.element;
});

$("#searchBtn").on("click",function () {
    searchData = getFormData();
    searchData.page = 1
    searchData.size = size
    $.get({
        url:window.ioApi.userData.queryLiveTotalData,
        data:searchData,
        success:function (res) {
            dataCount = res.total;
            tableData(res.content);
            pageGat(res.total,nowPage)


        },
        error:function () {
            layer.open({
                title: '????????????'
                ,content: '??????????????????'
            });
        }
    })
});
$("#resetBtn").on("click",function (){
    location.reload();
});

var tData;
function tableData(list) {
    for(var i=0;i<list.length;i++){
        list[i].recordTime = dateFormat("YYYY-mm-dd",list[i].recordTime)
        list[i].liveTime=formatSeconds(list[i].liveTime)
    }
    exportData = [...list]
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            ,limit:size
            , title: '????????????'
            ,loading:false
            , cols: [[
                {field: 'recordTime', title: '??????',align:"center"},
                {field: 'liveTime', title: '????????????',align:"center"},
                {field: 'liveCount', title: '????????????',align:"center"},
                {field: 'liveUserNum', title: '????????????',align:"center"},
                {field: 'rewardCount', title: '????????????',align:"center"},
                {field: 'incomeUserNum', title: '????????????',align:"center"},
                {field: 'incomeCoin', title: '????????????',align:"center"},
                {field: 'visitorNum', title: '????????????',align:"center"},
                {field: 'liveVisitorCount', title: '???????????????',align:"center"},
                {field: 'attentionNum', title: '????????????',align:"center"},
                {field: 'connectedLiveCount', title: '????????????',align:"center"},
                {field: 'bulletChatNum', title: '????????????',align:"center"},
                {field: 'banUserCount', title: '????????????',align:"center"}
            ]],
            id: 'testReload'
        });

    });
}



function getFormData() {

    var startTime = $("#test1").val() === ""? "": new Date($("#test1").val()+" 00:00:00").getTime()
    var endTime = $("#test2").val() === ""? "": new Date($("#test2").val()+" 00:00:00").getTime()
    var platform = parseInt($("#searchPlatform").val())
    return {
        platform,
        startTime,
        endTime
    }
}

$("#excel").on("click",function () {
    var aoa = [
        ['??????', '????????????',"????????????",
            "????????????","????????????","????????????",
            "????????????","????????????","???????????????",
            "????????????","????????????","????????????",
            "????????????"
        ],
    ];
    for(var i = 0;i<exportData.length;i++){
        var newDataArr = [];
        newDataArr.push(exportData[i].recordTime);
        newDataArr.push(exportData[i].liveTime);
        newDataArr.push(exportData[i].liveCount);
        newDataArr.push(exportData[i].liveUserNum);
        newDataArr.push(exportData[i].rewardCount);
        newDataArr.push(exportData[i].incomeUserNum);
        newDataArr.push(exportData[i].incomeCoin);
        newDataArr.push(exportData[i].visitorNum);
        newDataArr.push(exportData[i].liveVisitorCount);
        newDataArr.push(exportData[i].attentionNum);
        newDataArr.push(exportData[i].connectedLiveCount);
        newDataArr.push(exportData[i].bulletChatNum);
        newDataArr.push(exportData[i].banUserCount);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '?????????????????? ???????????????'+dateFormat("YYYY-mm-dd",new Date().getTime())+'.xlsx');
})