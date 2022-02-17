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

displayRoleElement(["LIVEUSERRECORDQSEARCHLIVEUSERID"],"idSearch")
displayRoleElement(["LIVEUSERRECORDQEXCEL"],"excel")
var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var nowDataMsg
function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            limits: [10,20,30,40,50,100,200] ,
            jump: function(obj, first){
                size =obj.limit;
                page = obj.curr;
                if(!first){
                    $.get({
                        url:window.ioApi.liveUser.queryPlatformLiveRecordInfo,
                        data:getParameter(),
                        success:function (res) {
                            count = res.total;
                            console.log(res);
                            nowDataMsg=filterData(res.content)
                            tableData(filterData(res.content));
                            getPage();
                        }
                    });
                }
            }
        });
    });
}

function getParameter(){
    var platform = 3
    var roomId = $("#roomId").val()
    var liveUserId = $("#liveUserId").val()
    var displayName = $("#displayName").val()
    var startTime = $("#test1").val() === ""? "": new Date($("#test1").val()+" 00:00:00").getTime()
    var endTime = $("#test2").val() === ""? "": new Date($("#test2").val()+" 00:00:00").getTime()

    return {
       platform,roomId,liveUserId,displayName,startTime,endTime,page,size
    }
}

layui.use('layer', function(){
    var layer = layui.layer
});
// 插件加载
layui.use('element', function(){
    var element = layui.element;
});


$("#resetBtn").on("click",function () {
    location.reload();
});


function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '主播审核列表'
            ,loading:false
            ,limit:size
            ,cellMinWidth:150
            , cols: [[
                {field: 'recordTime', title: '日期',width: 200,align:"center"},
                {field: 'displayName', title: '主播昵称',align:"center"},
                {field: 'liveUserId', title: '主播ID',align:"center"},
                {field: 'liveStartTime', title: '开播时间',align:"center" },
                {field: 'liveEndTime', title: '下播时间',align:"center" },
                {field: 'liveTime', title: '开播时长',align:"center" },
                {field: 'audienceHistory', title: '访客人数',align:"center" },
                {field: 'liveVisitorCount', title: '访问次数',align:"center" },
                {field: 'giftCount', title: '收礼次数',align:"center" },
                {field: 'giftFlow', title: '打赏收入',align:"center" },
                {field: 'newFansNum', title: '粉丝增长',align:"center" },
                {field: 'bulletMessageCount', title: '弹幕互动',align:"center" },
                {field: 'connectedLiveCount', title: '连麦次数',align:"center" },
                {field: 'loginDevice', title: '登录设备',align:"center" },
                {field: 'liveIp', title: '登录IP',align:"center" },

            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpDetailsUrl("liveUserDetailInfo","liveUserId",data.liveUserId)

            }
        });
        roleTableBtn(["LIVEUSERRECORDQDETAILINFOQ"],"detail")
    });
}

$("#searchBtn").on("click",function () {
    page = 1;
    $.get({
        url:window.ioApi.liveUser.queryPlatformLiveRecordInfo,
        data:getParameter(),
        success:function (res) {
            count = res.total
            nowDataMsg=filterData(res.content)
            tableData(filterData(res.content))
            getPage();
        }
    });
})


$.get({
    url:window.ioApi.liveUser.queryPlatformLiveRecordInfo,
    data:{
        page:1,
        size:size,
        startTime: $("#test1").val() === "" ?  "" :  isIE()?
            new Date(( $("#test1").val()+" 00:00:00").replace(/-/g, '/')).getTime():new Date(($("#test1").val()+" 00:00:00")).getTime(),
        endTime:$("#test2").val() === "" ?  "" :  isIE()?
            new Date(( $("#test2").val()+" 00:00:00").replace(/-/g, '/')).getTime():new Date(($("#test2").val()+" 00:00:00")).getTime(),
        platform:3
    },
    success:function (res) {
        count = res.total
        console.log(res);
        nowDataMsg=filterData(res.content)
        tableData(filterData(res.content))
        getPage();
    }
});

function filterData(d) {
    var list = [];
    for(var i=0;i<d.length;i++){
        var obj = {};
        obj.displayName=d[i].displayName;
        obj.id=d[i].userId;
        obj.head=d[i].avatar;
        obj.liveUserId = d[i].liveRecord.liveUserId;
        obj.recordTime = dateFormat("YYYY-mm-dd",d[i].liveRecord.liveStartTime)
        obj.liveTime=formatSeconds(d[i].liveRecord.liveTime);
        obj.liveTimeLong=formatSeconds(d[i].liveRecord.recordTime -d[i].liveRecord.liveStartTime);
        obj.giftFlow=d[i].liveRecord.giftFlow;
        obj.totalIncome = d[i].totalIncome;
        obj.gameFlow=d[i].liveRecord.gameFlow;
        obj.totalGameFlow=d[i].totalGameFlow;
        obj.audienceNum = d[i].liveRecord.audienceNum;
        obj.bulletMessageCount = d[i].liveRecord.bulletMessageCount;
        obj.fansNum = d[i].liveRecord.fansNum;
        obj.liveIp = d[i].liveRecord.liveIp;
        obj.totalLiveTime=formatSeconds(d[i].liveRecord.totalLiveTime);
        obj.newFansNum=d[i].liveRecord.newFansNum;
        obj.liveStartTime = dateFormat("HH:MM:SS",d[i].liveRecord.liveStartTime)
        obj.liveEndTime = dateFormat("HH:MM:SS",d[i].liveRecord.recordTime)
        obj.liveVisitorCount = d[i].liveRecord.liveVisitorCount
        obj.audienceHistory = d[i].liveRecord.audienceHistory.length
        obj.giftCount = d[i].liveRecord.giftCount
        obj.giftFlow = d[i].liveRecord.giftFlow
        obj.loginDevice = brandForNum(d[i].liveRecord.loginDevice)
        obj.connectedLiveCount = d[i].liveRecord.connectedLiveCount
        list.push(obj)
    }
    return list
}


$("#excel").on("click",function () {
    let excelData = getParameter()
    delete excelData.page
    delete excelData.size
    $.get({
        url:window.ioApi.liveUser.queryPlatformLiveRecordInfo,
        data:excelData,
        success:function (res) {
            nowDataMsg=filterData(res.content)
            getExcel()
        }
    });
})

function getExcel(){
    var aoa = [
        ['日期', '主播昵称',"主播ID",
            "开播时间","下播时间","开播时长",
            "访客人数","访问次数","收礼次数",
            "打赏收入","粉丝增长", "弹幕互动",
            "连麦次数","登录设备","登录IP"
        ],
    ];
    for(var i = 0;i<nowDataMsg.length;i++){
        var newDataArr = [];
        newDataArr.push(nowDataMsg[i].recordTime);
        newDataArr.push(nowDataMsg[i].displayName);
        newDataArr.push(nowDataMsg[i].liveUserId);
        newDataArr.push(nowDataMsg[i].liveStartTime);
        newDataArr.push(nowDataMsg[i].liveEndTime);
        newDataArr.push(nowDataMsg[i].liveTime);
        newDataArr.push(nowDataMsg[i].audienceHistory);
        newDataArr.push(nowDataMsg[i].liveVisitorCount);
        newDataArr.push(nowDataMsg[i].giftCount);
        newDataArr.push(nowDataMsg[i].giftFlow);
        newDataArr.push(nowDataMsg[i].newFansNum);
        newDataArr.push(nowDataMsg[i].bulletMessageCount);
        newDataArr.push(nowDataMsg[i].connectedLiveCount);
        newDataArr.push(nowDataMsg[i].loginDevice);
        newDataArr.push(nowDataMsg[i].liveIp);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '直播记录 导出日期：'+dateFormat("YYYY-mm-dd",new Date().getTime())+'.xlsx');
}
$("#resetBtn").on("click", function () {
    location.reload();
});