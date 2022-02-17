var pc_exportData ;
var np_exportData ;
var dp_exportData ;
var dl_exportData ;
var am_exportData ;
var gm_exportData ;
var time1 = fun_date(-6);
var time2 = fun_date(0);
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

//定义下载内容

function gm_download() {
    var aoa = [
        ['礼包', '首付玩家数',"百分比"],
    ];
    for(var i = 0;i<gm_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(gm_exportData.yAxis.data[i]);
        newDataArr.push(gm_exportData.series[0].data[i]);
        newDataArr.push(gm_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '首付充值包类型.xlsx');
}

function am_download() {
    var aoa = [
        ['金额', '首付玩家数',"百分比"],
    ];
    for(var i = 0;i<am_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(am_exportData.yAxis.data[i]);
        newDataArr.push(am_exportData.series[0].data[i]);
        newDataArr.push(am_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '玩家首付金额.xlsx');
}


function dl_download() {
    var aoa = [
        ['等级', '首付玩家数',"百分比"],
    ];
    for(var i = 0;i<dl_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dl_exportData.yAxis.data[i]);
        newDataArr.push(dl_exportData.series[0].data[i]);
        newDataArr.push(dl_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '玩家首付等级.xlsx');
}

function dp_download() {
    var aoa = [
        ['周期', '首付玩家数',"百分比"],
    ];
    for(var i = 0;i<dp_exportData.yAxis.data.length;i++){
        var newDataArr = [];
        newDataArr.push(dp_exportData.yAxis.data[i]);
        newDataArr.push(dp_exportData.series[0].data[i]);
        newDataArr.push(dp_exportData.series[0].markPoint.data[i].value);
        aoa.push(newDataArr)
    }
    var sheet = XLSX.utils.aoa_to_sheet(aoa);
    openDownloadDialog(sheet2blob(sheet), '玩家首付周期.xlsx');
}

function np_download() {
    if(np_pageControl === "rateD"){
        var aoa = [
            ['日期', '首日付费率',"首日付费数"],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]+"%");
            newDataArr.push(np_exportData.series[1].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '首日付费率.xlsx');
    }
    else if(np_pageControl === "rateW"){
        var aoa = [
            ['日期', '首周付费率',"首周付费数"],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]+"%");
            newDataArr.push(np_exportData.series[1].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '首周付费率.xlsx');
    }else {
        var aoa = [
            ['日期', '首月付费率',"首月付费数"],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]+"%");
            newDataArr.push(np_exportData.series[1].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '首月付费率.xlsx');
    }
}

function pc_download(){
    if(pc_pageControl === "newPaidPlayers"){
        var aoa = [
            ['日期', '新增付费玩家'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '新增付费玩家.xlsx');
    }
    else if(pc_pageControl === "cumulativePaidPlayers"){
        var aoa = [
            ['日期', '累计付费玩家'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '累计付费玩家.xlsx');
    }
    else {
        var aoa = [
            ['日期', '总体付费率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '总体付费率.xlsx');
    }

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
        dataRendering();
    }
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
var np_helpControl = "hidden";
var dp_helpControl = "hidden";
var dl_helpControl = "hidden";
var am_helpControl = "hidden";
var gm_helpControl = "hidden";
//页面控件
var pc_pageControl = "newPaidPlayers";
var np_pageControl = "rateD";

var pcChart;
var npChart;
var dpChart;
var dlChart;
var amChart;
var gmChart;
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

function dataRendering(){
    pc_dataRendering();
    np_dataRendering();
    dp_dataRendering();
    dl_dataRendering();
    am_dataRendering();
    gm_dataRendering();
};

dataRendering();
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    if(pc_pageControl === "newPaidPlayers"){
        //    请求事件
        var simulationData = [
            {time:"2021-3-2",value:40},
            {time:"2021-3-3",value:45},
            {time:"2021-3-4",value:12},
            {time:"2021-3-5",value:25},
            {time:"2021-3-6",value:56},
            {time:"2021-3-7",value:41},
            {time:"2021-3-8",value:35},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.title.text = "新增付费玩家";
        o.xAxis.data=time;
        o.legend.data = ["新增付费玩家"];
        o.series = [{
            name: '新增付费玩家',
            type: 'line',
            data:data
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)
    }else if(pc_pageControl === "cumulativePaidPlayers") {
        var simulationData = [
            {time:"2021-3-2",value:25422},
            {time:"2021-3-3",value:25452},
            {time:"2021-3-4",value:25445},
            {time:"2021-3-5",value:25485},
            {time:"2021-3-6",value:25788},
            {time:"2021-3-7",value:25478},
            {time:"2021-3-8",value:25877},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.title.text = "累计付费玩家";
        o.xAxis.data=time;
        o.legend.data = ["累计付费玩家"];
        o.series = [{
            name: '累计付费玩家',
            type: 'line',
            data:data
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o);
    }else {
        var simulationData = [
            {time:"2021-3-2",value:0.21},
            {time:"2021-3-3",value:0.24},
            {time:"2021-3-4",value:0.23},
            {time:"2021-3-5",value:0.22},
            {time:"2021-3-6",value:0.23},
            {time:"2021-3-7",value:0.27},
            {time:"2021-3-8",value:0.33},
        ];
        var data = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            simulationData[i].value = (simulationData[i].value*100).toFixed(2);
            data.push(simulationData[i].value)
            time.push(simulationData[i].time)
        }
        o.tooltip.formatter =  '总体付费率：{c0}% ';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "总体付费率";
        o.xAxis.data=time;
        o.legend.data = ["总体付费率"];
        o.series = [{
            name: '次日留存率',
            type: 'line',
            data:data
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
function np_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    o.yAxis = [
        {
            type: 'value',
            axisLabel: {
                formatter: function (value) {
                    return value+'%'
                }
            }
        },
        {
            type: 'value',
            axisLabel: {
                formatter:null
            }
        }
    ]


    if(np_pageControl === "rateD"){
        //    请求事件
        var simulationData = [
            {time:"2021-3-2",type:"z",value:40},
            {time:"2021-3-3",type:"z",value:45},
            {time:"2021-3-4",type:"z",value:12},
            {time:"2021-3-5",type:"z",value:25},
            {time:"2021-3-6",type:"z",value:56},
            {time:"2021-3-7",type:"z",value:41},
            {time:"2021-3-8",type:"z",value:35},
            {time:"2021-3-2",type:"l",value:0.21},
            {time:"2021-3-3",type:"l",value:0.20},
            {time:"2021-3-4",type:"l",value:0.23},
            {time:"2021-3-5",type:"l",value:0.25},
            {time:"2021-3-6",type:"l",value:0.24},
            {time:"2021-3-7",type:"l",value:0.23},
            {time:"2021-3-8",type:"l",value:0.24},
        ];
        var dataZ = [];
        var dataL = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            if(simulationData[i].type==="z"){
                dataZ.push(simulationData[i].value)
            }else {
                simulationData[i].value = (simulationData[i].value*100).toFixed(2);
                dataL.push(simulationData[i].value)
            }
            time.push(simulationData[i].time)
        }
        time = unique(time);

        o.tooltip.formatter =  '首日付费率：{c0}%<br /> 首日付费数：{c1}账户';
        o.title.text = "首日付费率";
        o.xAxis.data=time;
        o.legend.data = ["首日付费率","首日付费数"];
        o.series = [{
            name: '首日付费率',
            type: 'line',

            data: dataL
        }, {
            name: '首日付费数',
            type: 'bar',
            yAxisIndex: 1,
            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else if(np_pageControl === "rateW") {
        var simulationData = [
            {time:"2021-3-2",type:"z",value:2},
            {time:"2021-3-3",type:"z",value:5},
            {time:"2021-3-4",type:"z",value:5},
            {time:"2021-3-5",type:"z",value:4},
            {time:"2021-3-6",type:"z",value:2},
            {time:"2021-3-7",type:"z",value:6},
            {time:"2021-3-8",type:"z",value:8},
            {time:"2021-3-2",type:"l",value:0.02},
            {time:"2021-3-3",type:"l",value:0.03},
            {time:"2021-3-4",type:"l",value:0.04},
            {time:"2021-3-5",type:"l",value:0.03},
            {time:"2021-3-6",type:"l",value:0.02},
            {time:"2021-3-7",type:"l",value:0.04},
            {time:"2021-3-8",type:"l",value:0.05},
        ];
        var dataZ = [];
        var dataL = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            if(simulationData[i].type==="z"){
                dataZ.push(simulationData[i].value)
            }else {
                simulationData[i].value = (simulationData[i].value*100).toFixed(2);
                dataL.push(simulationData[i].value)
            }
            time.push(simulationData[i].time)
        }
        time = unique(time);

        o.tooltip.formatter =  '首周付费率：{c0}%<br /> 首周付费数：{c1}账户';
        o.title.text = "首周付费率";
        o.xAxis.data=time;
        o.legend.data = ["首周付费率","首周付费数"];
        o.series = [{
            name: '首周付费率',
            type: 'line',

            data: dataL
        }, {
            name: '首周付费数',
            type: 'bar',
            yAxisIndex: 1,
            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else {
        var simulationData = [
            {time:"2021-3-2",type:"z",value:55},
            {time:"2021-3-3",type:"z",value:57},
            {time:"2021-3-4",type:"z",value:57},
            {time:"2021-3-5",type:"z",value:53},
            {time:"2021-3-6",type:"z",value:56},
            {time:"2021-3-7",type:"z",value:56},
            {time:"2021-3-8",type:"z",value:55},
            {time:"2021-3-2",type:"l",value:0.02},
            {time:"2021-3-3",type:"l",value:0.03},
            {time:"2021-3-4",type:"l",value:0.04},
            {time:"2021-3-5",type:"l",value:0.03},
            {time:"2021-3-6",type:"l",value:0.02},
            {time:"2021-3-7",type:"l",value:0.04},
            {time:"2021-3-8",type:"l",value:0.05},
        ];
        var dataZ = [];
        var dataL = [];
        var time = [];
        simulationData = timeAscendingOrder(simulationData,"time");
        for(var i=0;i<simulationData.length;i++){
            if(simulationData[i].type==="z"){
                dataZ.push(simulationData[i].value)
            }else {
                simulationData[i].value = (simulationData[i].value*100).toFixed(2);
                dataL.push(simulationData[i].value)
            }
            time.push(simulationData[i].time)
        }
        time = unique(time);

        o.tooltip.formatter =  '首月付费率：{c0}%<br /> 首月付费数：{c1}账户';
        o.title.text = "首月付费率";
        o.xAxis.data=time;
        o.legend.data = ["首月付费率","首月付费数"];
        o.series = [{
            name: '首月付费率',
            type: 'line',

            data: dataL
        }, {
            name: '首月付费数',
            type: 'bar',
            yAxisIndex: 1,
            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }

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
function dp_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"首日",value:62},
        {time:"2~3日",value:78},
        {time:"4~7日",value:74},
        {time:"2周",value:60},
        {time:"3周",value:58},
        {time:"4周",value:54},
        {time:"5周",value:45},
        {time:"6周",value:34},
        {time:"7周",value:24},
        {time:"8周",value:15},
        {time:"9~12周",value:6},
        {time:">12周",value:1},
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

    o.title.text = "玩家首付周期";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
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
        {time:"1~10",value:684},
        {time:"11~20",value:421},
        {time:"21~30",value:456},
        {time:"31~40",value:245},
        {time:"41~50",value:452},
        {time:"51~60",value:254},
        {time:"61~90",value:120},
        {time:"91~120",value:78},
        {time:"121~150",value:75},
        {time:"151~160",value:25},
        {time:"161~190",value:21},
        {time:"190~200",value:1},
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

    o.title.text = "玩家首付等级";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
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
function am_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"<￥10",value:97},
        {time:"￥10~50",value:68},
        {time:"￥50~100",value:75},
        {time:"￥100~200",value:125},
        {time:"￥200~500",value:25},
        {time:"￥500~1000",value:0},
        {time:">￥1000",value:3},
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

    o.title.text = "玩家首付金额";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
            symbol:'pin',
            symbolRotate:-90
        }
    }];
    am_exportData = JSON.parse(JSON.stringify(o));
    am_HeavyLoad(o);

    $("#am_help").on("click",function () {
        if(am_helpControl === "show"){
            $("#am_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            am_helpControl = "hidden";
        }
        else {
            $("#am_helpTypeModality").attr("class","helpTypeModality");
            am_helpControl = "show";
        }
    });
    $("#am_download").on("click",function () {
        am_download()
    })
}
function gm_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    var simulationData = [
        {time:"金币",value:97},
        {time:"畅玩包",value:68},
        {time:"道具",value:75}
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

    o.title.text = "首付充值包类型";
    o.xAxis.type="value";
    o.yAxis.type="category";
    o.yAxis.data=time;
    o.legend.data = ["付费玩家"];
    o.series = [{
        name: '付费玩家',
        type: 'bar',
        data: value,
        markPoint:{
            data:pointArr,
            symbol:'circle'
        }
    }];
    gm_exportData = JSON.parse(JSON.stringify(o));
    gm_HeavyLoad(o);

    $("#gm_help").on("click",function () {
        if(gm_helpControl === "show"){
            $("#gm_helpTypeModality").attr("class","helpTypeModality theT_hidden");
            gm_helpControl = "hidden";
        }
        else {
            $("#gm_helpTypeModality").attr("class","helpTypeModality");
            gm_helpControl = "show";
        }
    });
    $("#gm_download").on("click",function () {
        gm_download()
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
    npChart.resize();
    dpChart.resize();
    dlChart.resize();
    amChart.resize();
    gmChart.resize();
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
function np_HeavyLoad(o) {
    var npChartDom = document.getElementById('np_chart');
    npChartDom.remove();
    $("#np").get(0).innerHTML += '<div id="np_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var npChartDom = document.getElementById('np_chart');
    npChart = echarts.init(npChartDom);
    npChart.setOption(o);
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
function am_HeavyLoad(o) {
    var amChartDom = document.getElementById('am_chart');
    amChartDom.remove();
    $("#am").get(0).innerHTML += '<div id="am_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var amChartDom = document.getElementById('am_chart');
    amChart = echarts.init(amChartDom);
    amChart.setOption(o);
}
function gm_HeavyLoad(o) {
    var gmChartDom = document.getElementById('gm_chart');
    gmChartDom.remove();
    $("#gm").get(0).innerHTML += '<div id="gm_chart" style="width: 100%;margin-left: auto;margin-right: auto;height: 500px;background-color: #fff;padding: 30px 20px"></div>';
    var gmChartDom = document.getElementById('gm_chart');
    gmChart = echarts.init(gmChartDom);
    gmChart.setOption(o);
}