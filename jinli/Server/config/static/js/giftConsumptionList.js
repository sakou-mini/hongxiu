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
var sendObj = {};
var page = 1;
var size = 10;
var count = 0;
var time1;
var time2;
var platform = 1
function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
            jump: function(obj, first){
                size =obj.limit;
                if(!first){
                    var data = getFormData()
                    data.page = obj.curr;
                    data.size = obj.limit;
                    $.get({
                        url:window.ioApi.revenueAnalysis.getGiftRecords,
                        data:data,
                        success:function(res){
                            console.log(res);
                            tableData(giftFormat(res.data));
                            count = res.count;
                        }
                    })
                }
            }
        });
    });
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
/**
 * Maneuver Destructive Armor
 * XOR-MDA
 * INU-MDA
 * AMA-MDA
 * */
analogData();

function tableData(list) {
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            , title: '主播审核列表'
            ,limit:size
            ,loading:false
            , cols: [[
                {field: 'userName', title: '用户昵称（用户ID）',width:250,align:"center"},
                {field: 'liveUserName', title: '主播名称（主播ID）',width:250,align:"center"},
                {field: 'giftName', title: '礼物（礼物ID）',align:"center"},
                {field: 'sendNum', title: '礼物数量',align:"center" },
                {field: 'totalPrice', title: '礼物总价' ,align:"center"},
                {field: 'giftOfRoomId', title: '直播间ID',align:"center" },
                {field: 'sendTime', title: '赠送时间',align:"center"},
                {field: 'platformTag', title: '平台',align:"center"}
            ]],
            id: 'testReload'
        });
    });
}


$("#searchBtn").on("click",function () {

    var data = getFormData();
    data.page = page;
    data.size = size;
    $.get({
        url:window.ioApi.revenueAnalysis.getGiftRecords,
        data:data,
        success:function(res){
            console.log(res);
            tableData(giftFormat(res.data));
            count = res.count;
            getPage()
        }
    })
});


function analogData() {
    var userId = $("#element").val().trim();
    var userName = $("#class").val().trim();
    var roomId = $("#roomId").val().trim();
    $("#theT_openTime").val(fun_date(-6));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    $.get({
        url:window.ioApi.revenueAnalysis.getGiftRecords,
        data:{
            userId,
            userName,
            roomId,
            startTime:time1,
            endTime:time2,
            platform:platform,
            page,
            size
        },
        success:function(res){
            console.log(res);
            tableData(giftFormat(res.data));
            count = res.count;
            getPage()
        }
    })
}



if(isIE()){
    time1 = new Date(($("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
}else {
    time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
    time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
}
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function (){
    if(verificationTheTTime() === "ok"){
        if(isIE()){
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        }else {
            time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
            time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
        }
        theTclose();
        innerTime();
    }
});

$("#theT_t").on("click",function (){
    $("#theT_openTime").val(fun_date(0));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 23:59:59").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 23:59:59")).getTime();
    }
    theTclose();
    innerTime();

})
$("#theT_y").on("click",function (){
    $("#theT_openTime").val(fun_date(-1));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();

})
$("#theT_w").on("click",function (){
    $("#theT_openTime").val(fun_date(-6));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
})
$("#theT_m").on("click",function () {
    $("#theT_openTime").val(fun_date(-30));
    $("#theT_endTime").val(fun_date(0));
    if(isIE()){
        time1 = new Date(( $("#theT_openTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00").replace(/-/g, '/')).getTime();
    }else {
        time1 = new Date(($("#theT_openTime").val()+" 00:00:00")).getTime();
        time2 = new Date(($("#theT_endTime").val()+" 00:00:00")).getTime();
    }
    theTclose();
    innerTime();
})

// 刷新时间选择器显示时间
function innerTime(){
    var day1 = formatSpecificDate(new Date($("#theT_openTime").val()));
    var day2 = formatSpecificDate(new Date($("#theT_endTime").val()));
    $("#theT_open").val(day1+" ~ "+day2);
}

//初始化时间选择器时间
$("#theT_open").val(fun_date(-6)+" ~ "+fun_date(0));

//时间控制器取消按钮
$("#theT_cancel").on("click",function () {
    theTclose();
});

//时间选择器显示方法
function theTshow() {
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose(){
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden theT_hidden")
}

function giftFormat(list) {
    for(let i=0;i<list.length;i++){
        list[i].userName = list[i].userName+"（"+list[i].userId+"）";
        list[i].liveUserName = list[i].liveUserName+"（"+list[i].liveUserId+"）";
        for(var x=0;x<window.gift.length;x++){
            if(list[i].giftId === window.gift[x].id){
                list[i].giftName=window.gift[x].name+"（"+list[i].giftId+"）"
            }
        }
        list[i].sendTime = dateFormat('YYYY-mm-dd HH:MM:SS',list[i].sendTime)
        list[i].platformTag = getFlatformForNum(list[i].platformTag)
    }
    return list;
}


function getFormData() {
    var userId = $("#class").val().trim();
    var userName = $("#element").val().trim();
    var roomId = $("#roomId").val().trim();
    var startTime = time1;
    var endTime = time2;

    return {
        userId,
        userName,
        startTime,
        endTime,
        roomId,
        platform:parseInt($("#platform").val())
    }
}

timeUpdate();
// 时间选择器初始化
function timeUpdate(){
    layui.use('laydate', function(){
        var laydate = layui.laydate;
        laydate.render({
            elem: '#theT_openTime',
            value:fun_date(-6),
            max:0
        });
        laydate.render({
            elem: '#theT_endTime',
            value:fun_date(0),
            max:0
        });
    });
}