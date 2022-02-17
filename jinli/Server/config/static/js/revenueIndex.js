var time1 = new Date(fun_date(-6)).getTime();
var time2 = new Date(fun_date(0)).getTime();
timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

function timeUpdate(){
    layui.use('laydate', function(){
        var laydate = layui.laydate;
        laydate.render({
            elem: '#theT_openTime',
            value:fun_date(-6),
            max:0
        })
        laydate.render({
            elem: '#theT_endTime',
            value:fun_date(0),
            max:0
        });
    });
}

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function () {
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
        getInfoData()
    }
})

$("#theT_t").on("click",function () {
    $("#theT_openTime").val(fun_date(0));
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
    getInfoData()
})
$("#theT_y").on("click",function () {
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
    getInfoData()
})
$("#theT_w").on("click",function () {
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
    getInfoData()
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
    getInfoData()
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

var chartDom = document.getElementById('chartDom');
var myChart = echarts.init(chartDom);
var option;

option = {
    xAxis: {
        type: 'category',
        data: ['3-1', '3-2', '3-3', '3-4', '3-5', '3-6', '3-7']
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    yAxis: {
        type: 'value'
    },
    legend: {
        data: ['锦鲤平台']
    },
    series: [{
        name:"锦鲤平台",
        data: [0, 0, 0, 0, 0, 0, 0],
        type: 'bar',
    }]
};

option && myChart.setOption(option);


var sendObj = {};
var page = 1;
var size = 10;
var count = 100;

getPage()
function getPage(){
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        laypage.render({
            elem: 'paging',
            count: count,
            limit:size,
            curr:page,
            jump: function(obj, first){
                page = obj.curr;
                if(!first){
                    console.log(obj.curr);
                    analogData();
                    getPage();
                }else {
                    analogData();
                }
            }
        });
    });
}


function tableData(list) {
    for(var i=0;i<list.length;i++){
        if(!list[i].avatarUrl){
            list[i].head =window.config.imgUrl+"/images/avatar/default/img_avatar.png"
        }else {
            list[i].head = window.config.imgUrl+list[i].avatarUrl
        }
    }
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#demo'
            , data: list
            ,limit:count
            , title: "反馈列表"
            ,loading:false
            , cols: [[
                {field: 'time',width:300, title: '日期'},
                {field: 'flow',width:300, title: '收入'},
            ]],
            id: 'testReload'
        });
        table.on('tool(demo)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                jumpUrl("liveUserDetailInfo")
            }
        });
    });
}

function analogData() {
    count = 100;
    var data = []
    for(var i = 0;i<10;i++){
        var d = {}
        d.time = "12月"+(i+1)+"日";
        d.flow = 0;

        // d.roomId = randomNum(10000,99999)
        // slotNum++
        // d.slot = slotNum;
        // d.player = play[Math.floor(Math.random()*play.length)];
        // d.gameFlowing = randomNum(10000,9999999);
        // d.giftFlowing = randomNum(10000,9999999);
        // d.roomNum =  randomNum(100,100000);
        // d.time = dateFormat("YYYY-mm-dd HH:MM:SS",new Date().getTime() + randomNum(10000,1000000))
        data.push(d)
    }
    tableData(data)
}

$(".pc_titleClick").on("click",function () {
    analogData()
});