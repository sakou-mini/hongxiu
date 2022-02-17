var pc_exportData ;
var dp_exportData ;
var dl_exportData ;
var np_exportData;
var time1 = new Date(fun_date(-6)).getTime();
var time2 = new Date(fun_date(0)).getTime();



// 时间选择器初始化
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

//定义下载内容

function dl_download() {
    var aoa = [
        ['充值包', '收入金额',"收入金额百分比","人次","人次百分比"],
    ];
    for(var i = 0;i<dl_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dl_exportData.yAxis.data[i]);
        newDataArr.push(dl_exportData.series[0].data[i]);
        newDataArr.push(dl_exportData.series[0].markPoint.data[i].value);
        newDataArr.push(dl_exportData.series[1].data[i]);
        newDataArr.push(dl_exportData.series[1].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '充值包方式.xlsx');
}

function dp_download() {
    var aoa = [
        ['充值方式', '收入金额',"收入金额百分比","人次","人次百分比"],
    ];
    for(var i = 0;i<dp_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dp_exportData.yAxis.data[i]);
        newDataArr.push(dp_exportData.series[0].data[i]);
        newDataArr.push(dp_exportData.series[0].markPoint.data[i].value);
        newDataArr.push(dp_exportData.series[1].data[i]);
        newDataArr.push(dp_exportData.series[1].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '充值方式.xlsx');
}

function pc_download(){
    if(pc_pageControl === "nw"){
        var aoa = [
            ['每周充值次数', '人次',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '每周充值次数.xlsx');
    }
    else if(pc_pageControl === "nm"){
        var aoa = [
            ['每月充值次数', '人次',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '每月充值次数.xlsx');
    }
    else if(pc_pageControl === "ew"){
        var aoa = [
            ['每周充值额度', '人次',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '每周充值额度.xlsx');
    }else if(pc_pageControl === "em"){
        var aoa = [
            ['每月充值额度', '人次',"百分比"],
        ];
        for(var i = 0;i<pc_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.yAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            newDataArr.push(pc_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '每月充值额度.xlsx');
    }

}

function np_download() {
        var aoa = [
            ['时间', '人次',"百分比"],
        ];
        for(var i = 0;i<np_exportData.yAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.yAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            newDataArr.push(np_exportData.series[0].markPoint.data[i].value);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '玩家充值间隔.xlsx');

}


timeUpdate();
//点击时间选择器控件
$("#theT_open").on("click",function () {
    theTshow()
})

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

var option;
//帮助弹框控件
var pc_helpControl = "hidden";
var dp_helpControl = "hidden";
var dl_helpControl = "hidden";
var np_helpControl = "hidden";
//页面控件
var pc_pageControl = "nw";

var pcChart;
var dpChart;
var dlChart;
var npChart;
//图表配置
option = {
    title: {
        text: ''
    },

    toolbox:{

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
dataRendering()

function dataRendering(){
    pc_dataRendering();
    dp_dataRendering();
    dl_dataRendering();
    np_dataRendering();
};
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    if(pc_pageControl === "nw"){
        //    请求事件
        var o = JSON.parse(JSON.stringify(option));
        var simulationData = [
            {time:"1次",value:62},
            {time:"2次",value:78},
            {time:"3次",value:74},
            {time:"4次",value:60},
            {time:"5次",value:58},
            {time:"6~10次",value:54},
            {time:"11~20次",value:45},
            {time:"21~30次",value:34},
            {time:"31~40次",value:24},
            {time:"41~50次",value:15},
            {time:">50次",value:6},
        ]
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "每周充值次数";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["人次"];
        o.series = [{
            name: '人次',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "nm") {
        var o = JSON.parse(JSON.stringify(option));
        var simulationData = [
            {time:"1次",value:642},
            {time:"2次",value:718},
            {time:"3次",value:704},
            {time:"4次",value:601},
            {time:"5次",value:508},
            {time:"6~10次",value:546},
            {time:"11~20次",value:454},
            {time:"21~30次",value:341},
            {time:"31~40次",value:254},
            {time:"41~50次",value:152},
            {time:">50次",value:61},
        ]
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];

        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "每月充值次数";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["人次"];
        o.series = [{
            name: '人次',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl==="ew"){
        var simulationData = [
            {time:"<￥10",value:97},
            {time:"￥10~50",value:68},
            {time:"￥50~100",value:75},
            {time:"￥100~200",value:125},
            {time:"￥200~500",value:25},
            {time:"￥500~1000",value:7},
            {time:"￥1000~3000",value:6},
            {time:"￥3000~5000",value:3},
            {time:"￥5000~10000",value:0},
            {time:">￥10000",value:1}
        ]
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];
        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "每周充值额度";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["人次"];
        o.series = [{
            name: '人次',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "em"){
        var simulationData = [
            {time:"<￥10",value:957},
            {time:"￥10~50",value:628},
            {time:"￥50~100",value:715},
            {time:"￥100~200",value:1225},
            {time:"￥200~500",value:253},
            {time:"￥500~1000",value:70},
            {time:"￥1000~3000",value:61},
            {time:"￥3000~5000",value:30},
            {time:"￥5000~10000",value:12},
            {time:">￥10000",value:6}
        ]
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];
        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "每周充值额度";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["人次"];
        o.series = [{
            name: '人次',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
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
function dp_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"微信",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
        {time:"支付宝",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
        {time:"银联",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
    ]
    var time = [];
    var value1 = [];
    var value2 = [];
    var num1 = 0;
    var num2 = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].time);
        value1.unshift(simulationData[i].value1);
        value2.unshift(simulationData[i].value2);
        num1 += simulationData[i].value1
        num2 += simulationData[i].value2
    }
    var pointArr1 = [];
    var pointArr2 = [];
    for(var x = 0;x<value1.length;x++){
        var percentage = ((value1[x]/num1)*100).toFixed(2)+"%"
        var pointObj = {
            coord: [value1[x],time[x]],
            value:percentage
        }
        pointArr1.push(pointObj)
        percentage = ((value2[x]/num2)*100).toFixed(2)+"%"
        pointObj = {
            coord: [value2[x],time[x]],
            value:percentage
        }
        pointArr2.push(pointObj)
    }
    o.title.text = "充值方式";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["收入金额","充值人数"];
    o.series = [{
        name: '收入金额',
        type: 'bar',
        data: value1,
        markPoint:{
            data:pointArr1,
            symbol:'pin',
            symbolRotate:-90
        }
    },{
        name: '充值人数',
        type: 'bar',
        data: value2,
        markPoint:{
            data:pointArr2,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    dp_exportData = JSON.parse(JSON.stringify(o));
    dp_HeavyLoad(o);

    $("#dp_help").on("click",function () {
        if(dp_helpControl === "show"){
            $("#dp_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            dp_helpControl = "hidden";
        }
        else {
            $("#dp_helpTypeModality").attr("class","helpTypeModality");
            dp_helpControl = "show";
        }
    });
    $("#dp_download").on("click",function () {
        dp_download()
    })
}
function dl_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"金币",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
        {time:"畅玩包",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
        {time:"道具",value1:randomNum(1000,10000),value2:randomNum(100,1000)},
    ]
    var time = [];
    var value1 = [];
    var value2 = [];
    var num1 = 0;
    var num2 = 0;
    for(var i=0;i<simulationData.length;i++){
        time.unshift(simulationData[i].time);
        value1.unshift(simulationData[i].value1);
        value2.unshift(simulationData[i].value2);
        num1 += simulationData[i].value1
        num2 += simulationData[i].value2
    }
    var pointArr1 = [];
    var pointArr2 = [];
    for(var x = 0;x<value1.length;x++){
        var percentage = ((value1[x]/num1)*100).toFixed(2)+"%"
        var pointObj = {
            coord: [value1[x],time[x]],
            value:percentage
        }
        pointArr1.push(pointObj)
        percentage = ((value2[x]/num2)*100).toFixed(2)+"%"
        pointObj = {
            coord: [value2[x],time[x]],
            value:percentage
        }
        pointArr2.push(pointObj)
    }
    o.title.text = "充值包方式";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["收入金额","充值人数"];
    o.series = [{
        name: '收入金额',
        type: 'bar',
        data: value1,
        markPoint:{
            data:pointArr1,
            symbol:'pin',
            symbolRotate:-90
        }
    },{
        name: '充值人数',
        type: 'bar',
        data: value2,
        markPoint:{
            data:pointArr2,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    dl_exportData = JSON.parse(JSON.stringify(o));
    dl_HeavyLoad(o);

    $("#dl_help").on("click",function () {
        if(dl_helpControl === "show"){
            $("#dl_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            dl_helpControl = "hidden";
        }
        else {
            $("#dl_helpTypeModality").attr("class","helpTypeModality");
            dl_helpControl = "show";
        }
    });
    $("#dl_download").on("click",function () {
        dl_download()
    })
}

function np_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
        //    请求事件
        var simulationData = [
            {time:"首日",value:62},
            {time:"1小时内",value:78},
            {time:"1-24小时",value:74},
            {time:"1天",value:60},
            {time:"2天",value:58},
            {time:"3天",value:54},
            {time:"4天",value:45},
            {time:"5-7日",value:34},
            {time:"1-2周",value:24},
            {time:"2周-1月",value:15},
            {time:"1月至3月",value:6},
            {time:"3月至1年",value:4},
            {time:"1年+",value:2},
        ]
        var time = [];
        var value = [];
        var num = 0;
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift(simulationData[i].value);
            num += simulationData[i].value
        }
        var pointArr = [];
        for(var x = 0;x<value.length;x++){
            var percentage = ((value[x]/num)*100).toFixed(2)+"%"
            var pointObj = {
                coord: [value[x],time[x]],
                value:percentage
            }
            pointArr.push(pointObj)
        }

        o.title.text = "玩家充值间隔";
        o.xAxis.type="value";
        o.yAxis.type="category";
        o.yAxis.data=time;
        o.legend.data = ["人次"];
        o.series = [{
            name: '人次',
            type: 'bar',
            data: value,
            markPoint:{
                data:pointArr,
                symbol:'pin',
                symbolRotate:-90
            }
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)

    $("#np_help").on("click",function () {
        if(np_helpControl === "show"){
            $("#np_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            np_helpControl = "hidden";
        }
        else {
            $("#np_helpTypeModality").attr("class","helpTypeModality");
            np_helpControl = "show";
        }
    });
    $("#np_download").on("click",function () {
        np_download()
    })
}




















//定义点击标签事件
$(".pc_titleClick").on("click",function () {
    pc_pageControl = this.getAttribute("data-value");
    pc_dataRendering()
});

$(".np_titleClick").on("click",function () {
    np_pageControl = this.getAttribute("data-value");
    np_dataRendering()
});
//图表自适应
window.addEventListener("resize", function () {
    pcChart.resize();
    dpChart.resize();
    dlChart.resize();
    npChart.resize();
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
function dp_HeavyLoad(o) {
    var dpChartDom = document.getElementById('dp_chart');
    dpChartDom.remove();
    $("#dp").get(0).innerHTML += '<div id="dp_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var dpChartDom = document.getElementById('dp_chart');
    dpChart = echarts.init(dpChartDom);
    dpChart.setOption(o);
}
function dl_HeavyLoad(o) {
    var dlChartDom = document.getElementById('dl_chart');
    dlChartDom.remove();
    $("#dl").get(0).innerHTML += '<div id="dl_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var dlChartDom = document.getElementById('dl_chart');
    dlChart = echarts.init(dlChartDom);
    dlChart.setOption(o);
}

function np_HeavyLoad(o) {
    var npChartDom = document.getElementById('np_chart');
    npChartDom.remove();
    $("#np").get(0).innerHTML += '<div id="np_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var npChartDom = document.getElementById('np_chart');
    npChart = echarts.init(npChartDom);
    npChart.setOption(o);
}