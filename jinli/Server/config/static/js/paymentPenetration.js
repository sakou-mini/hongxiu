var pc_exportData ;
var np_exportData ;
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


function np_download() {

    if(np_pageControl === "arpuD"){
        var aoa = [
            ['日期', 'ARPU(日)'],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'ARPU(日).xlsx');
    }
    else if(np_pageControl === "arpuM"){
        var aoa = [
            ['日期', 'ARPU(月)'],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'ARPU(月).xlsx');
    }
    else if(np_pageControl === "arppuD"){
        var aoa = [
            ['日期', 'ARPPU(日)'],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'ARPPU(日).xlsx');
    }else {
        var aoa = [
            ['日期', 'ARPPU(月)'],
        ];
        for(var i = 0;i<np_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(np_exportData.xAxis.data[i]);
            newDataArr.push(np_exportData.series[0].data[i]);
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), 'ARPPU(月).xlsx');
    }
}

function pc_download(){
    if(pc_pageControl === "dailyRate"){
        var aoa = [
            ['日期', '日付费率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        console.log(aoa);
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '日付费率.xlsx');
    }
    else if(pc_pageControl === "weeklyRate"){
        var aoa = [
            ['日期', '周付费率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '周付费率.xlsx');
    }else {
        var aoa = [
            ['日期', '月付费率'],
        ];
        for(var i = 0;i<pc_exportData.xAxis.data.length;i++){
            var newDataArr = [];
            newDataArr.push(pc_exportData.xAxis.data[i]);
            newDataArr.push(pc_exportData.series[0].data[i]+"%");
            aoa.push(newDataArr)
        }
        var sheet = XLSX.utils.aoa_to_sheet(aoa);
        openDownloadDialog(sheet2blob(sheet), '月付费率.xlsx');
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
//页面控件
var pc_pageControl = "dailyRate";
var np_pageControl = "arpuD";

var pcChart;
var npChart;

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
};

dataRendering();
//渲染图标
function pc_dataRendering(){
    var o = JSON.parse(JSON.stringify(option));
    if(pc_pageControl === "dailyRate"){
        //    请求事件
        var simulationData = [
            {time:"2021-3-2",value:0.1537},
            {time:"2021-3-3",value:0.8544},
            {time:"2021-3-4",value:0.8437},
            {time:"2021-3-5",value:0.5437},
            {time:"2021-3-6",value:0.7687},
            {time:"2021-3-7",value:0.5478},
            {time:"2021-3-8",value:0.5478},
        ];
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value*100).toFixed(2));
        }
        o.tooltip.formatter =  '日付费率：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "日付费率";
        o.xAxis.data=time;
        o.legend.data = ["日付费率",];
        o.series = [{
            name: '日付费率',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)


    }else if(pc_pageControl === "weeklyRate") {
        var simulationData = [
            {time:"2021-3-2",value:0.1237},
            {time:"2021-3-3",value:0.1444},
            {time:"2021-3-4",value:0.2437},
            {time:"2021-3-5",value:0.2137},
            {time:"2021-3-6",value:0.2087},
            {time:"2021-3-7",value:0.1978},
            {time:"2021-3-8",value:0.2078},
        ];
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value*100).toFixed(2));
        }
        o.tooltip.formatter =  '周付费率：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "周付费率";
        o.xAxis.data=time;
        o.legend.data = ["周付费率",];
        o.series = [{
            name: '周付费率',
            type: 'line',
            data:value
        }];
        pc_exportData = JSON.parse(JSON.stringify(o));
        pc_HeavyLoad(o)

    }else {
        var simulationData = [
            {time:"2021-3-2",value:0.0537},
            {time:"2021-3-3",value:0.0544},
            {time:"2021-3-4",value:0.0437},
            {time:"2021-3-5",value:0.0437},
            {time:"2021-3-6",value:0.0687},
            {time:"2021-3-7",value:0.0478},
            {time:"2021-3-8",value:0.0478},
        ];
        var time = [];
        var value = [];
        for(var i=0;i<simulationData.length;i++){
            time.unshift(simulationData[i].time);
            value.unshift((simulationData[i].value*100).toFixed(2));
        }
        o.tooltip.formatter =  '月付费率：{c0}%';
        o.yAxis.axisLabel.formatter = function (value) {
            return value+'%'
        };
        o.title.text = "月付费率";
        o.xAxis.data=time;
        o.legend.data = ["月付费率",];
        o.series = [{
            name: '月付费率',
            type: 'line',
            data:value
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
                formatter: {}
            }
        },
        {
            type: 'value',
            axisLabel: {
                formatter:null
            }
        }
    ]


    if(np_pageControl === "arpuD"){
        //    请求事件
        var simulationData = [
            {time:"2021-3-2",type:"z",value:1.01},
            {time:"2021-3-3",type:"z",value:0.84},
            {time:"2021-3-4",type:"z",value:0.45},
            {time:"2021-3-5",type:"z",value:0.92},
            {time:"2021-3-6",type:"z",value:1.12},
            {time:"2021-3-7",type:"z",value:1.01},
            {time:"2021-3-8",type:"z",value:1.06}
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

        o.title.text = "ARPU(日)";
        o.xAxis.data=time;
        o.legend.data = ["ARPU值"];
        o.series = [ {
            name: 'ARPU值',
            type: 'line',

            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else if(np_pageControl === "arpuM") {
        var simulationData = [
            {time:"2021-3-2",type:"z",value:1.51},
            {time:"2021-3-3",type:"z",value:0.94},
            {time:"2021-3-4",type:"z",value:0.95},
            {time:"2021-3-5",type:"z",value:0.82},
            {time:"2021-3-6",type:"z",value:1.52},
            {time:"2021-3-7",type:"z",value:1.71},
            {time:"2021-3-8",type:"z",value:1.26}
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

        o.title.text = "ARPU(月)";
        o.xAxis.data=time;
        o.legend.data = ["ARPU值"];
        o.series = [ {
            name: 'ARPU值',
            type: 'line',

            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else if(np_pageControl === "arppuD"){
        var simulationData = [
            {time:"2021-3-2",type:"z",value:154.51},
            {time:"2021-3-3",type:"z",value:124.94},
            {time:"2021-3-4",type:"z",value:122.95},
            {time:"2021-3-5",type:"z",value:126.82},
            {time:"2021-3-6",type:"z",value:145.52},
            {time:"2021-3-7",type:"z",value:122.71},
            {time:"2021-3-8",type:"z",value:132.26}
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

        o.title.text = "ARPPU(日)";
        o.xAxis.data=time;
        o.legend.data = ["ARPPU值"];
        o.series = [ {
            name: 'ARPPU值',
            type: 'line',

            data: dataZ
        }];
        np_exportData = JSON.parse(JSON.stringify(o));
        np_HeavyLoad(o)
    }else {
        var simulationData = [
            {time:"2021-3-2",type:"z",value:112.51},
            {time:"2021-3-3",type:"z",value:124.94},
            {time:"2021-3-4",type:"z",value:154.95},
            {time:"2021-3-5",type:"z",value:102.82},
            {time:"2021-3-6",type:"z",value:106.52},
            {time:"2021-3-7",type:"z",value:111.71},
            {time:"2021-3-8",type:"z",value:125.26}
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

        o.title.text = "ARPPU(月)";
        o.xAxis.data=time;
        o.legend.data = ["ARPPU值"];
        o.series = [ {
            name: 'ARPPU值',
            type: 'line',

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