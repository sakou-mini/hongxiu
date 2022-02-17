var pc_exportData ;
var time1 = fun_date(-6);
var time2 = fun_date(0);


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
    init()
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
    init()
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
    init()
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
    init()
})









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



timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

//时间选择器点击确定按钮事件
$("#theT_confirm").on("click",function () {
    if(verificationTheTTime() === "ok"){
        theTclose();
        innerTime();
        init()
    }
})

init()
function init() {
    $.get({
        url:window.ioApi.liveUser.getLiveRecordChartData,
        data:{
            liveUserId:GetQueryString("liveUserId"),
            startTime:$("#theT_openTime").val()===""?new Date(time1).getTime():new Date($("#theT_openTime").val()).getTime(),
            endTime:$("#theT_endTime").val()===""?new Date(time2).getTime():new Date($("#theT_endTime").val()).getTime()
        },
        success:function (res) {
            console.log(res);
            pc_dataRendering(res)
        }
    });
}

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
    timeUpdate()
});

//时间选择器显示方法
function theTshow() {
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden")
}

//时间选择器隐藏方法
function theTclose(){
    $("#theT_boxHidden").attr("class","layui-row theT_boxHidden theT_hidden")
}

var option;
//帮助弹框控件
var pc_helpControl = "hidden";
//页面控件
var pc_pageControl = "live";

var pcChart;

//图表配置
option = {
    title: {
        text: ''
    },
    tooltip: {
        trigger: 'axis',

        //百分比配置
        axisPointer: {
            label:{
                formatter: null
            }
        }
    },
    legend: {
        data: [
            // '新玩家', '老玩家'
        ]
    },
    grid: {
        left: '5%',
        right: '6%',
        bottom: '5%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: [
            // '2020-3-4', '2020-3-5', '2020-3-6', '2020-3-7', '2020-3-8', '2020-3-9', '2020-3-10'
        ]
    },

    yAxis: {
        type: 'value',
        //y坐标百分比配置
        axisLabel: {
            formatter:null
        }
    },
    series: [
        // {
        //     name: '老玩家',
        //     type: 'line',
        //     data: [312, 250, 351, 334, 299, 355, 381]
        // },
        // {
        //     name: '新玩家',
        //     type: 'line',
        //     data: [220, 182, 191, 234, 290, 330, 310]
        // }
    ]
};

//渲染图标
function pc_dataRendering(s){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData=[];
    if(pc_pageControl === "live"){
        var rank = []
        for(var x=0;x<s.length;x++){
            var itemd = {};
            itemd.time = dateFormat("YYYY-mm-dd",s[x].time);
            itemd.income=parseInt(s[x].income);
            itemd.num=parseInt(s[x].audienceNum);
            simulationData.push(itemd);
            if(s[x].rank<=5 && s[x].rank > 0){
                rank.push({coord:[itemd.time,itemd.num],value:"榜"+s[x].rank})
            }
        }

        //    请求事件
        var time = [];
        var income = [];
        var num = []
        for(var z=0;z<simulationData.length;z++){
            time.push(simulationData[z].time);
            income.push(simulationData[z].income);
            num.push(simulationData[z].num);
        }
        o.title.text = "直播记录";
        o.xAxis.data=time;
        o.legend.data = ["收入（元）","人数"];
        o.series = [{
            name: '收入（元）',
            type: 'bar',
            data:income
        },{
            name: '人数',
            type: 'bar',
            data:num,
            markPoint:{
                symbol:"pin",
                data:rank,
            }
        }];
        console.log(o);
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }else{

        for(var y=0;y<s.length;y++){
            var itemd = {};
            itemd.time = dateFormat("YYYY-mm-dd",s[y].time);
            itemd.liveTime=(parseInt(s[y].liveTime)/1000/60/60).toFixed(2);
            console.log(itemd.liveTime);
            simulationData.push(itemd)
        }
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.push(simulationData[i].time);
            value.push(simulationData[i].liveTime);
        }
        o.title.text = "考勤记录";
        o.xAxis.data=time;
        o.legend.data = ["直播时长（小时）",];
        o.series = [{
            name: '直播时长（小时）',
            type: 'bar',
            data:value,
            markLine: {
                data: [
                    {type: 'average', name: '平均值'}
                ]
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }

    $("#pc_help").on("click",function () {
        if(pc_helpControl === "show"){
            $("#pc_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            pc_helpControl = "hidden";
        }
        else {
            $("#pc_helpTypeModality").attr("class","helpTypeModality");
            pc_helpControl = "show";
        }
    });
    $("#pc_download").on("click",function () {
        pc_download()
    })

}

//定义点击标签事件
$(".pc_titleClick").on("click",function () {
    pc_pageControl = this.getAttribute("data-value");
    $.get({
        url:window.ioApi.liveUser.getLiveRecordChartData,
        data:{
            liveUserId:GetQueryString("liveUserId"),
            startTime:new Date(time1).getTime(),
            endTime:new Date(time2).getTime()
        },
        success:function (res) {
            console.log(res);
            pc_dataRendering(res)
        }
    });
});

//图表自适应
window.addEventListener("resize", function () {
    pcChart.resize();
});


//图表重载
function pc_HeavyLoad(o) {
    var pcChartDom = document.getElementById('pc_chart');
    pcChartDom.remove();
    $("#pc").get(0).innerHTML += '<div id="pc_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var pcChartDom = document.getElementById('pc_chart');
    pcChart = echarts.init(pcChartDom);
    pcChart.setOption(o);
}



